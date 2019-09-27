package br.com.braspag.googlepay

enum class TransactionResult(val value: Int) {
    SUCCESS(-1),
    USER_CANCELED(0),
    ERROR(1)
}