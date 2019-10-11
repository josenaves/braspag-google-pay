package br.com.braspag.googlepay.common

// returns Any
fun String.doubleOrString() = try {
    toDouble()
} catch(e: NumberFormatException) {
    this
}
