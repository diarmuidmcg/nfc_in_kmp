import model.NFCResult
import model.NFCWriteMessageKMP
import model.TypesOfTags

actual class NFCInteractionKMP {
    actual suspend fun startGeneralTagReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (NFCResult) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual suspend fun startSpecificTagReadSession(
        tagToFind: TypesOfTags,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (NFCResult) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual suspend fun startWriteSession(
        message: NFCWriteMessageKMP,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (NFCResult) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}