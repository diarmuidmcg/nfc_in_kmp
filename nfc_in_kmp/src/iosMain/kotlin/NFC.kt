import model.NFCRecord
import model.NFCTag
import platform.CoreNFC.NFCMiFareFamily
import platform.CoreNFC.NFCMiFareTagProtocol
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.CoreNFC.NFCNDEFTagProtocol
import platform.CoreNFC.NFCTagReaderSession
import platform.CoreNFC.NFCTagReaderSessionDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.DISPATCH_QUEUE_CONCURRENT
import platform.darwin.NSObject
import kotlin.native.internal.test.main
import kotlin.reflect.typeOf

actual class NFCInteraction {
    var session: NFCNDEFReaderSession? = null

    actual suspend fun startReadSession(
        typeOfNFCTag: NFCTag,
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecord? {
        var returnNFCRecord: NFCRecord? = null

            session = NFCNDEFReaderSession(NFCSession(typeOfNFCTag, customAlertMessage) { success, mTag ->
            if (success && mTag != null) {
                returnNFCRecord = NFCRecord("test", mTag)
//                actionDone.invoke()
            } else
                println("something went wrong")
//                readingInterface.error("no connection to nfc tag..")
        }, DISPATCH_QUEUE_CONCURRENT, true)
        session?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
        return returnNFCRecord
    }

    // for example typeOfNFCTag = NFCMiFareTagProtocol

    actual suspend fun startWriteSession(
        typeOfNFCTag: NFCTag,
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecord? {
        TODO("Not yet implemented")
    }
    class NFCSession(
        private val typeOfNFCTag: NFCTag,
        private val customErrorMessage: String?,
        private val completionHandler: (success: Boolean, tag: NFCTag?) -> Unit
    ) : NSObject(), NFCNDEFReaderSessionDelegateProtocol {

        override fun readerSessionDidBecomeActive(session: NFCNDEFReaderSession) {
//            super.readerSessionDidBecomeActive(session)
        }

        override fun readerSession(session: NFCNDEFReaderSession, didDetectNDEFs: List<*>) {

            val type = typeOfNFCTag::class
            val firstNDEF = didDetectNDEFs.firstOrNull()

            if (firstNDEF != null && type.isInstance(firstNDEF)) {
                val tag = firstNDEF as? NFCTag
                // Now 'tag' is safely cast to NFCTag or null if the cast isn't possible
                if (tag == null)
                    completionHandler.invoke(false, null)
                else {
                    val tagImplementsProtocol = tag as NFCNDEFTagProtocol
                    session.connectToTag(tagImplementsProtocol) {
                        completionHandler.invoke(it != null, tag)
                    }
                }
            }

        }

        override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
            customErrorMessage?.let { session.invalidateSessionWithErrorMessage(it) }
                ?: session.invalidateSession()
        }
    }
}

