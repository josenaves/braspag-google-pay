package br.com.braspag.googlepay.common

import android.util.Log
import br.com.braspag.googlepay.BuildConfig

internal fun Any.logd(message: String?) {
    if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "" + message)
}

internal fun Any.logi(message: String?) {
    Log.i(this::class.java.toString(), "" + message)
}

internal fun Any.logw(message: String?) {
    Log.w(this::class.java.toString(), "" + message)
}

internal fun Any.loge(message: String?) {
    Log.e(this::class.java.toString(), "" + message)
}