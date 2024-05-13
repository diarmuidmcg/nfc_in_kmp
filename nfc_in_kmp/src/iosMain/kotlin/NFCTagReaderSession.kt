import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCTagProtocol
import platform.CoreNFC.NFCTagReaderSession
import platform.CoreNFC.NFCTagReaderSessionDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject
import utils.toByteArray

internal class NFCTagReaderSession(
    customErrorMessage: String?,
    private val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
) :  NSObject(), NFCTagReaderSessionDelegateProtocol {

    private val nfcSessionDelegate = NFCSession(customErrorMessage, completionHandler)

    override fun tagReaderSession(session: NFCTagReaderSession, didInvalidateWithError: NSError) {
        nfcSessionDelegate.errorShouldStopProgram(didInvalidateWithError, session)    }

    override fun tagReaderSession(session: NFCTagReaderSession, didDetectTags: List<*>) {
        println("reader sesion detected")
        val firstTag = didDetectTags.firstOrNull()

        if (firstTag != null) {
            val tag = firstTag as NFCTagProtocol
            session.connectToTag(tag) { connectError ->
                if (nfcSessionDelegate.errorShouldStopProgram   (connectError, session))  return@connectToTag
                tag.asNFCMiFareTag()?.let {
                    nfcSessionDelegate.getPayloadAndUpdateRecord(it)
                    val miFareFamily: String =
                        when(it.mifareFamily.toInt()){
                            1 -> "Unknown"
                            2 -> "Ultralight"
                            3 -> "Plus"
                            4 -> "Desfire"
                            else -> ""
                        }
                    val returnRecord = NFCRecordKMP(
                        identifier = it.identifier.toByteArray().decodeToString(),
                        type = miFareFamily,
                        isLocked = it.isAvailable()
                    )
                    println("NFC:  is asNFCMiFareTag")
                    println("NFC: identifier is ${returnRecord.identifier}")
                    println("NFC: type is ${it.type}")
                    println("NFC: mifareFamily is $miFareFamily")
                    nfcSessionDelegate.updateCurrentRecord(returnRecord)
                }
                tag.asNFCISO15693Tag()?.let {
                    nfcSessionDelegate.getPayloadAndUpdateRecord(it)
                    val returnRecord = NFCRecordKMP(
                        identifier = it.identifier.toByteArray().decodeToString(),
                        type = it.type,
                        isLocked = it.isAvailable()
                    )

                    println("NFC: is asNFCISO15693Tag")
                    println("NFC: icSerialNumber is ${it.icSerialNumber.toByteArray().decodeToString()}")
                    println("NFC: identifier is ${returnRecord.identifier}")
                    println("NFC: type is ${it.type}")
                    println("NFC: icManufacturerCode is ${it.icManufacturerCode}")
                    nfcSessionDelegate.updateCurrentRecord(returnRecord)
                }
                tag.asNFCISO7816Tag()?.let {
                    nfcSessionDelegate.getPayloadAndUpdateRecord(it)
                    val returnRecord = NFCRecordKMP(
                        identifier = it.identifier.toByteArray().decodeToString(),
                        type = it.type,
                        isLocked = it.isAvailable()
                    )

                    println("NFC: is asNFCISO15693Tag")
                    println("NFC: applicationData is ${it.applicationData?.toByteArray()?.decodeToString()}")
                    println("NFC: identifier is ${returnRecord.identifier}")
                    println("NFC: type is ${it.type}")
                    println("NFC: initialSelectedAID is ${it.initialSelectedAID}")
                    println("NFC: proprietaryApplicationDataCoding is ${it.proprietaryApplicationDataCoding}")
                    nfcSessionDelegate.updateCurrentRecord(returnRecord)
                }
                tag.asNFCFeliCaTag()?.let {
                    nfcSessionDelegate.getPayloadAndUpdateRecord(it)
                    val returnRecord = NFCRecordKMP(
                        identifier = it.currentIDm.toByteArray().decodeToString(),
                        type = it.type,
                        isLocked = it.isAvailable()
                    )

                    println("NFC: is asNFCISO15693Tag")
                    println("NFC: applicationData is ${it.currentSystemCode.toByteArray().decodeToString()}")
                    println("NFC: identifier is ${returnRecord.identifier}")
                    println("NFC: type is ${it.type}")
                    nfcSessionDelegate.updateCurrentRecord(returnRecord)
//                    val messageToSend = nfcSessionDelegate.createNDEFMessage(null,null,null,"en")
//                    nfcSessionDelegate.writeToTag(session, it, messageToSend)
                }
            }
            session.invalidateSession()
        } else completionHandler.invoke(null, NFCErrorKMP("", "No Tags found"))
    }
}