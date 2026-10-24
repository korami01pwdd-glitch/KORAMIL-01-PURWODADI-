package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import java.io.File
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KomsosActivity
import com.example.ui.theme.ArmyContainer
import com.example.ui.theme.ArmyGreenDark
import com.example.ui.theme.ArmyGreenPrimary
import com.example.ui.theme.OnArmyContainer
import com.example.util.SpreadsheetExporter
import com.example.viewmodel.Screen

@Composable
fun ActivityListScreen(
    activities: List<KomsosActivity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    categoryFilter: String,
    onCategoryChange: (String) -> Unit,
    statusFilter: String,
    onStatusChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (KomsosActivity) -> Unit,
    onDeleteClick: (KomsosActivity) -> Unit
) {
    val context = LocalContext.current
    var viewingActivity by remember { mutableStateOf<KomsosActivity?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<KomsosActivity?>(null) }

    val categories = listOf("Semua", "Tokoh Agama", "Tokoh Masyarakat", "Tokoh Pemuda", "Aparat Desa", "Kelompok Tani", "Warga")
    val statuses = listOf("Semua", "Selesai", "Proses Tindak Lanjut", "Perlu Koordinasi")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("activity_list_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari tokoh, topik, atau desa binaan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus pencarian")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("activity_search_field"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val selected = categoryFilter == cat
                    FilterChip(
                        selected = selected,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ArmyContainer,
                            selectedLabelColor = ArmyGreenDark
                        ),
                        modifier = Modifier.testTag("filter_cat_$cat")
                    )
                }
            }

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                statuses.forEach { st ->
                    val selected = statusFilter == st
                    FilterChip(
                        selected = selected,
                        onClick = { onStatusChange(st) },
                        label = { Text(st, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (st == "Selesai") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            selectedLabelColor = if (st == "Selesai") Color(0xFF1B5E20) else Color(0xFFE65100)
                        ),
                        modifier = Modifier.testTag("filter_status_$st")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Activities List or Empty State
            if (activities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak ada kegiatan Komsos yang cocok",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ArmyGreenDark
                        )
                        Text(
                            text = "Coba ubah kata kunci pencarian atau reset filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp)
                ) {
                    items(activities, key = { it.id }) { item ->
                        ActivityDetailCard(
                            activity = item,
                            onClick = { viewingActivity = item }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_activity"),
            containerColor = ArmyGreenPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Kegiatan Komsos")
        }
    }

    // Detail View Dialog
    viewingActivity?.let { act ->
        AlertDialog(
            onDismissRequest = { viewingActivity = null },
            title = {
                Text(
                    text = act.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ArmyGreenDark
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ArmyContainer
                            ) {
                                Text(
                                    text = act.targetCategory,
                                    color = OnArmyContainer,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (act.status == "Selesai") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                            ) {
                                Text(
                                    text = act.status,
                                    color = if (act.status == "Selesai") Color(0xFF2E7D32) else Color(0xFFE65100),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        // Display photo documentation if available
                        if (act.photoUri != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Dokumentasi Foto Komsos Lapangan:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ArmyGreenDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF0F3EC))
                            ) {
                                if (act.photoUri.startsWith("res:") || act.photoUri.contains("drawable")) {
                                    Image(
                                        painter = painterResource(id = R.drawable.komsos_hero_1792862664779),
                                        contentDescription = "Foto Dokumentasi",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    AsyncImage(
                                        model = if (act.photoUri.startsWith("/")) File(act.photoUri) else Uri.parse(act.photoUri),
                                        contentDescription = "Foto Dokumentasi Komsos",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        DetailItemRow(label = "Waktu Pelaksanaan", value = "${act.date} pukul ${act.time} WIB")
                        DetailItemRow(label = "Wilayah Binaan", value = "${act.village} (${act.locationDetail})")
                        DetailItemRow(label = "Tokoh / Narasumber", value = "${act.contactPerson} - ${act.contactRole}")
                        DetailItemRow(label = "Topik Pembahasan", value = act.topic)
                        DetailItemRow(label = "Hasil & Kesimpulan", value = act.summary)
                        DetailItemRow(label = "Rencana Tindak Lanjut", value = act.followUpAction)
                        DetailItemRow(
                            label = "Dokumentasi Foto",
                            value = if (act.photoUri != null) "Tersedia & Terlampir (1 Foto)" else "Tidak ada lampiran foto"
                        )
                        DetailItemRow(label = "Petugas Babinsa", value = "${act.babinsaName} (${act.koramilName})")

                        Spacer(modifier = Modifier.height(12.dp))
                        // Action row inside detail dialog
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    SpreadsheetExporter.shareSpreadsheet(context, listOf(act), asXmlExcel = true)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ekspor Excel", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    val itemToEdit = act
                                    viewingActivity = null
                                    onEditClick(itemToEdit)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Data", fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewingActivity = null }) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = ArmyGreenPrimary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = act
                        viewingActivity = null
                    }
                ) {
                    Text("Hapus", color = Color.Red)
                }
            }
        )
    }

    // Delete confirmation dialog
    showDeleteConfirm?.let { act ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
            title = { Text("Konfirmasi Hapus Kegiatan") },
            text = { Text("Apakah Anda yakin ingin menghapus data Komsos '${act.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick(act)
                        showDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF222222)
        )
    }
}

@Composable
fun ActivityDetailCard(
    activity: KomsosActivity,
    onClick: () -> Unit
) {
    val statusBg = if (activity.status == "Selesai") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusFg = if (activity.status == "Selesai") Color(0xFF2E7D32) else Color(0xFFE65100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("activity_item_${activity.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ArmyContainer
                ) {
                    Text(
                        text = activity.targetCategory,
                        color = OnArmyContainer,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusBg
                ) {
                    Text(
                        text = activity.status,
                        color = statusFg,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ArmyGreenDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = ArmyGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${activity.contactPerson} (${activity.contactRole})",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF2E2E2E)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Hasil: ${activity.summary}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                maxLines = 2
            )
            if (activity.photoUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 72.dp, height = 50.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE5E9E0))
                    ) {
                        if (activity.photoUri.startsWith("res:") || activity.photoUri.contains("drawable")) {
                            Image(
                                painter = painterResource(id = R.drawable.komsos_hero_1792862664779),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AsyncImage(
                                model = if (activity.photoUri.startsWith("/")) File(activity.photoUri) else Uri.parse(activity.photoUri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = ArmyGreenPrimary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Foto Dokumentasi Terlampir", color = ArmyGreenDark, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${activity.date} ${activity.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${activity.village} (${activity.locationDetail})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
