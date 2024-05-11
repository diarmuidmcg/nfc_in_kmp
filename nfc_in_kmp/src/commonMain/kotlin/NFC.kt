import model.NFCRecordKMP
import model.NFCTagKMP

expect class NFCInteractionKMP {
    constructor()

    suspend fun startReadSession(
        customAlertMessage: String? = null,
        customErrorMessage: String? = null
    ): NFCRecordKMP?

    suspend fun startWriteSession(
        message: String,
        // url: String?
        // is URI
        // pass in locale
        customAlertMessage: String? = null,
        customErrorMessage: String? = null
    ): NFCRecordKMP?
}

class NFCInKMP {
    private val reader = NFCInteractionKMP()

    suspend fun startReading(customAlertMessage: String? = null, customErrorMessage: String? = null): NFCRecordKMP? {
        return reader.startReadSession(customAlertMessage, customErrorMessage)
    }

    suspend fun startWriting(message: String, customAlertMessage: String? = null, customErrorMessage: String? = null): NFCRecordKMP? {
        return reader.startWriteSession(message, customAlertMessage, customErrorMessage)
    }


    // startWriteAndLockSession

    // startLockSession

    // fetchUUIDSession

    // startWriteAndFetchUUIDSession
}