package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "komsos_activities")
data class KomsosActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String, // format YYYY-MM-DD
    val time: String, // format HH:mm
    val village: String, // Desa / Kelurahan Binaan
    val locationDetail: String, // Dusun / RW / RT / Tempat
    val targetCategory: String, // Tokoh Agama, Tokoh Masyarakat, Tokoh Pemuda, Aparat Desa, Kelompok Tani, Warga
    val contactPerson: String, // Nama Tokoh / Warga
    val contactRole: String, // Jabatan / Posisi
    val topic: String, // Topik Bahasan
    val summary: String, // Hasil / Kesimpulan Komsos
    val followUpAction: String, // Rencana Tindak Lanjut
    val status: String = "Selesai", // Selesai, Proses Tindak Lanjut, Perlu Koordinasi
    val photoUri: String? = null, // URI atau path foto dokumentasi komsos
    val babinsaName: String = "Serka Bambang Suryono",
    val koramilName: String = "Koramil 08/Binaan",
    val createdAt: Long = System.currentTimeMillis()
)
