package br.com.braspag.googlepay.common

import br.com.braspag.googlepay.BillingAddress
import br.com.braspag.googlepay.ShippingAddress
import org.json.JSONObject

internal fun JSONObject.toBillingAddress() = BillingAddress(
    name = this.getString("name"),
    address1 = this.getString("address1"),
    address2 = this.getString("address2"),
    address3 = this.getString("address3"),
    locality = this.getString("locality"),
    administrativeArea = this.getString("administrativeArea"),
    postalCode = this.getString("postalCode"),
    countryCode = this.getString("countryCode"),
    sortingCode = this.getString("sortingCode")
)

internal fun JSONObject.toShippingAddress() = ShippingAddress(
    name = this.getString("name"),
    address1 = this.getString("address1"),
    address2 = this.getString("address2"),
    address3 = this.getString("address3"),
    locality = this.getString("locality"),
    administrativeArea = this.getString("administrativeArea"),
    postalCode = this.getString("postalCode"),
    countryCode = this.getString("countryCode"),
    sortingCode = this.getString("sortingCode"),
    phoneNumber = this.getString("phoneNumber")
)
