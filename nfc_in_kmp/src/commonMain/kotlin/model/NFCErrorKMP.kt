package model

class NFCErrorKMP(
    val statusCode: String,
    val message: String,
    val errorReason: String? = null,
    val recoverySuggestion: String? = null
)