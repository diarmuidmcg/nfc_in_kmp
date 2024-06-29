import model.NFCErrorKMP
import model.NFCResult
import model.NFCWriteMessageKMP
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.CoreNFC.NFCNDEFTagProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

typealias ShouldStopExecuting = Boolean

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class NFCNDEFWriterSession(
    private val message: NFCWriteMessageKMP,
    customErrorMessage: String?,
    completionHandler: (NFCResult) -> Unit
): NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    private val nfcSessionDelegate = NFCSessionDelegate(customErrorMessage, completionHandler)

    override fun readerSessionDidBecomeActive(session: NFCNDEFReaderSession) {}

    /*
    I add the Suppression due to a problem with the type inference from the Objective C translation.
    Although didDetectNDEFs & didDetectTags return lists of two separate objects, the translation
    returns a wildcard, leading Kotlin to believe that they are duplicate functions (as parameter names are ignored)
     */
    @Suppress("CONFLICTING_OVERLOADS")
    override fun readerSession(session: NFCNDEFReaderSession, didDetectNDEFs: List<*>) {}

    @Suppress("CONFLICTING_OVERLOADS")
    override fun readerSession(session: NFCNDEFReaderSession, didDetectTags: List<*>) {
        val firstTag = didDetectTags.firstOrNull()

        if (firstTag != null) {
            val tag: NFCNDEFTagProtocol = firstTag as NFCNDEFTagProtocol
            val messageToSend = nfcSessionDelegate.createNDEFMessage(message)
            session.connectToTag(tag) { connectError ->
                if (nfcSessionDelegate.errorShouldStopProgram(connectError, session)) return@connectToTag
                nfcSessionDelegate.writeToTag(session, tag, messageToSend)
            }
        } else nfcSessionDelegate.callCompletionHandler(NFCErrorKMP("", "No Tags found"), session)
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        nfcSessionDelegate.errorShouldStopProgram(didInvalidateWithError, session)
    }
}