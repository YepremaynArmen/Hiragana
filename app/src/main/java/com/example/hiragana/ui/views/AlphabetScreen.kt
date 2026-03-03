package com.example.hiragana.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hiragana.data.HiraganaData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetScreen(
    viewModel: GameViewModel,  // ← Добавляем ViewModel!
    onBack: () -> Unit= {}
) {
    var selectedRowIndex by remember { mutableIntStateOf(-1) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(HiraganaData.levels) { rowIndex, row ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRowIndex = rowIndex }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ряд ${rowIndex + 1}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // ✅ КНОПКА ПРАКТИКИ
                            TextButton(
                                onClick = {
                                    selectedRowIndex = rowIndex
                                    viewModel.startPracticeRow(rowIndex)  // ← ПРАКТИКА!
                                    onBack()
                                }
                            ) {
                                Text("Практиковать", color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        row.forEach { hiragana ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(hiragana.symbol, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                Text(hiragana.romaji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
