package br.com.braspag.googlepay.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

inline fun View.snack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit = {}
) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}
