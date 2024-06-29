import model.NFCResult
import model.NFCWriteMessageKMP
import model.TypesOfTags
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreNFC.NFCPollingISO14443
import platform.CoreNFC.NFCPollingISO15693
import platform.CoreNFC.NFCTagReaderSession

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class NFCInteractionKMP {
    private var ndefSession: NFCNDEFReaderSession? = null
    private var tagSession: NFCTagReaderSession? = null

    actual suspend fun startGeneralTagReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (NFCResult) -> Unit
    ) {
        ndefSession = NFCNDEFReaderSession(NFCNDEFReaderSession(customAlertMessage) { record ->
            completionHandler(record)
        }, queue = null, invalidateAfterFirstRead = true)
        ndefSession?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
    }

    actual suspend fun startSpecificTagReadSession(
        tagToFind: TypesOfTags,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (NFCResult) -> Unit
    ) {
        val pollingOption = when (tagToFind) {
            TypesOfTags.MIFARE, TypesOfTags.ISO7816 -> NFCPollingISO14443
            TypesOfTags.ISO15693 -> NFCPollingISO15693
            TypesOfTags.FELICA -> NFCPollingISO14443
        }
        tagSession = NFCTagReaderSession(pollingOption, NFCTagReaderSessionKMP(customAlertMessage) { record ->
            completionHandler(record)
        }, queue = null)
        tagSession?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
    }

    actual suspend fun startWriteSession(
        message: NFCWriteMessageKMP,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (NFCResult) -> Unit
    ) {
        ndefSession = NFCNDEFReaderSession(NFCNDEFWriterSession(message,customAlertMessage) { record ->
            completionHandler(record)
        }, queue = null, invalidateAfterFirstRead = true)
        ndefSession?.let { strongSession ->
            customAlertMessage?.let { strongSession.alertMessage = it }
            strongSession.beginSession()
        }
    }
}