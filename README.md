# Braspag Google Pay SDK

## Objetivo


## Build

Para compilar uma versão, basta ir até o terminal e digitar
 
 ```
 ./gradlew clean assemble
 ```

O arquivo AAR estará disponível no diretório `./googlepay/build/outputs/aar`


## Instalação (apps clientes)

Primeiramente, deve ser alterado o app/build.gradle do cliente para carregar dependências aar localizadas na pasta libs.

```
implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])
```

De posse do **AAR**'s, coloque-o na pasta ***libs*** do projeto cliente.

Não é mais necessário colocar a dependência da Cardinal.

Sendo assim, na pasta ***libs*** do app cliente teremos:

- braspag-google-pay-1.0.0-debug.aar (ou braspag-google-pay-1.0.0-release.aar)


## Utilização

Para utilizar o SDK, é necessário alterar o ***AndroidManifest.xml*** para incluir esses metadados:

```
<meta-data
    android:name="com.google.android.gms.wallet.api.enabled"
    android:value="true" />
```

O ***AndroidManifest.xml*** do app fica assim:

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="br.com.braspag.googlepay.sample">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <meta-data
            android:name="com.google.android.gms.wallet.api.enabled"
            android:value="true" />

    </application>

</manifest>
```

Para utilizar o SDK, é necessário importar os pacotes:

```
import br.com.braspag.googlepay.BraspagGooglePay
import br.com.braspag.googlepay.Environment
import br.com.braspag.googlepay.TransactionResult
```

Para instanciar o SDK

```
sdk = BraspagGooglePay(
    merchantId = MERCHANT_ID,
    merchantName = "Lojão das Fábricas",
    environment = Environment.SANDBOX,
    activity = this,
    dataRequestCode = REQUEST_CODE
)
```

Atenção especial para a constante `REQUEST_CODE`
 
```
const val REQUEST_CODE = 666
```

Feito isso, é necessário verificar se o Google Pay está disponível no device do usuário.
Isso é feito através do método *`isGooglePayAvailable`*

Com ele é possível habilitar (ou exibir) o botão de pagamento do Google Pay.

```
sdk.isGooglePayAvailable {
    buttonGooglePay.isEnabled = it
}
```

Após isso, é possível fazer uma transação:

```
buttonGooglePay.setOnClickListener {
    sdk.makeTransaction(100.00)
}
```

O método *`makeTransaction`* irá exibir uma activity do Google Pay.

Após isso, é necessário tratar o retorno através do método

```
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
```