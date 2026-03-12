package com.austin.mesax.screens.home.components

// CurrencyUtils.kt

fun formatToKwanza(value: String): String {
    val digits = value.filter { it.isDigit() }
    if (digits.isEmpty()) return ""

    val number = digits.toLongOrNull() ?: return ""
    val intPart = number / 100
    val decPart = number % 100

    val formatted = "%,d".format(intPart).replace(',', '.')
    return "$formatted,${"%02d".format(decPart)} Kz"
}

fun parseKwanzaToDouble(formatted: String): Double {
    val clean = formatted
        .replace(" Kz", "")
        .replace(".", "")
        .replace(",", ".")
    return clean.toDoubleOrNull() ?: 0.0
}