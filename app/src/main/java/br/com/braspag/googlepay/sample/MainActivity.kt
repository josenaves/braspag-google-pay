package br.com.braspag.googlepay.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.braspag.googlepay.BraspagGooglePay
import br.com.braspag.googlepay.Environment
import br.com.braspag.googlepay.TransactionResult
import br.com.braspag.googlepay.common.snack
import kotlinx.android.synthetic.main.activity_main.*

const val REQUEST_CODE = 666
const val MERCHANT_ID = "fecd2b61-3f0e-4e49-8b4f-eb382fa4da56"

// MerchantId: fecd2b61-3f0e-4e49-8b4f-eb382fa4da56 (SANDBOX)
// MerchantKey = WSCIKUJBVHFPPPAWFPJGRYXRDNGQTMZAGBJSZZBV (sandbox)

class MainActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var sdk: BraspagGooglePay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sdk = BraspagGooglePay(
            merchantId = MERCHANT_ID,
            merchantName = "Lojão das Fábricas",
            environment = Environment.SANDBOX,
            billingAddressRequired = true,
            shippingAddressRequired = true,
            phoneNumberRequired = true,
            activity = this,
            dataRequestCode = REQUEST_CODE
        )

        buttonGooglePay.setOnClickListener {
            val price = editPrice.text.toString()
            // check price
            if (price.isBlank()) {
                rootView.snack(getString(R.string.message_validation_empty_price))
            } else {
                val p = price.toDoubleOrNull()

                p?.let {
                    buttonGooglePay.isEnabled = false
                    sdk.makeTransaction(it)
                } ?: rootView.snack(getString(R.string.message_validation_numeric_price))

            }
        }
    }

    override fun onStart() {
        super.onStart()

        sdk.isGooglePayAvailable {
            Log.i(TAG, "Is google pay available? $it")
            buttonGooglePay.isEnabled = it
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            REQUEST_CODE -> {
                when (resultCode) {

                    TransactionResult.SUCCESS.value ->
                        data?.let { intent ->
                            val paymentInfo = sdk.getDataFromIntent(intent)

                            Log.i(TAG, "paymentInfo: $paymentInfo")

                            rootView.snack(getString(R.string.message_transacion_success))

                            //  continue authorization flow
                        }

                    TransactionResult.USER_CANCELED.value -> {
                        // Nothing to do here normally - the user simply cancelled without selecting
                        // a payment method.
                        rootView.snack(getString(R.string.message_user_canceled))
                    }

                    TransactionResult.ERROR.value -> {
                        data?.let {
                            val errorCode = sdk.getStatusFromIntent(it)

                            //  display error to user
                            rootView.snack(String.format(getString(R.string.message_error_transaction, errorCode)))
                        }
                    }
                }

                buttonGooglePay.isEnabled = true
            }
        }
    }
}
