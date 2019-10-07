package br.com.braspag.googlepay.common

import android.util.Log
import br.com.braspag.googlepay.BuildConfig

fun Any.logd(message: String?) {
    if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "" + message)
}

fun Any.loge(message: String?) {
    Log.e(this::class.java.toString(), "" + message)
}