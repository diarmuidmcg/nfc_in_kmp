import model.NFCErrorKMP
import model.NFCRecordKMP
import model.NFCTagKMP
import model.NFCWriteMessageKMP
import model.TypesOfTags

actual class NFCInteractionKMP {
    actual suspend fun startGeneralTagReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual suspend fun startSpecificTagReadSession(
        tagToFind: TypesOfTags,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual suspend fun startWriteSession(
        message: NFCWriteMessageKMP,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}