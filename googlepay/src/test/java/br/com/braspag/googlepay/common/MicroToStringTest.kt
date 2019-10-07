package br.com.braspag.googlepay.common

import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class MicroToStringTest {

    @Test
    fun zeroToMicroString() {
        val number : Long = 0
        assertThat(number.microsToString(), `is`("0.00"))
    }

    @Test
    fun smallPositiveToMicroString() {
        val number : Long = 1
        assertThat(number.microsToString(), `is`("0.00"))
    }

    @Test
    fun largePositiveToMicroString() {
        val number : Long = 1450555
        assertThat(number.microsToString(), `is`("1.45"))
    }

    @Test
    fun smallNegativeToMicroString() {
        val number : Long = -20
        assertThat(number.microsToString(), `is`("0.00"))
    }

    @Test
    fun largeNegativeToMicroString() {
        val number : Long = -6666666
        assertThat(number.microsToString(), `is`("-6.67"))
    }
}