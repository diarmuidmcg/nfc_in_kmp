import model.NFCErrorKMP
import model.NFCRecordKMP
import model.NFCResult
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject
import utils.toByteArray

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
internal class NFCNDEFReaderSession(
    customErrorMessage: String?,
//    private val completionHandler: (NFCResultType<NFCRecordKMP, NFCErrorKMP>) -> Unit
    private val completionHandler: (NFCResult) -> Unit
) :  NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    private val nfcSessionDelegate = NFCSessionDelegate(customErrorMessage, completionHandler)

    override fun readerSessionDidBecomeActive(session: NFCNDEFReaderSession) {}

    override fun readerSession(session: NFCNDEFReaderSession, didDetectNDEFs: List<*>) {
        val firstNDEF = didDetectNDEFs.firstOrNull()

        if (firstNDEF != null) {
            val tag = firstNDEF as NFCNDEFMessage
            val firstRecord: NFCNDEFPayload = tag.records.first() as NFCNDEFPayload

            val returnRecord = NFCRecordKMP(
                payload = if(firstRecord.payload.length.toInt() > 0) firstRecord.payload.toByteArray().decodeToString() else "",
            )
            println("NFC: payload is ${returnRecord.payload}")
            nfcSessionDelegate.updateCurrentRecord(returnRecord)
            nfcSessionDelegate.callCompletionHandler(null, session)
        } else nfcSessionDelegate.callCompletionHandler(NFCErrorKMP("", "No Tags found"), session)
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        nfcSessionDelegate.errorShouldStopProgram(didInvalidateWithError, session)
    }
}