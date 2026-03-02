package com.example.hiragana.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hiragana.data.HiraganaData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetScreen(onBack: () -> Unit) {
    Column {
        TopAppBar(
            title = { Text("Хирагана алфавит") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Text("← Назад")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            itemsIndexed(HiraganaData.levels) { levelIndex, level ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ряд ${levelIndex + 1}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        level.forEach { hiragana ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(hiragana.symbol, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(hiragana.romaji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
