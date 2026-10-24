package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KomsosActivity
import com.example.ui.theme.ExcelCellSelected
import com.example.ui.theme.ExcelGridBorder
import com.example.ui.theme.ExcelHeaderBg
import com.example.ui.theme.ExcelHeaderFg
import com.example.ui.theme.ExcelRowAlternate
import com.example.util.SpreadsheetExporter

data class SelectedCellInfo(
    val coordinate: String,
    val columnName: String,
    val value: String,
    val activity: KomsosActivity
)

@Composable
fun SpreadsheetScreen(
    activities: List<KomsosActivity>,
    onSelectActivity: (KomsosActivity) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Semua") }
    var selectedCell by remember { mutableStateOf<SelectedCellInfo?>(null) }
    var showExportMenu by remember { mutableStateOf(false) }

    val categories = listOf("Semua", "Tokoh Agama", "Tokoh Masyarakat", "Tokoh Pemuda", "Aparat Desa", "Kelompok Tani", "Warga")

    val filteredList = activities.filter { item ->
        val matchSearch = searchQuery.isBlank() ||
            item.title.contains(searchQuery, ignoreCase = true) ||
            item.contactPerson.contains(searchQuery, ignoreCase = true) ||
            item.village.contains(searchQuery, ignoreCase = true) ||
            item.topic.contains(searchQuery, ignoreCase = true) ||
            item.summary.contains(searchQuery, ignoreCase = true)
        val matchCat = selectedCategoryFilter == "Semua" || item.targetCategory == selectedCategoryFilter
        matchSearch && matchCat
    }

    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F2))
            .testTag("spreadsheet_screen")
    ) {
        // Excel Top Action Toolbar
        Surface(
            color = ExcelHeaderBg,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "LEMBAR KERJA SPREADSHEET EXCEL",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "Rekapitulasi Komsos Babinsa • Siap Ekspor .XLS / .CSV",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Action buttons
                    Row {
                        IconButton(
                            onClick = {
                                SpreadsheetExporter.copyTableToClipboard(context, filteredList)
                            },
                            modifier = Modifier.testTag("btn_copy_table")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin Tabel", tint = Color.White)
                        }
                        IconButton(
                            onClick = {
                                SpreadsheetExporter.shareSpreadsheet(context, filteredList, asXmlExcel = true)
                            },
                            modifier = Modifier.testTag("btn_share_excel")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Bagikan Excel", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fast Export Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            SpreadsheetExporter.shareSpreadsheet(context, filteredList, asXmlExcel = true)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_export_xls")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ekspor .XLS (Excel)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            SpreadsheetExporter.shareSpreadsheet(context, filteredList, asXmlExcel = false)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_export_csv")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ekspor .CSV (UTF-8)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search & Filter Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari di lembar kerja...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("spreadsheet_search_input"),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(8.dp)
            )

            Box {
                Button(
                    onClick = { showExportMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6445)),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("spreadsheet_category_filter")
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedCategoryFilter == "Semua") "Filter Sasaran" else selectedCategoryFilter,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
                DropdownMenu(
                    expanded = showExportMenu,
                    onDismissRequest = { showExportMenu = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, fontSize = 12.sp) },
                            onClick = {
                                selectedCategoryFilter = cat
                                showExportMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Formula / Status Simulation Bar
        Surface(
            color = Color(0xFFECEFE8),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "fx",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "=REKAP_KOMSOS(A2:N${filteredList.size + 1}) • ${filteredList.size} Baris Aktif",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF2C3E2A)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Spreadsheet Interactive Grid
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .background(Color.White)
                .border(1.dp, ExcelGridBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .background(ExcelHeaderBg)
                        .height(34.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderCell(label = "A", title = "No", width = 45.dp)
                    HeaderCell(label = "B", title = "Tanggal", width = 95.dp)
                    HeaderCell(label = "C", title = "Jam", width = 60.dp)
                    HeaderCell(label = "D", title = "Desa / Wilayah", width = 130.dp)
                    HeaderCell(label = "E", title = "Dusun / Lokasi", width = 140.dp)
                    HeaderCell(label = "F", title = "Sasaran Komsos", width = 130.dp)
                    HeaderCell(label = "G", title = "Nama Tokoh / Warga", width = 150.dp)
                    HeaderCell(label = "H", title = "Jabatan / Peran", width = 140.dp)
                    HeaderCell(label = "I", title = "Topik Pembahasan", width = 180.dp)
                    HeaderCell(label = "J", title = "Hasil & Kesimpulan", width = 240.dp)
                    HeaderCell(label = "K", title = "Rencana Tindak Lanjut", width = 220.dp)
                    HeaderCell(label = "L", title = "Status", width = 110.dp)
                    HeaderCell(label = "M", title = "Dokumentasi Foto", width = 130.dp)
                    HeaderCell(label = "N", title = "Babinsa Pelapor", width = 140.dp)
                }

                // Table Rows
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(filteredList) { index, item ->
                        val rowIndex = index + 2 // Excel row starts at 2 (1 is header)
                        val rowBg = if (index % 2 == 0) Color.White else ExcelRowAlternate

                        Row(
                            modifier = Modifier
                                .background(rowBg)
                                .height(46.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DataCell(
                                value = (index + 1).toString(),
                                width = 45.dp,
                                isNumber = true,
                                onClick = {
                                    selectedCell = SelectedCellInfo("A$rowIndex", "No Urut", (index + 1).toString(), item)
                                }
                            )
                            DataCell(
                                value = item.date,
                                width = 95.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("B$rowIndex", "Tanggal", item.date, item)
                                }
                            )
                            DataCell(
                                value = item.time,
                                width = 60.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("C$rowIndex", "Jam", item.time, item)
                                }
                            )
                            DataCell(
                                value = item.village,
                                width = 130.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("D$rowIndex", "Desa / Wilayah", item.village, item)
                                }
                            )
                            DataCell(
                                value = item.locationDetail,
                                width = 140.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("E$rowIndex", "Dusun / Lokasi", item.locationDetail, item)
                                }
                            )
                            DataCell(
                                value = item.targetCategory,
                                width = 130.dp,
                                isBold = true,
                                onClick = {
                                    selectedCell = SelectedCellInfo("F$rowIndex", "Sasaran Komsos", item.targetCategory, item)
                                }
                            )
                            DataCell(
                                value = item.contactPerson,
                                width = 150.dp,
                                isBold = true,
                                onClick = {
                                    selectedCell = SelectedCellInfo("G$rowIndex", "Nama Tokoh / Warga", item.contactPerson, item)
                                }
                            )
                            DataCell(
                                value = item.contactRole,
                                width = 140.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("H$rowIndex", "Jabatan / Peran", item.contactRole, item)
                                }
                            )
                            DataCell(
                                value = item.topic,
                                width = 180.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("I$rowIndex", "Topik Pembahasan", item.topic, item)
                                }
                            )
                            DataCell(
                                value = item.summary,
                                width = 240.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("J$rowIndex", "Hasil Komsos", item.summary, item)
                                }
                            )
                            DataCell(
                                value = item.followUpAction,
                                width = 220.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("K$rowIndex", "Rencana Tindak Lanjut", item.followUpAction, item)
                                }
                            )
                            StatusDataCell(
                                status = item.status,
                                width = 110.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("L$rowIndex", "Status", item.status, item)
                                }
                            )
                            DataCell(
                                value = if (item.photoUri != null) "📷 Ada Foto" else "Tidak Ada",
                                width = 130.dp,
                                isBold = item.photoUri != null,
                                onClick = {
                                    selectedCell = SelectedCellInfo("M$rowIndex", "Dokumentasi Foto", if (item.photoUri != null) "Tersedia Lampiran Foto Dokumentasi" else "Tidak Ada Lampiran Foto", item)
                                }
                            )
                            DataCell(
                                value = item.babinsaName,
                                width = 140.dp,
                                onClick = {
                                    selectedCell = SelectedCellInfo("N$rowIndex", "Babinsa Pelapor", item.babinsaName, item)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Excel Bottom Sheet Summary / Formula Bar
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATUS BAR: ${filteredList.size} Kegiatan • Selesai: ${filteredList.count { it.status == "Selesai" }} • Monitor: ${filteredList.count { it.status != "Selesai" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
                Text(
                    text = "Excel Sheet 1 (Aktif)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1F4E24)
                )
            }
        }
    }

    // Cell Inspector Dialog when user taps a cell
    selectedCell?.let { cell ->
        AlertDialog(
            onDismissRequest = { selectedCell = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF1F4E24)
                )
            },
            title = {
                Text("Inspektur Sel [${cell.coordinate}]", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Kolom: ${cell.columnName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ExcelCellSelected),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = cell.value,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Kegiatan Terkait: ${cell.activity.title} (${cell.activity.village})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val act = cell.activity
                        selectedCell = null
                        onSelectActivity(act)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4E24))
                ) {
                    Text("Buka Detail Kegiatan")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCell = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun HeaderCell(label: String, title: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(34.dp)
            .border(0.5.dp, Color(0xFF133617))
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = "$label: $title",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = ExcelHeaderFg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DataCell(
    value: String,
    width: androidx.compose.ui.unit.Dp,
    isNumber: Boolean = false,
    isBold: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(46.dp)
            .border(0.5.dp, ExcelGridBorder)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = if (isNumber) Alignment.Center else Alignment.CenterStart
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 11.5.sp
            ),
            color = Color(0xFF222222),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatusDataCell(
    status: String,
    width: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val bg = if (status == "Selesai") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val fg = if (status == "Selesai") Color(0xFF2E7D32) else Color(0xFFE65100)

    Box(
        modifier = Modifier
            .width(width)
            .height(46.dp)
            .border(0.5.dp, ExcelGridBorder)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = bg,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = status,
                color = fg,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
