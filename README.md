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

Para utilizar o SDK, é necessário importar os pacotes:

```
import br.com.braspag.googlepay.BraspagGooglePay
import br.com.braspag.googlepay.Environment
import br.com.braspag.googlepay.TransactionResult
```

e instanciá-lo

```
sdk= BraspagGooglePay(
    merchantId = MERCHANT_ID,
    merchantName = "Lojão das Fábricas",
    environment = Environment.SANDBOX,
    activity = this,
    dataRequestCode = REQUEST_CODE
)
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
}}
```