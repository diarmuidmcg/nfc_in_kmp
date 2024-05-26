import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFReaderSession

actual class NFCInteractionKMP {
    var session: NFCNDEFReaderSession? = null

    actual suspend fun startReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        session = NFCNDEFReaderSession(NFCNDEFReaderSession(customAlertMessage) { record, nfcTag ->
            completionHandler(record, nfcTag)
        }, null, true)
        session?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
    }

    actual suspend fun startWriteSession(
        message: String?,
        url: String?,
        uri: String?,
        locale: String,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        session = NFCNDEFReaderSession(NFCNDEFWriterSession(message, url, uri, locale, customAlertMessage) { record, nfcTag ->
            completionHandler(record, nfcTag)
        }, null, true)
        session?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
    }
}