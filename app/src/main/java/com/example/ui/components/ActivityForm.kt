package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.KomsosActivity
import com.example.ui.theme.ArmyGreenDark
import com.example.ui.theme.ArmyGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.viewmodel.KomsosViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ActivityForm Composable
 *
 * Allows users to input details for a Komsos activity:
 * - Date (Tanggal Kegiatan)
 * - Location (Lokasi / Wilayah Binaan)
 * - Description (Deskripsi / Uraian Kegiatan)
 * - Participant Count (Jumlah Peserta)
 *
 * Saves entered activity directly to Firebase Firestore under the authenticated user's subcollection.
 */
@Composable
fun ActivityForm(
    modifier: Modifier = Modifier,
    viewModel: KomsosViewModel? = null,
    initialActivity: KomsosActivity? = null,
    onSaveToFirestore: ((date: String, location: String, description: String, participantCount: Int) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onSaveSuccess: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val databaseId = stringResource(id = R.string.firestore_database_id)

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var date by remember { mutableStateOf(initialActivity?.date ?: todayStr) }
    var location by remember { mutableStateOf(initialActivity?.village?.ifBlank { initialActivity.locationDetail } ?: "") }
    var description by remember { mutableStateOf(initialActivity?.summary?.ifBlank { initialActivity.topic } ?: "") }
    var participantCount by remember { mutableIntStateOf(initialActivity?.participantCount ?: 1) }

    // Optional category and status for enhanced TNI AD Komsos reporting
    var targetCategory by remember { mutableStateOf(initialActivity?.targetCategory ?: "Tokoh Masyarakat") }
    var status by remember { mutableStateOf(initialActivity?.status ?: "Selesai") }

    // Validation error states
    var dateError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var participantCountError by remember { mutableStateOf<String?>(null) }

    // Async state
    var isSaving by remember { mutableStateOf(false) }
    var firestoreSuccessDocId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun validateInputs(): Boolean {
        var isValid = true
        if (date.isBlank()) {
            dateError = "Tanggal wajib diisi (Format: YYYY-MM-DD)"
            isValid = false
        } else {
            dateError = null
        }

        if (location.isBlank()) {
            locationError = "Lokasi kegiatan wajib diisi"
            isValid = false
        } else {
            locationError = null
        }

        if (description.isBlank()) {
            descriptionError = "Deskripsi / uraian kegiatan wajib diisi"
            isValid = false
        } else if (description.trim().length < 5) {
            descriptionError = "Deskripsi minimal 5 karakter"
            isValid = false
        } else {
            descriptionError = null
        }

        if (participantCount < 1) {
            participantCountError = "Jumlah peserta minimal 1 orang"
            isValid = false
        } else {
            participantCountError = null
        }

        return isValid
    }

    fun handleSave() {
        if (!validateInputs()) return

        isSaving = true
        errorMessage = null

        // 1. If explicit callback was provided, invoke it
        if (onSaveToFirestore != null) {
            onSaveToFirestore(date.trim(), location.trim(), description.trim(), participantCount)
            isSaving = false
            return
        }

        // 2. If KomsosViewModel is available, use its dual-sync save method
        if (viewModel != null) {
            viewModel.saveKomsosActivityToFirestore(
                date = date.trim(),
                location = location.trim(),
                description = description.trim(),
                participantCount = participantCount,
                onSuccess = { docId ->
                    isSaving = false
                    firestoreSuccessDocId = docId
                    Toast.makeText(context, "Berhasil disimpan ke Firestore!", Toast.LENGTH_SHORT).show()
                    onSaveSuccess?.invoke(docId)
                },
                onError = { err ->
                    isSaving = false
                    errorMessage = err.localizedMessage ?: "Gagal menyimpan ke Firestore"
                }
            )
            return
        }

        // 3. Fallback direct Firestore write using FirebaseAuth and FirebaseFirestore
        scope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    errorMessage = "User belum login. Harap login terlebih dahulu."
                    isSaving = false
                    return@launch
                }

                val firestore = FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
                val collection = firestore.collection("users").document(uid).collection("komsos_activities")
                val docRef = collection.document()

                val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val activityMap = hashMapOf<String, Any?>(
                    "title" to "Komsos di ${location.trim()}",
                    "date" to date.trim(),
                    "time" to nowTime,
                    "village" to location.trim(),
                    "locationDetail" to location.trim(),
                    "targetCategory" to targetCategory,
                    "contactPerson" to "Peserta Komsos",
                    "contactRole" to "Masyarakat",
                    "topic" to description.take(60).trim(),
                    "summary" to description.trim(),
                    "followUpAction" to "Koordinasi berkala bersama warga binaan",
                    "status" to status,
                    "participantCount" to participantCount,
                    "babinsaName" to (auth.currentUser?.displayName ?: "Serka Bambang Suryono"),
                    "koramilName" to "Koramil 08/Binaan",
                    "createdAt" to System.currentTimeMillis(),
                    "userId" to uid,
                    "photoUri" to null
                )

                docRef.set(activityMap).await()
                isSaving = false
                firestoreSuccessDocId = docRef.id
                Toast.makeText(context, "Data Komsos berhasil disimpan ke Firestore!", Toast.LENGTH_SHORT).show()
                onSaveSuccess?.invoke(docRef.id)
            } catch (e: Exception) {
                isSaving = false
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menyimpan ke Firestore"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("activity_form"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Header Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ArmyGreenPrimary),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoldAccent
                    ) {
                        Text(
                            text = "TNI AD • Babinsa",
                            color = Color(0xFF1B2A17),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0x33FFFFFF)
                    ) {
                        Text(
                            text = "Firestore Cloud Sync",
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Input Kegiatan Komsos",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Text(
                    text = "Catat rincian Tanggal, Lokasi, Deskripsi, dan Jumlah Peserta untuk sinkronisasi ke Firebase Firestore.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE2EBD8)
                )
            }
        }

        // Success Alert Banner
        AnimatedVisibility(visible = firestoreSuccessDocId != null) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("firestore_success_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Berhasil",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Berhasil Disimpan ke Firestore!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Document ID: ${firestoreSuccessDocId ?: ""}",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        // Error Alert Banner
        AnimatedVisibility(visible = errorMessage != null) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("firestore_error_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Error",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFB71C1C),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Section: Form Inputs
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // 1. DATE (Tanggal Kegiatan)
                Column {
                    Text(
                        text = "Tanggal Kegiatan (Date) *",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = date,
                        onValueChange = {
                            date = it
                            dateError = null
                        },
                        placeholder = { Text("YYYY-MM-DD (cth: 2026-10-24)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Tanggal",
                                tint = ArmyGreenPrimary
                            )
                        },
                        isError = dateError != null,
                        supportingText = dateError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_date")
                            .testTag("activity_form_date_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGreenPrimary,
                            cursorColor = ArmyGreenPrimary
                        )
                    )

                    // Quick date helpers
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { date = todayStr; dateError = null },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_quick_date_today"),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                        ) {
                            Text("Hari Ini ($todayStr)", fontSize = 11.sp, color = ArmyGreenDark)
                        }
                    }
                }

                // 2. LOCATION (Lokasi Kegiatan)
                Column {
                    Text(
                        text = "Lokasi Kegiatan (Location) *",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = {
                            location = it
                            locationError = null
                        },
                        placeholder = { Text("Cth: Balai Desa Sukamaju / Pos Ronda Dusun 2") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Lokasi",
                                tint = ArmyGreenPrimary
                            )
                        },
                        isError = locationError != null,
                        supportingText = locationError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_location")
                            .testTag("activity_form_location_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGreenPrimary,
                            cursorColor = ArmyGreenPrimary
                        )
                    )

                    // Quick location suggestions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Balai Desa", "Pos Ronda", "Rumah Warga", "Lahan Pertanian", "Tempat Ibadah").forEach { locSample ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5EB),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFD6DEC9), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = locSample,
                                    fontSize = 11.sp,
                                    color = ArmyGreenDark,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clickable {
                                            location = locSample
                                            locationError = null
                                        }
                                )
                            }
                        }
                    }
                }

                // 3. DESCRIPTION (Uraian Kegiatan)
                Column {
                    Text(
                        text = "Uraian / Deskripsi Kegiatan (Description) *",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            descriptionError = null
                        },
                        placeholder = { Text("Rincian materi dialog, kesepakatan warga, aspirasi, atau koordinasi kamtibmas...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Deskripsi",
                                tint = ArmyGreenPrimary
                            )
                        },
                        isError = descriptionError != null,
                        supportingText = descriptionError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("input_description")
                            .testTag("activity_form_description_input"),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGreenPrimary,
                            cursorColor = ArmyGreenPrimary
                        )
                    )

                    // Quick template chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Silaturahmi Kamtibmas",
                            "Pendampingan Tani",
                            "Kerukunan Warga",
                            "Gotong Royong Lingkungan"
                        ).forEach { sampleDesc ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5EB),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFD6DEC9), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = sampleDesc,
                                    fontSize = 11.sp,
                                    color = ArmyGreenDark,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clickable {
                                            description = "Melaksanakan komsos dalam rangka $sampleDesc bersama warga setempat."
                                            descriptionError = null
                                        }
                                )
                            }
                        }
                    }
                }

                // 4. PARTICIPANT COUNT (Jumlah Peserta)
                Column {
                    Text(
                        text = "Jumlah Peserta (Participant Count) *",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ArmyGreenDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Decrement Button
                        IconButton(
                            onClick = {
                                if (participantCount > 1) {
                                    participantCount--
                                    participantCountError = null
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8EFE3))
                                .testTag("btn_decrement_participants")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Kurang peserta",
                                tint = ArmyGreenDark
                            )
                        }

                        // Number TextField
                        OutlinedTextField(
                            value = participantCount.toString(),
                            onValueChange = { raw ->
                                val parsed = raw.filter { it.isDigit() }.toIntOrNull()
                                if (parsed != null) {
                                    participantCount = parsed
                                    participantCountError = null
                                } else if (raw.isBlank()) {
                                    participantCount = 0
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = "Peserta",
                                    tint = ArmyGreenPrimary
                                )
                            },
                            trailingIcon = {
                                Text(
                                    text = "Orang",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = participantCountError != null,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_participant_count")
                                .testTag("activity_form_participant_count_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ArmyGreenPrimary,
                                cursorColor = ArmyGreenPrimary
                            )
                        )

                        // Increment Button
                        IconButton(
                            onClick = {
                                participantCount++
                                participantCountError = null
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8EFE3))
                                .testTag("btn_increment_participants")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah peserta",
                                tint = ArmyGreenDark
                            )
                        }
                    }

                    if (participantCountError != null) {
                        Text(
                            text = participantCountError ?: "",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Quick participant presets
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(2, 5, 10, 25, 50).forEach { preset ->
                            FilterChip(
                                selected = participantCount == preset,
                                onClick = {
                                    participantCount = preset
                                    participantCountError = null
                                },
                                label = { Text("$preset Orang", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ArmyGreenPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Supporting Komsos Category Selector
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "Sasaran Komsos:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Tokoh Masyarakat",
                            "Tokoh Agama",
                            "Tokoh Pemuda",
                            "Aparat Desa",
                            "Kelompok Tani",
                            "Warga"
                        ).forEach { cat ->
                            FilterChip(
                                selected = targetCategory == cat,
                                onClick = { targetCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE1EBD8),
                                    selectedLabelColor = ArmyGreenDark
                                )
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Save to Firestore & Cancel
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { handleSave() },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_activity_button")
                    .testTag("activity_form_save_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArmyGreenPrimary,
                    contentColor = Color.White
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Menyimpan ke Firestore...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Simpan",
                        tint = GoldAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simpan ke Firestore",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (onCancel != null) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("activity_form_cancel_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kembali", color = ArmyGreenDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
