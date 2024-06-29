package model

sealed class NFCResult {
    data class Success(val record: NFCRecordKMP): NFCResult()
    data class Failure(val error: NFCErrorKMP): NFCResult()
}