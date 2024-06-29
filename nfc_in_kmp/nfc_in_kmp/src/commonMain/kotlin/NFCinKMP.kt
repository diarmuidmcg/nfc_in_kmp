import model.NFCErrorKMP
import model.NFCRecordKMP
import model.NFCResult
import model.NFCWriteMessageKMP
import model.TypesOfTags
import utils.currentLocale

class NFCInKMP {
    private val reader = NFCInteractionKMP()

    /// This is used to get any tag to read
    /// on iOS, this will not return the UUID or specific platform tag information
    suspend fun startGeneralReading(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startGeneralTagReadSession(customAlertMessage, customErrorMessage, completionHandler)
    }

    suspend fun startReadingMifare(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startSpecificTagReadSession(TypesOfTags.MIFARE, customAlertMessage, customErrorMessage, completionHandler)
    }

    suspend fun startReadingFeliCa(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startSpecificTagReadSession(TypesOfTags.FELICA, customAlertMessage, customErrorMessage, completionHandler)
    }

    suspend fun startReadingISO15693(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startSpecificTagReadSession(TypesOfTags.ISO15693, customAlertMessage, customErrorMessage, completionHandler)
    }

    suspend fun startReadingISO7816(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startSpecificTagReadSession(TypesOfTags.ISO7816, customAlertMessage, customErrorMessage, completionHandler)
    }

    suspend fun startWritingText(message: String, locale: String = currentLocale, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startWriteSession(
            message = NFCWriteMessageKMP.fromTextMessage(message, locale),
            customAlertMessage = customAlertMessage,
            customErrorMessage = customErrorMessage,
            completionHandler = completionHandler)
    }

    suspend fun startWritingURL(url: String, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startWriteSession(
            NFCWriteMessageKMP.fromUrl(url),
            customAlertMessage = customAlertMessage,
            customErrorMessage = customErrorMessage,
            completionHandler = completionHandler)
    }

    suspend fun startWritingURI(uri: String, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) {
        reader.startWriteSession(
            NFCWriteMessageKMP.fromUri(uri),
            customAlertMessage = customAlertMessage,
            customErrorMessage = customErrorMessage,
            completionHandler = completionHandler)
    }


    // startWriteAndLockSession

    // startLockSession

    // startWriteAndFetchUUIDSession
}