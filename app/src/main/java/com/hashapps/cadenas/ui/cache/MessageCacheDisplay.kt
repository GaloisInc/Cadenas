package com.hashapps.cadenas.ui.cache

import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.components.DeleteConfirmationDialog
import com.hashapps.cadenas.ui.processing.ProcessingMode
import java.time.Instant
import java.util.Date
import java.util.Locale

/**
 * Display the cache
 */
@Composable
fun DisplayMessageCache(
    messages: List<Message>,
    cacheTimeinMS: Int,
    clearMessageCache: () -> Unit) {

    var expanded by remember { mutableStateOf(true) }
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    if (messages.isNotEmpty()) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = stringResource(R.string.expand)
                    )
                }
                Text(
                    text = stringResource(R.string.message_cache_label)+" (" + messages.size + ")",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
                IconButton(
                    onClick = { deleteConfirmationRequired = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
            if (expanded) {
                HorizontalDivider(thickness = 2.dp)
                messages.reversed().forEach() {
                    //move cards slightly right or left based on Encode/Decode status
                    val cardPadding = if (it.processingMode == ProcessingMode.Encode) {
                        PaddingValues(4.dp, 4.dp, 24.dp, 4.dp)
                    } else {
                        PaddingValues(24.dp, 4.dp, 4.dp, 4.dp)
                    }
                    Card(
                        shape = RoundedCornerShape(size = 8.dp),
                        modifier = Modifier
                            .padding(cardPadding)
                            .align(if (it.processingMode == ProcessingMode.Encode) AbsoluteAlignment.Left else AbsoluteAlignment.Right)
                    )
                    {
                        Row(
                            modifier = Modifier.padding(0.dp)
                        ) {
                            //Display date of message creation
                            val creationDate =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                val formattedDate =
                                    SimpleDateFormat(
                                        "EEE, MMM d, hh:mm:ss",
                                        Locale.getDefault()
                                    )
                                    formattedDate.format(
                                        Date(
                                            it.time.toEpochMilli().toLong()
                                        )
                                    )
                            } else {
                                it.time.toString()
                            }
                            Text(
                                text = creationDate,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
                            )
                            //Display countdown to deletion of message
                            val msRemaining =
                                cacheTimeinMS - Instant.now()
                                    .minusMillis(it.time.toEpochMilli())
                                    .toEpochMilli();
                            val seconds = (msRemaining / 1000) % 60
                            val minutes = (msRemaining / (1000 * 60) % 60)
                            val hours = (msRemaining / (1000 * 60 * 60) % 24)
                            Text(
                                text = String.format(
                                    "(%02d:%02d:%02d)",
                                    hours,
                                    minutes,
                                    seconds
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp)
                            )
                        }
                        //Display the message
                        Text(
                            text = it.message,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(8.dp, 0.dp, 8.dp, 8.dp)
                                .align(if (it.processingMode == ProcessingMode.Encode) AbsoluteAlignment.Left else AbsoluteAlignment.Right)
                        )
                    }
                }
            }
        }
    }

    if (deleteConfirmationRequired) {
        DeleteConfirmationDialog(
            confirmationQuestion = stringResource(R.string.delete_message_cache_question),
            onDeleteConfirm = {
                deleteConfirmationRequired = false
                clearMessageCache()
            },
            onDeleteCancel = { deleteConfirmationRequired = false },
        )
    }
}





