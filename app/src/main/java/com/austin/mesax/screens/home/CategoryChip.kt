package com.austin.mesax.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage


@Composable
fun CategoryChip(
    icon: String? = null,
    iconM: ImageVector? = null,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = Color.LightGray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = modifier.clickable{
            onClick()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {


            icon?.let {
                AsyncImage(
                    model = icon,
                    contentDescription = text,
                    modifier = Modifier.size(24.dp)

                )
            }



            iconM?.let {
                Icon(
                    imageVector = it,
                    contentDescription = text,
                    modifier = Modifier.size(18.dp),

                )


            }



            Text(
                text = text,
                color = if (isSelected) Color.White else
                    Color.Black.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )


        }
    }

}