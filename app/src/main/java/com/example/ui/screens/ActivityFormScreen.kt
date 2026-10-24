package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.KomsosActivity
import com.example.ui.theme.ArmyContainer
import com.example.ui.theme.ArmyGreenDark
import com.example.ui.theme.ArmyGreenPrimary
import com.example.util.ImageStorageHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFormScreen(
    initialActivity: KomsosActivity?,
    viewModel: com.example.viewmodel.KomsosViewModel? = null,
    onSave: (
        id: Long,
        title: String,
        date: String,
        time: String,
        village: String,
        locationDetail: String,
        targetCategory: String,
        contactPerson: String,
        contactRole: String,
        topic: String,
        summary: String,
        followUpAction: String,
        status: String,
        photoUri: String?,
        babinsaName: String,
        koramilName: String
    ) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val nowTimeStr = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    var selectedTab by remember { androidx.compose.runtime.mutableIntStateOf(0) }

    var title by remember { mutableStateOf(initialActivity?.title ?: "") }
    var date by remember { mutableStateOf(initialActivity?.date ?: todayStr) }
    var time by remember { mutableStateOf(initialActivity?.time ?: nowTimeStr) }
    var village by remember { mutableStateOf(initialActivity?.village ?: "Desa Sukamaju") }
    var locationDetail by remember { mutableStateOf(initialActivity?.locationDetail ?: "") }
    var targetCategory by remember { mutableStateOf(initialActivity?.targetCategory ?: "Tokoh Masyarakat") }
    var contactPerson by remember { mutableStateOf(initialActivity?.contactPerson ?: "") }
    var contactRole by remember { mutableStateOf(initialActivity?.contactRole ?: "") }
    var topic by remember { mutableStateOf(initialActivity?.topic ?: "") }
    var summary by remember { mutableStateOf(initialActivity?.summary ?: "") }
    var followUpAction by remember { mutableStateOf(initialActivity?.followUpAction ?: "") }
    var status by remember { mutableStateOf(initialActivity?.status ?: "Selesai") }
    var photoUri by remember { mutableStateOf(initialActivity?.photoUri) }
    var babinsaName by remember { mutableStateOf(initialActivity?.babinsaName ?: "Serka Bambang Suryono") }
    var koramilName by remember { mutableStateOf(initialActivity?.koramilName ?: "Koramil 08/Binaan") }

    var titleError by remember { mutableStateOf(false) }
    var contactError by remember { mutableStateOf(false) }
    var summaryError by remember { mutableStateOf(false) }

    // Photo Picker Launcher using Android Photo Picker API (Zero Permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedLocalPath = ImageStorageHelper.saveImageToInternalStorage(context, uri)
            photoUri = savedLocalPath ?: uri.toString()
        }
    }

    val targetCategories = listOf(
        "Tokoh Agama",
        "Tokoh Masyarakat",
        "Tokoh Pemuda",
        "Aparat Desa",
        "Kelompok Tani",
        "Warga"
    )

    val statuses = listOf("Selesai", "Proses Tindak Lanjut", "Perlu Koordinasi")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = if (initialActivity != null) "Edit Kegiatan Komsos" else "Form Input Komsos Babinsa",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("form_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ArmyGreenPrimary
                    )
                )

                androidx.compose.material3.TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ArmyGreenDark,
                    contentColor = Color.White
                ) {
                    androidx.compose.material3.Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Form Komsos Firestore",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_activity_form")
                    )
                    androidx.compose.material3.Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Detail & Foto Lapangan",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_full_form")
                    )
                }
            }
        }
    ) { innerPadding ->
        if (selectedTab == 0) {
            // Dedicated ActivityForm composable fulfilling Date, Location, Description, Participant Count to Firestore
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ActivityForm(
                    viewModel = viewModel,
                    initialActivity = initialActivity,
                    onSaveToFirestore = { fDate: String, fLoc: String, fDesc: String, fCount: Int ->
                        if (viewModel != null) {
                            viewModel.saveKomsosActivityToFirestore(
                                date = fDate,
                                location = fLoc,
                                description = fDesc,
                                participantCount = fCount,
                                onSuccess = { onBack() },
                                onError = {
                                    // Fallback to onSave
                                    onSave(
                                        initialActivity?.id ?: 0L,
                                        "Komsos di $fLoc",
                                        fDate,
                                        nowTimeStr,
                                        fLoc,
                                        fLoc,
                                        "Tokoh Masyarakat",
                                        "Warga $fLoc",
                                        "Peserta Komsos",
                                        fDesc.take(50),
                                        fDesc,
                                        "Koordinasi lanjutan bersama warga binaan",
                                        "Selesai",
                                        null,
                                        babinsaName,
                                        koramilName
                                    )
                                }
                            )
                        } else {
                            onSave(
                                initialActivity?.id ?: 0L,
                                "Komsos di $fLoc",
                                fDate,
                                nowTimeStr,
                                fLoc,
                                fLoc,
                                "Tokoh Masyarakat",
                                "Warga $fLoc",
                                "Peserta Komsos",
                                fDesc.take(50),
                                fDesc,
                                "Koordinasi lanjutan bersama warga binaan",
                                "Selesai",
                                null,
                                babinsaName,
                                koramilName
                            )
                        }
                    },
                    onCancel = onBack,
                    onSaveSuccess = { onBack() }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .testTag("activity_form_screen"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Identitas Kegiatan
                Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Judul & Waktu Kegiatan",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        label = { Text("Judul Kegiatan Komsos *") },
                        placeholder = { Text("Cth: Silaturahmi & Koordinasi Siskamling") },
                        isError = titleError,
                        supportingText = if (titleError) { { Text("Judul wajib diisi") } } else null,
                        leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_title"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Quick topic suggestions
                    Text(
                        text = "Saran Judul Cepat:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = { title = "Silaturahmi & Dialog Kamtibmas" },
                            label = { Text("Kamtibmas", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { title = "Pendampingan Kelompok Tani Padi" },
                            label = { Text("Ketahanan Pangan", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { title = "Komsos Kerukunan Antar Umat" },
                            label = { Text("Kerukunan Umat", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { title = "Koordinasi Gotong Royong Saluran Air" },
                            label = { Text("Gotong Royong", fontSize = 11.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Tanggal (YYYY-MM-DD)") },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_input_date"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Jam (HH:mm)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_input_time"),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Section 2: Wilayah & Lokasi
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Lokasi & Wilayah Binaan",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = village,
                        onValueChange = { village = it },
                        label = { Text("Desa / Kelurahan Binaan") },
                        placeholder = { Text("Desa Sukamaju") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_village"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = locationDetail,
                        onValueChange = { locationDetail = it },
                        label = { Text("Dusun / RT / RW / Tempat Pertemuan") },
                        placeholder = { Text("Cth: Pos Ronda Dusun 2, RT 03/RW 01") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_location"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Section 3: Sasaran & Tokoh yang Ditemui
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Kategori Sasaran & Tokoh Masyarakat",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih Kategori Sasaran Komsos:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        targetCategories.forEach { cat ->
                            FilterChip(
                                selected = targetCategory == cat,
                                onClick = { targetCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ArmyContainer,
                                    selectedLabelColor = ArmyGreenDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = contactPerson,
                        onValueChange = {
                            contactPerson = it
                            contactError = false
                        },
                        label = { Text("Nama Tokoh / Narasumber Warga *") },
                        placeholder = { Text("Cth: Bpk. H. Mansur") },
                        isError = contactError,
                        supportingText = if (contactError) { { Text("Nama narasumber wajib diisi") } } else null,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_contact"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = contactRole,
                        onValueChange = { contactRole = it },
                        label = { Text("Jabatan / Peran di Lingkungan") },
                        placeholder = { Text("Cth: Ketua RW 04 / Pengurus DKM") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_role"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Section 4: Materi Dialog, Hasil & Tindak Lanjut
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Hasil Pembicaraan & Tindak Lanjut",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topik Pokok Pembahasan") },
                        placeholder = { Text("Cth: Penguatan Siskamling & Pengamanan Desa") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_topic"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = summary,
                        onValueChange = {
                            summary = it
                            summaryError = false
                        },
                        label = { Text("Hasil & Kesimpulan Komsos *") },
                        placeholder = { Text("Rangkuman hasil pertemuan, keluhan warga, atau kesepakatan...") },
                        isError = summaryError,
                        supportingText = if (summaryError) { { Text("Hasil komsos wajib diisi") } } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("form_input_summary"),
                        maxLines = 4,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = followUpAction,
                        onValueChange = { followUpAction = it },
                        label = { Text("Rencana Tindak Lanjut Babinsa") },
                        placeholder = { Text("Cth: Babinsa akan mendampingi patroli malam Sabtu ini...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("form_input_followup"),
                        maxLines = 3,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Status Pelaksanaan:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statuses.forEach { st ->
                            FilterChip(
                                selected = status == st,
                                onClick = { status = st },
                                label = { Text(st, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (st == "Selesai") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                    selectedLabelColor = if (st == "Selesai") Color(0xFF1B5E20) else Color(0xFFE65100)
                                )
                            )
                        }
                    }
                }
            }

            // Section 5: UPLOAD FOTO DOKUMENTASI KOMSOS (Fitur Permintaan Pengguna)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "5. Dokumentasi Foto Komsos",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ArmyGreenDark
                        )
                        if (photoUri != null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "Foto Terlampir",
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val currentPhoto = photoUri
                    if (currentPhoto != null) {
                        // Display photo preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFC0C7B8), RoundedCornerShape(8.dp))
                        ) {
                            if (currentPhoto.startsWith("res:") || currentPhoto.contains("drawable")) {
                                Image(
                                    painter = painterResource(id = R.drawable.komsos_hero_1792862664779),
                                    contentDescription = "Foto Dokumentasi",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                AsyncImage(
                                    model = if (currentPhoto.startsWith("/")) File(currentPhoto) else Uri.parse(currentPhoto),
                                    contentDescription = "Foto Dokumentasi Komsos",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_change_photo"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ganti Foto", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { photoUri = null },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_delete_photo"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Hapus Foto", fontSize = 12.sp)
                            }
                        }
                    } else {
                        // Empty photo placeholder with upload button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF7F9F5), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFCAD1C4), RoundedCornerShape(8.dp))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(vertical = 24.dp, horizontal = 16.dp)
                                .testTag("upload_photo_area"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(26.dp))
                                        .background(ArmyContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Upload Foto",
                                        tint = ArmyGreenPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Unggah Foto Dokumentasi Lapangan",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ArmyGreenDark
                                )
                                Text(
                                    text = "Pilih foto dokumentasi komsos dari galeri atau kamera perangkat",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ArmyGreenPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("btn_pick_photo")
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Buka Pemilih Foto (Photo Picker)")
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                // Quick sample option for testing in emulator
                                TextButton(
                                    onClick = {
                                        photoUri = "drawable/komsos_hero"
                                    },
                                    modifier = Modifier.testTag("btn_sample_photo")
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp), tint = ArmyGreenDark)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gunakan Contoh Foto Dokumentasi Komsos", fontSize = 11.sp, color = ArmyGreenDark)
                                }
                            }
                        }
                    }
                }
            }

            // Section 6: Data Petugas
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "6. Data Babinsa Pelapor",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = babinsaName,
                            onValueChange = { babinsaName = it },
                            label = { Text("Nama Babinsa") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = koramilName,
                            onValueChange = { koramilName = it },
                            label = { Text("Koramil") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Save Submit Button
            Button(
                onClick = {
                    var hasError = false
                    if (title.isBlank()) {
                        titleError = true
                        hasError = true
                    }
                    if (contactPerson.isBlank()) {
                        contactError = true
                        hasError = true
                    }
                    if (summary.isBlank()) {
                        summaryError = true
                        hasError = true
                    }
                    if (!hasError) {
                        onSave(
                            initialActivity?.id ?: 0L,
                            title,
                            date,
                            time,
                            village,
                            locationDetail,
                            targetCategory,
                            contactPerson,
                            contactRole,
                            topic,
                            summary,
                            followUpAction,
                            status,
                            photoUri,
                            babinsaName,
                            koramilName
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_activity"),
                colors = ButtonDefaults.buttonColors(containerColor = ArmyGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialActivity != null) "Perbarui Data Kegiatan" else "Simpan Kegiatan Komsos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

/**
 * ActivityForm Composable
 *
 * Dedicated Composable that allows users to input details for a Komsos activity
 * (Date, Location, Description, Participant Count) and save directly to Firebase Firestore.
 */
@Composable
fun ActivityForm(
    modifier: Modifier = Modifier,
    viewModel: com.example.viewmodel.KomsosViewModel? = null,
    initialActivity: KomsosActivity? = null,
    onSaveToFirestore: ((date: String, location: String, description: String, participantCount: Int) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onSaveSuccess: ((String) -> Unit)? = null
) {
    com.example.ui.components.ActivityForm(
        modifier = modifier,
        viewModel = viewModel,
        initialActivity = initialActivity,
        onSaveToFirestore = onSaveToFirestore,
        onCancel = onCancel,
        onSaveSuccess = onSaveSuccess
    )
}

