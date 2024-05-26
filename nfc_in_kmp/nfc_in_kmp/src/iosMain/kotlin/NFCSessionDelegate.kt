import model.NFCErrorKMP
import model.NFCRecordKMP
import model.NFCWriteMessageKMP
import platform.CoreNFC.NFCFeliCaTagProtocol
import platform.CoreNFC.NFCISO15693TagProtocol
import platform.CoreNFC.NFCISO7816TagProtocol
import platform.CoreNFC.NFCMiFareTagProtocol
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFTagProtocol
import platform.CoreNFC.NFCReaderSession
import platform.CoreNFC.wellKnownTypeTextPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithURL
import platform.Foundation.NSError
import platform.Foundation.NSFeatureUnsupportedError
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.Foundation.base64Encoding
import utils.toByteArray

// I would have liked to create an abstract class for all my NFCSessions to inherit, but
// Classes cannot inherit from Objective C AND Kotlin classes/interfaces
// Therefore, I just create an instance of NFCSession on all my Super NFCSessions
interface NFCSessionDelegateProtocol {
    val customErrorMessage: String?
    val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    fun updateCurrentRecord(newNFCRecordKMP: NFCRecordKMP)
    fun callCompletionHandler(error: NFCErrorKMP?, session: NFCReaderSession)
    fun errorShouldStopProgram(connectError: NSError?, session: NFCReaderSession): ShouldStopExecuting
}

