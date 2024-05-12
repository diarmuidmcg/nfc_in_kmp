import model.NFCErrorKMP
import model.NFCRecordKMP
import platform.CoreNFC.NFCNDEFReaderSession
import platform.Foundation.NSError

// I would have liked to create an abstract class for all my NFCSessions to inherit, but
// Classes cannot inherit from Objective C AND Kotlin classes/interfaces
// Therefore, I just create an instance of NFCSession on all my Super NFCSessions
interface NFCSessionProtocol {
    val customErrorMessage: String?
    val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
    fun errorShouldStopProgram(connectError: NSError?, session: NFCNDEFReaderSession): ShouldStopExecuting
}

class NFCSession(
    override val customErrorMessage: String?,
    override val completionHandler: (record: NFCRecordKMP?, error: NFCErrorKMP?) -> Unit
) : NFCSessionProtocol {
    internal val currentRecord = MutableStateFlow<NFCRecordKMP>
    override fun errorShouldStopProgram(
        connectError: NSError?,
        session: NFCNDEFReaderSession
    ): ShouldStopExecuting {
        connectError?.let {
            customErrorMessage?.let { errMsg ->
                session.invalidateSessionWithErrorMessage(errMsg)
            } ?: session.invalidateSession()
            val errorDetails = NFCErrorKMP(
                connectError.code.toString(),
                connectError.localizedDescription,
                connectError.localizedFailureReason,
                connectError.localizedRecoverySuggestion
            )
            completionHandler(null, errorDetails)
            return true
        } ?: return false
    }
}
