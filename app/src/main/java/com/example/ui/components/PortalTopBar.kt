package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ArmyGreenDark
import com.example.ui.theme.ArmyGreenPrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalTopBar(
    title: String = "PORTAL KOMSOS BABINSA",
    subtitle: String = "TNI AD • Pembinaan Teritorial",
    onExportClick: (() -> Unit)? = null
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Emblem TNI AD",
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = "WEB",
                                color = Color.Black,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Portal Web Online • Siap Akses Browser (Tanpa Play Store)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        },
        actions = {
            if (onExportClick != null) {
                IconButton(
                    onClick = onExportClick,
                    modifier = Modifier.testTag("top_bar_export_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = "Ekspor Spreadsheet Excel",
                        tint = Color.White
                    )
                }
            }
            IconButton(
                onClick = { showInfoDialog = true },
                modifier = Modifier.testTag("top_bar_info_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informasi Portal",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ArmyGreenPrimary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )

    if (showInfoDialog) {
        Dialog(onDismissRequest = { showInfoDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = ArmyGreenPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Portal Komsos Babinsa",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = ArmyGreenDark
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = "Aplikasi portal berbasis Web Dashboard untuk menunjang kegiatan dan pembelajaran Komunikasi Sosial (Komsos) Bintara Pembina Desa (Babinsa) TNI AD.\n\n🌐 Mode Web Portal (Tanpa Play Store):\nAplikasi ini dibuka dan dioperasikan langsung melalui Web Browser di laptop/komputer atau ponsel tanpa perlu mengunduh dari Google Play Store.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Fitur Utama:\n• Akses Web Portal langsung di browser\n• Pencatatan kegiatan Komsos & upload foto dokumentasi\n• Pratinjau tabel interaktif ala Spreadsheet Excel\n• Ekspor data langsung ke file Excel (.xls) & CSV UTF-8\n• Modul materi pelajaran doktrin & metode Komsos\n• Kuis uji pemahaman Binter Babinsa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        androidx.compose.material3.TextButton(onClick = { showInfoDialog = false }) {
                            Text("Tutup", fontWeight = FontWeight.Bold, color = ArmyGreenPrimary)
                        }
                    }
                }
            }
        }
    }
}
