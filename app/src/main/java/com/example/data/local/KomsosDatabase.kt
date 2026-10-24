package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.KomsosActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [KomsosActivity::class], version = 2, exportSchema = false)
abstract class KomsosDatabase : RoomDatabase() {
    abstract fun komsosDao(): KomsosDao

    companion object {
        @Volatile
        private var INSTANCE: KomsosDatabase? = null

        fun getDatabase(context: Context): KomsosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KomsosDatabase::class.java,
                    "komsos_database.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with realistic seed data
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.komsosDao()?.insertAll(initialSampleActivities)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        val initialSampleActivities = listOf(
            KomsosActivity(
                title = "Silaturahmi & Koordinasi Siskamling Malam",
                date = "2026-10-24",
                time = "19:30",
                village = "Desa Sukamaju",
                locationDetail = "Pos Ronda RW 04, Dusun Mawar",
                targetCategory = "Tokoh Masyarakat",
                contactPerson = "Bpk. Subagio",
                contactRole = "Ketua RW 04 & Sesepuh Warga",
                topic = "Keamanan Lingkungan & Jadwal Ronda Warga",
                summary = "Membahas pengaktifan kembali ronda malam secara bergiliran menyusul maraknya pencurian hewan ternak di wilayah perbatasan desa.",
                followUpAction = "Babinsa akan turut mendampingi jadwal ronda malam pada hari Sabtu dan berkoordinasi dengan Bhabinkamtibmas.",
                status = "Proses Tindak Lanjut",
                photoUri = "drawable/komsos_hero",
                babinsaName = "Serka Bambang Suryono",
                koramilName = "Koramil 08/Binaan"
            ),
            KomsosActivity(
                title = "Komsos Kerukunan Umat & Pencegahan Radikalisme",
                date = "2026-10-23",
                time = "10:00",
                village = "Desa Sukamaju",
                locationDetail = "Serambi Masjid Jami Al-Ikhlas Dusun 1",
                targetCategory = "Tokoh Agama",
                contactPerson = "Ustadz H. Mansur",
                contactRole = "Ketua MUI Tingkat Desa / Tokoh Agama",
                topic = "Kerukunan Beragama & Pembinaan Remaja Masjid",
                summary = "Mendiskusikan materi khutbah dan kajian remaja untuk memperkuat ukhuwah islamiyah dan wawasan kebangsaan bela negara.",
                followUpAction = "Mengagendakan sesi penyuluhan wawasan kebangsaan bersama Babinsa untuk remaja masjid pada awal bulan depan.",
                status = "Selesai",
                babinsaName = "Serka Bambang Suryono",
                koramilName = "Koramil 08/Binaan"
            ),
            KomsosActivity(
                title = "Pendampingan Kelompok Tani Ketahanan Pangan",
                date = "2026-10-22",
                time = "08:30",
                village = "Desa Karangtengah",
                locationDetail = "Balai Pertemuan Tani 'Makmur Sejahtera'",
                targetCategory = "Kelompok Tani",
                contactPerson = "Bpk. Sugeng Wibowo",
                contactRole = "Ketua Gapoktan Makmur Sejahtera",
                topic = "Alokasi Pupuk Subsidi & Normalisasi Saluran Irigasi",
                summary = "Petani mengeluhkan keterlambatan distribusi pupuk urea bersubsidi dan pendangkalan saluran irigasi sekunder sepanjang 400 meter.",
                followUpAction = "Babinsa telah berkoordinasi dengan PPL Dinas Pertanian Kecamatan dan merencanakan kerja bakti normalisasi irigasi.",
                status = "Perlu Koordinasi",
                babinsaName = "Serka Bambang Suryono",
                koramilName = "Koramil 08/Binaan"
            ),
            KomsosActivity(
                title = "Koordinasi Tiga Pilar Persiapan Musrenbangdes",
                date = "2026-10-21",
                time = "14:00",
                village = "Desa Sukamaju",
                locationDetail = "Kantor Balai Desa Sukamaju",
                targetCategory = "Aparat Desa",
                contactPerson = "Bpk. Suryanto, S.Sos",
                contactRole = "Kepala Desa Sukamaju",
                topic = "Sinkronisasi Pembangunan Desa & Rawan Bencana",
                summary = "Penyusunan usulan skala prioritas pembangunan jalan rabat beton dan penguatan tebing rawan longsor di Dusun 3.",
                followUpAction = "Memasukkan rencana normalisasi tebing dan pelatihan tanggap bencana warga ke usulan Musrenbang tingkat kecamatan.",
                status = "Selesai",
                babinsaName = "Serka Bambang Suryono",
                koramilName = "Koramil 08/Binaan"
            ),
            KomsosActivity(
                title = "Pembinaan Pemuda & Karang Taruna Desa",
                date = "2026-10-19",
                time = "16:00",
                village = "Desa Karangtengah",
                locationDetail = "Lapangan Bola Voli Pemuda RT 02",
                targetCategory = "Tokoh Pemuda",
                contactPerson = "Rizki Pratama",
                contactRole = "Ketua Karang Taruna Bhakti Karya",
                topic = "Pencegahan Narkoba & Turnamen Olahraga Desa",
                summary = "Mendorong pemuda menyalurkan energi positif melalui kompetisi olahraga antar dusun guna membentengi pemuda dari bahaya narkoba dan judi online.",
                followUpAction = "Babinsa bertindak sebagai penasehat kepanitiaan turnamen bola voli Piala Babinsa Cup tingkat desa.",
                status = "Proses Tindak Lanjut",
                babinsaName = "Serka Bambang Suryono",
                koramilName = "Koramil 08/Binaan"
            ),
            KomsosActivity(
                title = "Pendampingan Posyandu & Pencegahan Stunting",
                date = "2026-10-17",
                time = "09:00",
                village = "Desa Sukamaju",
                locationDetail = "Posyandu Melati Dusun Melati",
                targetCategory = "Warga",
                contactPerson = "Ibu Siti Rahmawati, A.Md.Keb",
                contactRole = "Bidan Desa & Koordinator Kader Posyandu",
                topic = "Pemantauan Balita Berisiko Stunting & PMT",
                summary = "Pencatatan 5 balita dengan indikasi berat badan kurang serta distribusi paket makanan tambahan bergizi bantuan Koramil dan Puskesmas.",
                followUpAction = "Kunjungan 'Babinsa Masuk Dapur' ke rumah keluarga balita sasaran untuk memastikan asupan gizi mencukupi.",
                status = "Selesai",
                babinsaName = "Serka Bambang Suryono",
                koramilName = "Koramil 08/Binaan"
            )
        )
    }
}
