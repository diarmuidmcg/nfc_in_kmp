import model.NFCErrorKMP
import model.NFCRecordKMP
import model.NFCTagKMP

actual class NFCInteractionKMP {
    actual suspend fun startReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual suspend fun startWriteSession(
        message: String?,
        url: String?,
        uri: String?,
        locale: String?,
        customAlertMessage: String?,
        customErrorMessage: String?,
        completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}