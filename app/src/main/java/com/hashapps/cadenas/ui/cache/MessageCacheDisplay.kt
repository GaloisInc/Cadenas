package com.hashapps.cadenas.ui.cache

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hashapps.cadenas.ui.processing.ProcessingMode

/**
 * Display the cache
 */
@Composable
fun displayMessageCache(msgs: List<Message>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Message Cache",//TODO FIXME!
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        HorizontalDivider(thickness = 2.dp)
        msgs.forEach() {
            Text(
                text = it.message,
                textAlign = if (it.processingMode == ProcessingMode.Encode) TextAlign.Left else TextAlign.Right,
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
            )
            HorizontalDivider(thickness = 1.dp)
        }
    }
}