import model.NFCRecord
import model.NFCTag

actual class NFCInteraction {
    actual suspend fun startReadSession(
        typeOfNFCTag: NFCTag,
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecord? {
        TODO("Not yet implemented")
    }

    actual suspend fun startWriteSession(
        typeOfNFCTag: NFCTag,
        customAlertMessage: String?,
        customErrorMessage: String?
    ): NFCRecord? {
        TODO("Not yet implemented")
    }
}