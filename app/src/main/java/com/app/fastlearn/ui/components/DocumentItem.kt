package com.app.fastlearn.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.util.DateTimeUtils

@Composable
fun DocumentListItem(
    document: Document,
    onDocumentClick: (Document) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { onDocumentClick(document) },
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = document.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = document.content,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = document.category,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = DateTimeUtils.formatDate(document.createdDate.toString()),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
