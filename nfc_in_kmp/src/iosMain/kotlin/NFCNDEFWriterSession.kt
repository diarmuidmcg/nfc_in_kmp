import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCFeliCaTagProtocol
import platform.CoreNFC.NFCISO15693TagProtocol
import platform.CoreNFC.NFCISO7816TagProtocol
import platform.CoreNFC.NFCMiFareTagProtocol
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.CoreNFC.NFCNDEFTagProtocol
import platform.CoreNFC.NFCTagTypeMiFare
import platform.CoreNFC.wellKnownTypeTextPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithURL
import platform.Foundation.NSError
import platform.Foundation.NSFeatureUnsupportedError
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.darwin.NSObject

typealias ShouldStopExecuting = Boolean

class NFCNDEFWriterSession(
    private val message: String?,
    private val url: String?,
    private val uri: String?,
    private val locale: String,
    customErrorMessage: String?,
    private val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
) : NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    private val nfcSessionDelegate = NFCSession(customErrorMessage, completionHandler)

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
            val messageToSend = nfcSessionDelegate.createNDEFMessage(message, url, uri, locale)
            session.connectToTag(tag) { connectError ->
                if (nfcSessionDelegate.errorShouldStopProgram(connectError, session)) return@connectToTag
                nfcSessionDelegate.writeToTag(session, tag, messageToSend)
            }
        } else completionHandler.invoke(null, NFCErrorKMP("", "No Tags found"))
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        nfcSessionDelegate.errorShouldStopProgram(didInvalidateWithError, session)
    }
}