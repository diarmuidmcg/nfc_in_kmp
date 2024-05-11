import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.CoreNFC.NFCNDEFTagProtocol
import platform.CoreNFC.wellKnownTypeTextPayloadWithString
import platform.Foundation.NSError
import platform.Foundation.NSLocale
import platform.darwin.NSObject

class NFCWriterSession(
    private val message: String,
    private val customErrorMessage: String?,
    private val completionHandler: (error: NSError?) -> Unit
) : NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    private fun handleNFCError(
        connectError: NSError?,
        session: NFCNDEFReaderSession,
        completionHandler: () -> Unit
    ) {
        connectError?.let {
            completionHandler(it)
            customErrorMessage?.let { errMsg ->
                session.invalidateSessionWithErrorMessage(errMsg)
            } ?: session.invalidateSession()
            completionHandler()
        }
    }

    override fun readerSessionDidBecomeActive(session: NFCNDEFReaderSession) {
        println("readerSessionDidBecomeActive")
    }

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
            session.connectToTag(tag) { connectError ->
                handleNFCError(connectError, session) { return@handleNFCError }
                tag.queryNDEFStatusWithCompletionHandler { status, _, queryError ->
                    handleNFCError(queryError, session) { return@handleNFCError }
                    /* For NFCNDEF Status
                    case notSupported = 1
                    case readWrite = 2
                    case readOnly = 3
                     */
                    if (status.toInt() == 2) {
                        val payload = NFCNDEFPayload.wellKnownTypeTextPayloadWithString(message, locale = NSLocale("en_US"))
                        val messageToBePut = NFCNDEFMessage(nDEFRecords = listOf(payload))
                        if (messageToBePut != null) {
                            tag.writeNDEF(messageToBePut) { readError ->
                                handleNFCError(readError, session) { return@handleNFCError }
                                session.invalidateSession()
                            }
                        }
                    } else {
                        if (status.toInt() == 1) handleNFCError(NSError("Tag not supported", 43, null), session) { return@handleNFCError }
                       else handleNFCError(NSError("Tag is locked", 45, null), session) { return@handleNFCError }
                    }
                }
            }
        }
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        if(didInvalidateWithError.underlyingErrors.isNotEmpty()) {
            println("NFC invalidated with error!")
            for (error in didInvalidateWithError.underlyingErrors) {
                println("error is ${error.toString()}")
            }
            customErrorMessage?.let { session.invalidateSessionWithErrorMessage(it) }
                ?: session.invalidateSession()
        } else println("NFC finished successfully")
    }
}