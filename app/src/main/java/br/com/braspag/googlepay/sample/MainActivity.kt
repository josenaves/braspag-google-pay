package br.com.braspag.googlepay.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.braspag.googlepay.BraspagGooglePay
import br.com.braspag.googlepay.Environment
import br.com.braspag.googlepay.TransactionResult
import kotlinx.android.synthetic.main.activity_main.*

const val REQUEST_CODE = 666
const val MERCHANT_ID = "fecd2b61-3f0e-4e49-8b4f-eb382fa4da56"

// MerchantId: fecd2b61-3f0e-4e49-8b4f-eb382fa4da56 (SANDBOX)
// MerchantKey = WSCIKUJBVHFPPPAWFPJGRYXRDNGQTMZAGBJSZZBV (sandbox)

class MainActivity : AppCompatActivity() {

    lateinit var sdk: BraspagGooglePay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sdk = BraspagGooglePay(
            merchantId = MERCHANT_ID,
            merchantName = "Lojão das Fábricas",
            environment = Environment.SANDBOX,
            activity = this,
            dataRequestCode = REQUEST_CODE
        )

        sdk.isGooglePayAvailable {
            buttonGooglePay.isEnabled = it
        }

        buttonGooglePay.setOnClickListener {
            buttonGooglePay.isEnabled = false
            sdk.makeTransaction(100.00)
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

                            // TODO continue authorization flow
                        }

                    TransactionResult.USER_CANCELED.value -> {
                        // Nothing to do here normally - the user simply cancelled without selecting
                        // a payment method.
                    }

                    TransactionResult.ERROR.value -> {
                        data?.let {
                            val errorCode = sdk.getStatusFromIntent(it)
                            // TODO display error to user
                        }
                    }
                }

                buttonGooglePay.isClickable = true
            }
        }
    }
}
