package br.com.braspag.googlepay

import android.app.Activity
import android.content.Intent
import android.util.Log
import br.com.braspag.googlepay.common.microsToString
import br.com.braspag.googlepay.helper.GooglePayHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.android.gms.wallet.WalletConstants.ENVIRONMENT_PRODUCTION
import com.google.android.gms.wallet.WalletConstants.ENVIRONMENT_TEST
import kotlin.math.roundToLong

// MerchantId: fecd2b61-3f0e-4e49-8b4f-eb382fa4da56 (SANDBOX)
// MerchantKey = WSCIKUJBVHFPPPAWFPJGRYXRDNGQTMZAGBJSZZBV (sandbox)

class BraspagGooglePay(
    private val merchantId: String,
    private val merchantName: String,

    private val billingAddressRequired: Boolean = false,
    private val shippingAddressRequired: Boolean = false,
    private val phoneNumberRequired: Boolean = false,

    private val environment: Environment,
    private val activity: Activity,
    private val dataRequestCode: Int = 999
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
        val json =
            googlePayHelper.isReadyToPayRequest(billingAddressRequired) ?: callback.invoke(false)
        val request = IsReadyToPayRequest.fromJson(json.toString())

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = client.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                if (completedTask.isSuccessful) {
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            } catch (exception: ApiException) {
                Log.w("isReadyToPay failed", exception)
                callback.invoke(false)
            }
        }
    }

    fun makeTransaction(price: Double) {

        val priceMicros = (price * 1000000).roundToLong().microsToString()
        val json = googlePayHelper.getPaymentDataRequest(
            merchantId,
            merchantName,
            billingAddressRequired,
            shippingAddressRequired,
            phoneNumberRequired,
            priceMicros
        )

        if (json == null) {
            Log.e("makeTransaction", "Can't fetch payment data request")
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

    fun getDataFromIntent(intent: Intent): PaymentReturnInfo {
        val data = PaymentData.getFromIntent(intent)
        val paymentInformation = data?.toJson()


        // TODO finish this method - look at handlePaymentSuccess

        return PaymentReturnInfo("xx", "yyy")
    }

}
