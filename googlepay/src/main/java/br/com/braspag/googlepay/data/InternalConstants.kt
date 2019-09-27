package br.com.braspag.googlepay.data

internal object InternalConstants {

    const val DEFAULT_GATEWAY_NAME = "cielo"

    val SUPPORTED_NETWORKS = listOf(
        "AMEX",
        "DISCOVER",
        "JCB",
        "MASTERCARD",
        "VISA"
    )

    const val COUNTRY_CODE = "BR"

    const val CURRENCY_CODE = "BRL"

    val SUPPORTED_AUTH_METHODS = listOf("CRYPTOGRAM_3DS")

}