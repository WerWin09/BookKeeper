package com.example.bookkeeper.uiComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bookkeeper.ui.theme.*

@Composable
fun RatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starCount: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(starCount) { index ->
            val starNumber = index + 1
            IconButton(
                onClick = { onRatingChange(starNumber) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (starNumber <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Ocena $starNumber",
                    tint = if (starNumber <= rating) MainColor else Color.Gray
                )
            }
        }
    }
}
