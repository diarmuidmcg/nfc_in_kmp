import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.CoreNFC.NFCNDEFTagProtocol
import platform.CoreNFC.wellKnownTypeTextPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithString
import platform.CoreNFC.wellKnownTypeURIPayloadWithURL
import platform.Foundation.NSError
import platform.Foundation.NSFeatureUnsupportedError
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.darwin.NSObject

typealias ShouldStopExecuting = Boolean

class NFCWriterSession(
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
            session.connectToTag(tag) { connectError ->
                if (nfcSessionDelegate.errorShouldStopProgram(connectError, session))  return@connectToTag
                tag.queryNDEFStatusWithCompletionHandler { status, _, queryError ->
                    if (nfcSessionDelegate.errorShouldStopProgram(queryError, session)) return@queryNDEFStatusWithCompletionHandler
                    /* For NFCNDEF Status
                    case notSupported = 1
                    case readWrite = 2
                    case readOnly = 3
                     */
                    if (status.toInt() == 2) {
                        val payload = message?.let { msg -> NFCNDEFPayload.wellKnownTypeTextPayloadWithString(msg, locale = NSLocale(locale)) }
                            ?: url?.let { url -> NFCNDEFPayload.wellKnownTypeURIPayloadWithURL(NSURL(string = url)) }
                            ?: uri?.let { uri -> NFCNDEFPayload.wellKnownTypeURIPayloadWithString(uri) }

                        val messageToBePut = NFCNDEFMessage(nDEFRecords = listOf(payload))
                        tag.writeNDEF(messageToBePut) { readError ->
                            if (nfcSessionDelegate.errorShouldStopProgram(readError, session))  return@writeNDEF
                            else {
                                val returnPayload = messageToBePut.records.fold("") { accumulatedValue, record ->
                                    val payload = record as? NFCNDEFPayload  // Safely cast each record to NFCNDEFPayload
                                    accumulatedValue + (payload?.payload ?: "")
                                }

                                val returnRecord = NFCRecordKMP(
                                    identifier = null, // unfortunately we have no way of getting UUID
                                    payload = returnPayload,
                                    type = tag::class,
                                    isLocked = tag.isAvailable()
                                )
                                completionHandler(returnRecord, null)
                                session.invalidateSession()
                                return@writeNDEF
                            }
                        }
                    } else {
                        if (status.toInt() == 1) nfcSessionDelegate.errorShouldStopProgram(NSError("Tag not supported", NSFeatureUnsupportedError, null), session)
                        else nfcSessionDelegate.errorShouldStopProgram(NSError("Tag is locked", NSFeatureUnsupportedError, null), session)
                    }
                }
            }
        } else completionHandler.invoke(null, NFCErrorKMP("", "No Tags found"))
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        nfcSessionDelegate.errorShouldStopProgram(didInvalidateWithError, session)
    }
}