class NFCSessionDelegate(
    override val customErrorMessage: String?,
    override val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
) : NFCSessionDelegateProtocol {
    private var  currentRecord: NFCRecordKMP? = null

    // this should be updated with threading
    override fun updateCurrentRecord(newNFCRecordKMP: NFCRecordKMP) {
//        currentRecord = currentRecord.copy(
//            identifier = newNFCRecordKMP.identifier ?: currentRecord.identifier,
//            type = newNFCRecordKMP.type ?: currentRecord.type,
//            payload = newNFCRecordKMP.payload ?: currentRecord.payload,
//            isLocked = newNFCRecordKMP.isLocked ?: currentRecord.isLocked
//        )
    }

    override fun callCompletionHandler(error: NFCErrorKMP?, session: NFCReaderSession) {
        completionHandler.invoke(currentRecord, error)
        session.invalidateSession()
    }


    override fun errorShouldStopProgram(
        connectError: NSError?,
        session: NFCReaderSession
    ): ShouldStopExecuting {
        connectError?.let {
            println("there is error")
            println("error is ${it.localizedDescription}")
            customErrorMessage?.let { errMsg ->
                session.invalidateSessionWithErrorMessage(errMsg)
            } ?: session.invalidateSession()
            val errorDetails = NFCErrorKMP(
                it.code.toString(),
                it.localizedDescription,
                it.localizedFailureReason,
                it.localizedRecoverySuggestion
            )
            callCompletionHandler(errorDetails, session)
            return true
        } ?: return false
    }

    fun writeToTag(session: NFCReaderSession, tag: NFCNDEFTagProtocol, message: NFCNDEFMessage) {

        tag.queryNDEFStatusWithCompletionHandler { status, _, queryError ->
            if (errorShouldStopProgram(
                    queryError,
                    session
                )
            ) return@queryNDEFStatusWithCompletionHandler
            /* For NFCNDEF Status
                case notSupported = 1
                case readWrite = 2
                case readOnly = 3
                 */
            if (status.toInt() == 2) {
                tag.writeNDEF(message) { readError ->
                    if (errorShouldStopProgram(readError, session)) return@writeNDEF
                    else {
                        val returnPayload =
                            message.records.fold("") { accumulatedValue, record ->
                                val ndefPayload =
                                    record as? NFCNDEFPayload  // Safely cast each record to NFCNDEFPayload
                                accumulatedValue + (ndefPayload?.payload ?: "")
                            }

                        val returnRecord = NFCRecordKMP(
                            identifier = null, // unfortunately we have no way of getting UUID
                            payload = returnPayload,
                            type = tag::class,
                            isLocked = tag.isAvailable()
                        )
                        updateCurrentRecord(returnRecord)
                        session.invalidateSession()
                    }
                }
            } else {
                if (status.toInt() == 1) errorShouldStopProgram(
                    NSError(
                        "Tag not supported",
                        NSFeatureUnsupportedError,
                        null
                    ), session
                )
                else errorShouldStopProgram(
                    NSError(
                        "Tag is locked",
                        NSFeatureUnsupportedError,
                        null
                    ), session
                )
            }
        }
    }

    private fun getPayloadAndUpdateRecord(tag: NFCNDEFTagProtocol, session: NFCReaderSession) {
        tag.readNDEFWithCompletionHandler { ndefMessage, readError ->
            errorShouldStopProgram(readError, session)
            ndefMessage?.let { message ->
                val firstRecord: NFCNDEFPayload =
                    message.records.first() as NFCNDEFPayload
                val returnRecord = NFCRecordKMP(
                    payload = if (firstRecord.payload.length.toInt() > 0) firstRecord.payload.toByteArray()
                        .decodeToString() else "",
                )
                println("NFC: payload is ${returnRecord.payload}")
                updateCurrentRecord(returnRecord)
            }
        }
    }

    fun createNDEFMessage(message: String?, url: String?, uri: String?, locale: String): NFCNDEFMessage {
        val payload = message?.let { msg -> NFCNDEFPayload.wellKnownTypeTextPayloadWithString(msg, locale = NSLocale(locale)) }
            ?: url?.let { url -> NFCNDEFPayload.wellKnownTypeURIPayloadWithURL(NSURL(string = url)) }
            ?: uri?.let { uri -> NFCNDEFPayload.wellKnownTypeURIPayloadWithString(uri) }

        return NFCNDEFMessage(nDEFRecords = listOf(payload))
    }

    fun createNDEFMessage(message: NFCWriteMessageKMP): NFCNDEFMessage {
        val payload = message.textMessage?.let { msg -> NFCNDEFPayload.wellKnownTypeTextPayloadWithString(msg, locale = NSLocale(message.locale)) }
            ?: message.url?.let { url -> NFCNDEFPayload.wellKnownTypeURIPayloadWithURL(NSURL(string = url)) }
            ?: message.uri?.let { uri -> NFCNDEFPayload.wellKnownTypeURIPayloadWithString(uri) }

        return NFCNDEFMessage(nDEFRecords = listOf(payload))
    }

    internal fun setMifareData(tag: NFCMiFareTagProtocol, session: NFCReaderSession) {
        getPayloadAndUpdateRecord(tag, session)
        val miFareFamily: String =
            when(tag.mifareFamily.toInt()){
                1 -> "Unknown"
                2 -> "Ultralight"
                3 -> "Plus"
                4 -> "Desfire"
                else -> ""
            }
        tag.identifier.toByteArray().decodeToString()
        val returnRecord = NFCRecordKMP(
            identifier = tag.identifier.toByteArray().decodeToString(),
            type = miFareFamily,
            isLocked = tag.isAvailable()
        )
        println("NFC:  is asNFCMiFareTag")
        println("NFC: identifier is ${tag.identifier}")
        println("NFC: identifier byte is ${tag.identifier.toByteArray()}")
        println("NFC: identifier decoded is ${returnRecord.identifier}")
        println("NFC: type is ${tag.type}")
        println("NFC: mifareFamily is $miFareFamily")
        updateCurrentRecord(returnRecord)
    }

    internal fun setISO15693(tag: NFCISO15693TagProtocol, session: NFCReaderSession) {
        getPayloadAndUpdateRecord(tag, session)
        val returnRecord = NFCRecordKMP(
            identifier = tag.identifier.toByteArray().decodeToString(),
            type = tag.type,
            isLocked = tag.isAvailable()
        )

        println("NFC: is asNFCISO15693Tag")
        println("NFC: icSerialNumber is ${tag.icSerialNumber.toByteArray().decodeToString()}")
        println("NFC: identifier is ${returnRecord.identifier}")
        println("NFC: type is ${tag.type}")
        println("NFC: icManufacturerCode is ${tag.icManufacturerCode}")
        updateCurrentRecord(returnRecord)
    }

    internal fun setISO7816(tag: NFCISO7816TagProtocol, session: NFCReaderSession) {
        getPayloadAndUpdateRecord(tag, session)
        val returnRecord = NFCRecordKMP(
            identifier = tag.identifier.toByteArray().decodeToString(),
            type = tag.type,
            isLocked = tag.isAvailable()
        )

        println("NFC: is asNFCISO15693Tag")
        println("NFC: applicationData is ${tag.applicationData?.toByteArray()?.decodeToString()}")
        println("NFC: identifier is ${returnRecord.identifier}")
        println("NFC: type is ${tag.type}")
        println("NFC: initialSelectedAID is ${tag.initialSelectedAID}")
        println("NFC: proprietaryApplicationDataCoding is ${tag.proprietaryApplicationDataCoding}")
        updateCurrentRecord(returnRecord)
    }

    internal fun setFeliCa(tag: NFCFeliCaTagProtocol, session: NFCReaderSession) {
        getPayloadAndUpdateRecord(tag, session)
        val returnRecord = NFCRecordKMP(
            identifier = tag.currentIDm.toByteArray().decodeToString(),
            type = tag.type,
            isLocked = tag.isAvailable()
        )

        println("NFC: is asNFCISO15693Tag")
        println("NFC: applicationData is ${tag.currentSystemCode.toByteArray().decodeToString()}")
        println("NFC: identifier is ${returnRecord.identifier}")
        println("NFC: type is ${tag.type}")
        updateCurrentRecord(returnRecord)
    }
}
