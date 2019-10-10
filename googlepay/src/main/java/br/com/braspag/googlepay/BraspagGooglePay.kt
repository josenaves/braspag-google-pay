package br.com.braspag.googlepay

import android.app.Activity
import android.content.Intent
import android.util.Log
import br.com.braspag.googlepay.common.*
import br.com.braspag.googlepay.common.microsToString
import br.com.braspag.googlepay.common.toBillingAddress
import br.com.braspag.googlepay.common.toShippingAddress
import br.com.braspag.googlepay.helper.GooglePayHelper
import br.com.braspag.googlepay.helper.MILLION
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.android.gms.wallet.WalletConstants.ENVIRONMENT_PRODUCTION
import com.google.android.gms.wallet.WalletConstants.ENVIRONMENT_TEST
import org.json.JSONObject
import kotlin.math.roundToLong

// MerchantId: fecd2b61-3f0e-4e49-8b4f-eb382fa4da56 (SANDBOX)
// MerchantKey = WSCIKUJBVHFPPPAWFPJGRYXRDNGQTMZAGBJSZZBV (sandbox)

const val RESULT_ERROR = 1

class BraspagGooglePay(
    private val merchantId: String,
    private val merchantName: String,

    private val billingAddressRequired: Boolean = true,
    private val shippingAddressRequired: Boolean = false,
    private val phoneNumberRequired: Boolean = false,

    private val environment: Environment,
    private val activity: Activity,
    private val dataRequestCode: Int
) {

    private val googlePayHelper = GooglePayHelper()

    private lateinit var client: PaymentsClient

    init {
        createClient()
    }

    private fun createClient() {
        val env =
            if (environment == Environment.SANDBOX) ENVIRONMENT_TEST else ENVIRONMENT_PRODUCTION

        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(env)
            .build()

        client = Wallet.getPaymentsClient(activity, walletOptions)
    }

    fun isGooglePayAvailable(callback: (Boolean) -> Unit) {

        val json = googlePayHelper.isReadyToPayRequest(billingAddressRequired)
        json?.let {


            logd("isReadyToPayRequest JSON: [$it]")
            val request = IsReadyToPayRequest.fromJson(it.toString())

            val task = client.isReadyToPay(request)

            task.addOnCompleteListener { completedTask ->
                try {
                    completedTask.getResult(ApiException::class.java)?.let { result ->
                        logd("result: [$result]")
                        callback.invoke(result)
                    } ?: callback.invoke(false)
                } catch (exception: ApiException) {
                    Log.w("isReadyToPay failed", exception)

                    WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE
                    callback.invoke(false)
                }
            }
        } ?: callback.invoke(false)
    }

    fun makeTransaction(price: Double) {

        val priceMicros = (price * MILLION).roundToLong().microsToString()

        val json = googlePayHelper.getPaymentDataRequest(
            merchantId,
            merchantName,
            billingAddressRequired,
            shippingAddressRequired,
            phoneNumberRequired,
            priceMicros
        )

        if (json == null) {
            loge("Can't fetch payment data request")
            return
        }

        val request = PaymentDataRequest.fromJson(json.toString())

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                client.loadPaymentData(request),
                activity,
                dataRequestCode
            )
        }
    }

    fun getDataFromIntent(intent: Intent): PaymentReturnInfo? {

        val paymentData = PaymentData.getFromIntent(intent)

        paymentData?.let {
            val paymentInformation = it.toJson()

            logd( "paymentInformation : $paymentInformation")

            try {
                val paymentMethodData =
                    JSONObject(paymentInformation).getJSONObject("paymentMethodData")

                var shippingAddress: JSONObject? = null
                if (JSONObject(paymentInformation).has("shippingAddress")) {
                    shippingAddress =
                        JSONObject(paymentInformation).getJSONObject("shippingAddress")
                }

                var billingAddress: JSONObject? = null
                if (paymentMethodData
                        .getJSONObject("info")
                        .has("billingAddress")
                ) {
                    billingAddress =
                        paymentMethodData
                            .getJSONObject("info")
                            .getJSONObject("billingAddress")
                }

                val billingName = billingAddress?.getString("name")

                logd("BillingName : $billingName")

                val token = paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")

                logd( "token: $token")
                logd( "billingAddress: $billingAddress")
                logd("shippingAddress: $shippingAddress")

                val tokenObject = JSONObject(token)

                val signature = tokenObject.getString("signature")
                val signedMessage = tokenObject.getString("signedMessage")

                return PaymentReturnInfo(
                    signature,
                    signedMessage,
                    billingAddress = billingAddress?.toBillingAddress(),
                    shippingAddress = shippingAddress?.toShippingAddress()
                )

            } catch (e: Throwable) {
                loge("Error: $e")
                return null
            }
        }

        return null
    }

    fun getStatusFromIntent(intent: Intent): Int {
        AutoResolveHelper.getStatusFromIntent(intent)?.let {
            return it.statusCode
        } ?: return 0
    }
}
