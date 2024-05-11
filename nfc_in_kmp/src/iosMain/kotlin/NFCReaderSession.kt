import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFMessage
import platform.CoreNFC.NFCNDEFPayload
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCNDEFReaderSessionDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject
import utils.toByteArray

internal class NFCReaderSession(
    private val customErrorMessage: String?,
    private val completionHandler: (success: Boolean, record: NFCRecordKMP?) -> Unit
) : NSObject(), NFCNDEFReaderSessionDelegateProtocol {

    override fun readerSessionDidBecomeActive(session: NFCNDEFReaderSession) {
        println("readerSessionDidBecomeActive")
    }

    override fun readerSession(session: NFCNDEFReaderSession, didDetectNDEFs: List<*>) {
        println("reader sesion detected")
        val firstNDEF = didDetectNDEFs.firstOrNull()

        if (firstNDEF != null) {
            val tag = firstNDEF as NFCNDEFMessage
            val firstRecord: NFCNDEFPayload = tag.records?.first() as NFCNDEFPayload

            val returnTag = NFCRecordKMP(
                identifier = if(firstRecord.identifier.length.toInt() > 0) firstRecord.identifier.toByteArray().decodeToString() else "",
                payload = if(firstRecord.payload.length.toInt() > 0) firstRecord.payload.toByteArray().decodeToString() else "",
                type = if(firstRecord.type.length.toInt() > 0) firstRecord.type.toByteArray().decodeToString() else ""
            )
            println("NFC: payload is ${returnTag.payload}")
            println("NFC: identifier is ${returnTag.identifier}")
            println("NFC: type is ${returnTag.type}")
            completionHandler.invoke(true, returnTag)
        } else completionHandler.invoke(false, null)
    }

    override fun readerSession(session: NFCNDEFReaderSession, didInvalidateWithError: NSError) {
        if(didInvalidateWithError.underlyingErrors.isNotEmpty()) {
            println("NFC invalidated with error!")
            for (error in didInvalidateWithError.underlyingErrors) {
                println("error is ${error.toString()}")
            }
            customErrorMessage?.let { session.invalidateSessionWithErrorMessage(it) }
                ?: session.invalidateSession()
        } else  println("NFC finished successfully")
    }
}