import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFTagProtocol
import platform.CoreNFC.NFCReaderSession
import platform.CoreNFC.wellKnownTypeTextPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithURL
import platform.Foundation.NSError
import platform.Foundation.NSFeatureUnsupportedError
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import utils.toByteArray

// I would have liked to create an abstract class for all my NFCSessions to inherit, but
// Classes cannot inherit from Objective C AND Kotlin classes/interfaces
// Therefore, I just create an instance of NFCSession on all my Super NFCSessions
interface NFCSessionProtocol {
    val customErrorMessage: String?
    val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    fun errorShouldStopProgram(connectError: NSError?, session: NFCReaderSession): ShouldStopExecuting
}

class NFCSession(
    override val customErrorMessage: String?,
    override val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
) : NFCSessionProtocol {
    private var  currentRecord: NFCRecordKMP? = null

    // this should be updated with threading
    fun updateCurrentRecord(newNFCRecordKMP: NFCRecordKMP) {
//        currentRecord = currentRecord.copy(
//            identifier = newNFCRecordKMP.identifier ?: currentRecord.identifier,
//            type = newNFCRecordKMP.type ?: currentRecord.type,
//            payload = newNFCRecordKMP.payload ?: currentRecord.payload,
//            isLocked = newNFCRecordKMP.isLocked ?: currentRecord.isLocked
//        )
    }


    override fun errorShouldStopProgram(
        connectError: NSError?,
        session: NFCReaderSession
    ): ShouldStopExecuting {
        connectError?.let {
            customErrorMessage?.let { errMsg ->
                session.invalidateSessionWithErrorMessage(errMsg)
            } ?: session.invalidateSession()
            val errorDetails = NFCErrorKMP(
                connectError.code.toString(),
                connectError.localizedDescription,
                connectError.localizedFailureReason,
                connectError.localizedRecoverySuggestion
            )
            completionHandler(currentRecord, errorDetails)
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

    fun getPayloadAndUpdateRecord(tag: NFCNDEFTagProtocol) {
        tag.readNDEFWithCompletionHandler { ndefMessage, readError ->
            ndefMessage?.let { message ->
                val firstRecord: NFCNDEFPayload =
                    message?.records?.first() as NFCNDEFPayload
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

}
