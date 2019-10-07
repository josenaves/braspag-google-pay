package br.com.braspag.googlepay.helper

import android.util.Log
import br.com.braspag.googlepay.data.InternalConstants
import br.com.braspag.googlepay.data.InternalConstants.COUNTRY_CODE
import br.com.braspag.googlepay.data.InternalConstants.CURRENCY_CODE
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

const val MILLION : Long = 1000000

val MICROS = BigDecimal(1000000.0)

internal class GooglePayHelper {

    companion object {
        const val TAG = "GooglePayHelper"
    }

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    fun getPaymentDataRequest(
        merchantId: String,
        merchantName: String,
        billingAddressRequired: Boolean,
        shippingAddressRequired: Boolean,
        phoneNumberRequired: Boolean,
        price: String
    ): JSONObject? {
        try {
            return JSONObject(baseRequest.toString()).apply {
                put(
                    "allowedPaymentMethods",
                    JSONArray().put(cardPaymentMethod(merchantId, billingAddressRequired))
                )
                put("transactionInfo", getTransactionInfo(price))
                put("merchantInfo", getMerchantInfo(merchantName))

                // An optional shipping address requirement is a top-level property of the
                // PaymentDataRequest JSON object.
                val shippingAddressParameters = JSONObject().apply {
                    put("phoneNumberRequired", phoneNumberRequired)
                    put("allowedCountryCodes", JSONArray(listOf(COUNTRY_CODE)))
                }
                put("shippingAddressRequired", shippingAddressRequired)
                put("shippingAddressParameters", shippingAddressParameters)
            }
        } catch (e: JSONException) {
            Log.w(TAG, "Error - $e")
            return null
        }
    }

    fun isReadyToPayRequest(billingAddressRequired: Boolean): JSONObject? {
        return try {
            val isReadyToPayRequest = JSONObject(baseRequest.toString())
            isReadyToPayRequest.put(
                "allowedPaymentMethods",
                JSONArray().put(baseCardPaymentMethod(billingAddressRequired))
            )
            isReadyToPayRequest
        } catch (e: JSONException) {
            null
        }
    }

    private fun cardPaymentMethod(merchantId: String, billingAddressRequired: Boolean): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod(billingAddressRequired)
        cardPaymentMethod.put(
            "tokenizationSpecification",
            gatewayTokenizationSpecification(merchantId)
        )
        return cardPaymentMethod
    }

    private fun baseCardPaymentMethod(billingAddressRequired: Boolean): JSONObject {
        val allowedCardNetworks = JSONArray(InternalConstants.SUPPORTED_NETWORKS)
        val allowedCardAuthMethod = JSONArray(InternalConstants.SUPPORTED_AUTH_METHODS)

        return JSONObject().apply {
            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethod)
                put("allowedCardNetworks", allowedCardNetworks)
                put("billingAddressRequired", billingAddressRequired)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                })
            }
            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    private fun gatewayTokenizationSpecification(gatewayMerchantId: String): JSONObject {
        val parameters = mapOf(
            "gateway" to InternalConstants.DEFAULT_GATEWAY_NAME,
            "gatewayMerchantId" to gatewayMerchantId
        )

        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject(parameters))
        }
    }

    private fun getMerchantInfo(merchantName: String): JSONObject {
        return JSONObject().put("merchantName", merchantName)
    }

    private fun getTransactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("countryCode", COUNTRY_CODE)
            put("currencyCode", CURRENCY_CODE)
        }
    }


}