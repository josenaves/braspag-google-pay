package br.com.braspag.googlepay.common

import br.com.braspag.googlepay.helper.MICROS
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Converts micros to a string format accepted by Google Pay
 *
 * @param micros value of the price.
 */
internal fun Long.microsToString() = BigDecimal(this)
    .divide(MICROS)
    .setScale(2, RoundingMode.HALF_EVEN)
    .toString()
