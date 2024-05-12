import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject
import utils.toByteArray

internal class NFCReaderSession(
    customErrorMessage: String?,
    private val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
) :  NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    private val nfcSessionDelegate = NFCSession(customErrorMessage, completionHandler)

    override fun readerSessionDidBecomeActive(session: NFCNDEFReaderSession) {}

    override fun readerSession(session: NFCNDEFReaderSession, didDetectNDEFs: List<*>) {
        println("reader sesion detected")
        val firstNDEF = didDetectNDEFs.firstOrNull()

        if (firstNDEF != null) {
            val tag = firstNDEF as NFCNDEFMessage
            val firstRecord: NFCNDEFPayload = tag.records?.first() as NFCNDEFPayload

            val returnTag = NFCRecordKMP(
                identifier = if(firstRecord.identifier.length.toInt() > 0) firstRecord.identifier.toByteArray().decodeToString() else "",
                payload = if(firstRecord.payload.length.toInt() > 0) firstRecord.payload.toByteArray().decodeToString() else "",
                type = if(firstRecord.type.length.toInt() > 0) firstRecord.type.toByteArray().decodeToString() else "",
                isLocked = null
            )
            println("NFC: payload is ${returnTag.payload}")
            println("NFC: identifier is ${returnTag.identifier}")
            println("NFC: type is ${returnTag.type}")
            completionHandler.invoke(returnTag, null)
            return
        } else completionHandler.invoke(null, NFCErrorKMP("", "No Tags found"))
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        nfcSessionDelegate.errorShouldStopProgram(didInvalidateWithError, session)
    }
}