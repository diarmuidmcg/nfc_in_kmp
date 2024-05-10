import model.NFCRecord
import model.NFCTag

expect class NFCInteraction {
    constructor()

    suspend fun startReadSession(
        typeOfNFCTag: NFCTag,
        customAlertMessage: String? = null,
        customErrorMessage: String? = null
    ): NFCRecord?

    suspend fun startWriteSession(
        typeOfNFCTag: NFCTag,
        customAlertMessage: String? = null,
        customErrorMessage: String? = null
    ): NFCRecord?
}

class NFC {
    private val reader = NFCInteraction()

    suspend fun startReading(typeOfNFCTag: NFCTag, customAlertMessage: String? = null, customErrorMessage: String? = null): NFCRecord? {
        return reader.startReadSession(typeOfNFCTag, customAlertMessage, customErrorMessage)
    }

    suspend fun startWriting(typeOfNFCTag: NFCTag, customAlertMessage: String? = null, customErrorMessage: String? = null): NFCRecord? {
        return reader.startWriteSession(typeOfNFCTag, customAlertMessage, customErrorMessage)
    }
}