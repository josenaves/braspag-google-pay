package br.com.braspag.googlepay.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import br.com.braspag.googlepay.BraspagGooglePay
import br.com.braspag.googlepay.Environment
import br.com.braspag.googlepay.TransactionResult
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
            buttonGooglePay.isEnabled = false
            sdk.makeTransaction(100.00)
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

                            //  continue authorization flow
                        }

                    TransactionResult.USER_CANCELED.value -> {
                        // Nothing to do here normally - the user simply cancelled without selecting
                        // a payment method.

                        Toast.makeText(this, "Usuário cancelou pagamento!", Toast.LENGTH_LONG)
                            .show()
                    }

                    TransactionResult.ERROR.value -> {
                        data?.let {
                            val errorCode = sdk.getStatusFromIntent(it)

                            //  display error to user
                            Toast.makeText(this, "Erro na transação: $errorCode", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }

                buttonGooglePay.isEnabled = true
            }
        }
    }
}
