import model.NFCRecordKMP
import model.NFCTagKMP

actual class NFCInteractionKMP {
    actual suspend fun startReadSession(
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecordKMP? {
        TODO("Not yet implemented")
    }

    actual suspend fun startWriteSession(
        message: String,
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecordKMP? {
        TODO("Not yet implemented")
    }

}