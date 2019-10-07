package br.com.braspag.googlepay

data class PaymentReturnInfo(
    val signature: String,
    val signedMessage: String,
    val billingAddress: BillingAddress?,
    val shippingAddress: ShippingAddress?
)

