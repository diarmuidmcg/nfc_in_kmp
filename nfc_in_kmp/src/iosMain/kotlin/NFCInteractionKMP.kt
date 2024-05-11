import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFReaderSession

actual class NFCInteractionKMP {
    var session: NFCNDEFReaderSession? = null

    actual suspend fun startReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecordKMP? {
        var returnNFCRecordKMP: NFCRecordKMP? = null
        session = NFCNDEFReaderSession(NFCReaderSession(customAlertMessage) { success, mTag ->
            if (success && mTag != null) {
                returnNFCRecordKMP = mTag
                session?.invalidateSession()
            }
        }, null, true)
        session?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
        return returnNFCRecordKMP
    }

    actual suspend fun startWriteSession(
        message: String,
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecordKMP? {
        var returnNFCRecordKMP: NFCRecordKMP? = null
        session = NFCNDEFReaderSession(NFCWriterSession(message, customAlertMessage) { error ->
            error?.let {
                // tell use
            } ?: // tell use success
                session?.invalidateSession()

        }, null, true)
        session?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
        return returnNFCRecordKMP
    }
}