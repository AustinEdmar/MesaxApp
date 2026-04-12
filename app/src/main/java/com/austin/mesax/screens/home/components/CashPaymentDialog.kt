package com.austin.mesax.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.austin.mesax.screens.home.formatKzCart

@Composable
fun CashPaymentDialog(
    total: Int,
    onDismiss: () -> Unit,
    onConfirm: (received: Int, change: Int) -> Unit
) {

    var receivedText by remember { mutableStateOf("") }

    val received = receivedText.toIntOrNull() ?: 0
    val change = received - total

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Pagamento em Dinheiro", fontWeight = FontWeight.Bold)
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text("Total: ${formatKzCart(total.toDouble())} KZ")

//                OutlinedTextField(


                OutlinedTextField(
                    value = receivedText,
                    onValueChange = { receivedText = it },

                    placeholder = {
                        Text(
                            text = "Valor recebido",
                            color = Color(0xFFAAAAAA),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Color(0xFFFFC107)
                    )
                )

                Text(
                    text = "Troco: ${formatKzCart(change.coerceAtLeast(0).toDouble())} KZ",
                    fontWeight = FontWeight.Bold
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {
                    onConfirm(received, change)
                },
                enabled = received >= total
            ) {
                Text("Confirmar")
            }
        },

        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}