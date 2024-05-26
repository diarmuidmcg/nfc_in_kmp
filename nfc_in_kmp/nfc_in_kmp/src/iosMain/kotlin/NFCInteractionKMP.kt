import model.NFCErrorKMP
import model.NFCRecordKMP
import model.NFCWriteMessageKMP
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCPollingISO14443
import platform.CoreNFC.NFCPollingOption
import platform.CoreNFC.NFCTagReaderSession

actual class NFCInteractionKMP {
    private var ndefSession: NFCNDEFReaderSession? = null
    private var tagSession: NFCTagReaderSession? = null

    actual suspend fun startReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        tagSession = NFCTagReaderSession(NFCPollingISO14443, NFCTagReaderSessionKMP(customAlertMessage) { record, nfcTag ->
            completionHandler(record, nfcTag)
        }, null)
        tagSession?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
    }

//    actual suspend fun startReadSession(
//        customAlertMessage: String?,
//        customErrorMessage: String?,
//        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
//    ) {
//        session = NFCNDEFReaderSession(NFCNDEFReaderSession(customAlertMessage) { record, nfcTag ->
//            completionHandler(record, nfcTag)
//        }, null, true)
//        session?.let { strongSession ->
//            customAlertMessage?.let { strongSession.alertMessage = it }
//            strongSession.beginSession()
//        }
//    }

    actual suspend fun startWriteSession(
        message: String?,
        url: String?,
        uri: String?,
        locale: String,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        ndefSession = NFCNDEFReaderSession(NFCNDEFWriterSession(message, url, uri, locale, customAlertMessage) { record, nfcTag ->
            completionHandler(record, nfcTag)
        }, null, true)
        ndefSession?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
    }
}