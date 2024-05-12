import model.NFCErrorKMP
import model.NFCRecordKMP
import utils.currentLocale

expect class NFCInteractionKMP {
    constructor()

    suspend fun startReadSession(
        customAlertMessage: String? = null,
        customErrorMessage: String? = null,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    )

    suspend fun startWriteSession(
        message: String? = null,
        url: String? = null,
        uri: String? = null,
        locale: String = currentLocale,
        customAlertMessage: String? = null,
        customErrorMessage: String? = null,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    )
}

class NFCInKMP {
    private val reader = NFCInteractionKMP()

    suspend fun startReading(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit) {
         reader.startReadSession(customAlertMessage, customErrorMessage, completionHandler)
    }

    suspend fun startWritingText(message: String, locale: String = currentLocale, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit) {
        reader.startWriteSession(
            message = message,
            locale = locale,
            customAlertMessage = customAlertMessage,
            customErrorMessage = customErrorMessage,
            completionHandler = completionHandler)
    }

    suspend fun startWritingURL(url: String, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit) {
        reader.startWriteSession(
            url = url,
            customAlertMessage = customAlertMessage,
            customErrorMessage = customErrorMessage,
            completionHandler = completionHandler)
    }

    suspend fun startWritingURI(uri: String, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit) {
        reader.startWriteSession(
            uri = uri,
            customAlertMessage = customAlertMessage,
            customErrorMessage = customErrorMessage,
            completionHandler = completionHandler)
    }


    // startWriteAndLockSession

    // startLockSession

    // fetchUUIDSession

    // startWriteAndFetchUUIDSession
}