package br.com.braspag.googlepay

data class ShippingAddress(
    val name: String,
    val address1: String,
    val address2: String,
    val address3: String,
    val locality: String,
    val administrativeArea: String,
    val postalCode: String,
    val countryCode: String,
    val sortingCode: String,
    val phoneNumber: String
)