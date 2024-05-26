import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCTagProtocol
import platform.CoreNFC.NFCTagReaderSession
import platform.CoreNFC.NFCTagReaderSessionDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

internal class NFCTagReaderSessionKMP(
    customErrorMessage: String?,
    completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
):  NSObject(), NFCTagReaderSessionDelegateProtocol {

    private val nfcSessionDelegate = NFCSessionDelegate(customErrorMessage, completionHandler)

    override fun tagReaderSessionDidBecomeActive(session: NFCTagReaderSession) {}

    override fun tagReaderSession(session: NFCTagReaderSession, didInvalidateWithError: NSError) {
        nfcSessionDelegate.errorShouldStopProgram(didInvalidateWithError, session)    }

    override fun tagReaderSession(session: NFCTagReaderSession, didDetectTags: List<*>) {
        val firstTag = didDetectTags.firstOrNull()

        if (firstTag != null) {
            val tag = firstTag as NFCTagProtocol
            session.connectToTag(tag) { connectError ->
                if (nfcSessionDelegate.errorShouldStopProgram   (connectError, session))  return@connectToTag
                tag.asNFCMiFareTag()?.let { nfcSessionDelegate.setMifareData(it, session) }
                tag.asNFCISO15693Tag()?.let { nfcSessionDelegate.setISO15693(it, session) }
                tag.asNFCISO7816Tag()?.let { nfcSessionDelegate.setISO7816(it, session) }
                tag.asNFCFeliCaTag()?.let {nfcSessionDelegate.setFeliCa(it, session) }
                nfcSessionDelegate.callCompletionHandler(null, session)
            }
        } else nfcSessionDelegate.callCompletionHandler(NFCErrorKMP("", "No Tags found"), session)
    }
}