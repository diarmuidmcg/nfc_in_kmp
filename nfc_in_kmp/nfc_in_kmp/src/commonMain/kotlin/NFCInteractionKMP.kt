import model.NFCErrorKMP
import model.NFCRecordKMP
import model.NFCResult
import model.NFCWriteMessageKMP
import model.TypesOfTags

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class NFCInteractionKMP() {
//    suspend fun startGeneralTagReadSession(
//        customAlertMessage: String? = null,
//        customErrorMessage: String? = null,
//        completionHandler: (NFCResultType<NFCRecordKMP, NFCErrorKMP>) -> Unit
//    )

    suspend fun startGeneralTagReadSession(
        customAlertMessage: String? = null,
        customErrorMessage: String? = null,
        completionHandler: (NFCResult) -> Unit
    )

    suspend fun startSpecificTagReadSession(
        tagToFind: TypesOfTags,
        customAlertMessage: String? = null,
        customErrorMessage: String? = null,
        completionHandler: (NFCResult) -> Unit
    )

    suspend fun startWriteSession(
        message: NFCWriteMessageKMP,
        customAlertMessage: String? = null,
        customErrorMessage: String? = null,
        completionHandler: (NFCResult) -> Unit
    )
}