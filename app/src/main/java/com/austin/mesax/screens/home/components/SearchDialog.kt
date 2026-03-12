package com.austin.mesax.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SearchDialog(
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {

    var text by remember { mutableStateOf("") }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false     // 👈 impede fechar com botão voltar
        )
    ) {


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Procurar producto",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },


                        placeholder = {
                            Text(
                                text = "Digite o nome do produto",
                                color = Color(0xFFAAAAAA),
                                fontSize = 14.sp
                            )
                        },

                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(40.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(40.dp) // 👈 aqui controla o arredondado
                                    ),
                                contentAlignment = Alignment.Center


                            ){
                                IconButton(
                                    onClick = { onSearch(text) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buscar",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(140.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = MaterialTheme.colorScheme.primary,
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    }


                }
            }
        }
    }


    //
