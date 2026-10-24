package com.example.data.model

data class LessonModule(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val durationMinutes: Int,
    val summary: String,
    val sections: List<LessonSection>,
    val tips: List<String>
)

data class LessonSection(
    val heading: String,
    val content: String,
    val bulletPoints: List<String> = emptyList()
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object KomsosEducationData {
    val modules = listOf(
        LessonModule(
            id = "modul_1",
            number = 1,
            title = "Hakikat & Tujuan Komsos TNI AD",
            subtitle = "Doktrin dasar Komunikasi Sosial bagi Bintara Pembina Desa",
            iconName = "military_tech",
            durationMinutes = 15,
            summary = "Komunikasi Sosial (Komsos) merupakan salah satu metode Pembinaan Teritorial (Binter) yang diselenggarakan oleh TNI AD bersama seluruh komponen bangsa.",
            sections = listOf(
                LessonSection(
                    heading = "1. Pengertian Komsos",
                    content = "Komsos adalah sarana interaksi langsung antara prajurit TNI AD (khususnya Babinsa) dengan masyarakat untuk mewujudkan saling pengertian, kebersamaan, dan kemanunggalan TNI dengan Rakyat.",
                    bulletPoints = listOf(
                        "Sarana memperkokoh persatuan dan kesatuan bangsa di tingkat desa/kelurahan.",
                        "Wadah pertukaran informasi dini mengenai situasi keamanan wilayah.",
                        "Menjaga citra positif TNI AD sebagai tentara rakyat, pejuang, dan profesional."
                    )
                ),
                LessonSection(
                    heading = "2. Tujuan Strategis Komsos",
                    content = "Komsos bukan sekadar obrolan biasa, melainkan memiliki tujuan strategis teritorial yang terukur:",
                    bulletPoints = listOf(
                        "Membangun Ruang, Alat, dan Kondisi Juang yang tangguh di wilayah darat.",
                        "Mencegah potensi konflik horizontal dan ancaman radikalisme sejak dini (Deteksi Dini & Cegah Dini).",
                        "Mendukung program pembangunan pemerintah daerah dan kesejahteraan masyarakat binaan."
                    )
                )
            ),
            tips = listOf(
                "Senyum, Salam, Sapa (3S) wajib diterapkan saat bertatap muka dengan warga.",
                "Pahami adat istiadat dan kearifan lokal sebelum memulai dialog formal.",
                "Hindari kesan menggurui; posisikan Babinsa sebagai pendengar yang solutif."
            )
        ),
        LessonModule(
            id = "modul_2",
            number = 2,
            title = "5 Kemampuan Teritorial Babinsa",
            subtitle = "Pilar profesionalisme pembinaan wilayah teritorial",
            iconName = "shield",
            durationMinutes = 20,
            summary = "Babinsa harus menguasai 5 Kemampuan Teritorial untuk menjalankan tugas pembinaan desa secara optimal dan akuntabel.",
            sections = listOf(
                LessonSection(
                    heading = "5 Kemampuan Teritorial Pokok",
                    content = "Kelima kemampuan ini saling terkait erat dalam setiap langkah penugasan lapangan:",
                    bulletPoints = listOf(
                        "1. Kemampuan Temu Cepat dan Lapor Cepat: Kecepatan menjaring informasi di lapangan dan melaporkan ke Danramil.",
                        "2. Kemampuan Manajemen Teritorial: Mengelola data geografi, demografi, dan kondisi sosial wilayah (Geo, Demo, Konsos).",
                        "3. Kemampuan Penguasaan Wilayah: Menguasai seluk-beluk geografi, titik rawan bencana, dan dinamika warga.",
                        "4. Kemampuan Pembinaan Perlawanan Wilayah (Bintahwil): Membina ketahanan mental warga, siskamling, dan bela negara.",
                        "5. Kemampuan Komunikasi Sosial (Komsos): Menjalin komunikasi harmonis dengan seluruh elemen masyarakat."
                    )
                ),
                LessonSection(
                    heading = "Keterkaitan Komsos dengan Lapor Cepat",
                    content = "Dengan Komsos yang intensif, warga desa akan secara sukarela dan cepat menyampaikan setiap kejanggalan atau potensi masalah kepada Babinsa sebelum membesar.",
                    bulletPoints = listOf(
                        "Warga menjadi mata dan telinga pertahanan teritorial.",
                        "Laporan kegiatan komsos harus segera ditindaklanjuti dan direkap dalam spreadsheet berkala."
                    )
                )
            ),
            tips = listOf(
                "Selalu bawa buku saku catatan lapangan saat patroli Komsos.",
                "Jaga kerahasiaan identitas narasumber warga yang memberikan informasi sensitif.",
                "Sinergikan langkah bersama Bhabinkamtibmas dan Kepala Desa (Tiga Pilar Desa)."
            )
        ),
        LessonModule(
            id = "modul_3",
            number = 3,
            title = "Metode Komsos & Pendekatan Efektif",
            subtitle = "Teknik anjangsana, silaturahmi, dan komunikasi dialogis",
            iconName = "record_voice_over",
            durationMinutes = 20,
            summary = "Metode pelaksanaan Komsos lapangan disesuaikan dengan situasi sosial budaya warga masyarakat setempat.",
            sections = listOf(
                LessonSection(
                    heading = "Metode Pokok Komsos",
                    content = "Ada beberapa metode utama yang lazim digunakan Babinsa:",
                    bulletPoints = listOf(
                        "Anjangsana: Berkunjung ke rumah warga, tokoh, pos ronda, ladang, atau warung kopi untuk menjalin keakraban.",
                        "Komunikasi Dialogis: Dialog interaktif dua arah tanpa jarak hierarki yang kaku.",
                        "Komsos Kreatif: Melalui kegiatan positif seperti olahraga bersama, gotong royong, atau lomba desa.",
                        "Pendekatan Kultural: Menghormati adat tradisi lokal (kenduri, tahlilan, kerja bakti)."
                    )
                ),
                LessonSection(
                    heading = "Etika & Sikap Teritorial Babinsa",
                    content = "Prajurit harus memegang teguh 8 Wajib TNI dalam setiap butir interaksi:",
                    bulletPoints = listOf(
                        "Bersikap ramah tamah terhadap rakyat.",
                        "Bersikap sopan santun terhadap rakyat.",
                        "Menjunjung tinggi kehormatan wanita.",
                        "Menjaga kehormatan diri di muka umum.",
                        "Senantiasa menjadi contoh dalam sikap dan kesederhanaannya.",
                        "Tidak sekali-kali merugikan rakyat.",
                        "Tidak sekali-kali menakuti dan menyakiti hati rakyat.",
                        "Menjadi contoh dan memelopori usaha-usaha untuk mengatasi kesulitan rakyat sekelilingnya."
                    )
                )
            ),
            tips = listOf(
                "Duduk sejajar dengan warga binaan, jangan bersikap angkuh.",
                "Minum sajian kopi/teh yang disuguhkan warga dengan rasa syukur dan kekeluargaan.",
                "Fokuskan pembicaraan pada hal-hal yang bermanfaat bagi kesejahteraan kampung."
            )
        ),
        LessonModule(
            id = "modul_4",
            number = 4,
            title = "Pemetaan Sasaran Komunikasi Sosial",
            subtitle = "Strategi pendekatan Tokoh Agama, Tokoh Masyarakat, Pemuda, & Warga",
            iconName = "groups",
            durationMinutes = 15,
            summary = "Setiap kelompok sasaran memiliki peran dan karakteristik unik yang membutuhkan cara pendekatan komunikasi tersendiri.",
            sections = listOf(
                LessonSection(
                    heading = "Klasifikasi Sasaran Komsos",
                    content = "Dalam sistem pelaporan dan pembinaan teritorial, sasaran dikelompokkan menjadi:",
                    bulletPoints = listOf(
                        "Tokoh Agama (Toga): Kiai, Pendeta, Ustadz, Pengurus DKM/Gereja - perannya menjaga kerukunan umat.",
                        "Tokoh Masyarakat (Tomas): Sesepuh desa, purnawirawan, pensiunan guru - pembentuk opini publik.",
                        "Tokoh Pemuda (Toda): Pengurus Karang Taruna, remaja masjid/gereja - agen penggerak kegiatan desa.",
                        "Tokoh Adat (Todat): Ketua adat, pemangku budaya lokal - penjaga kearifan tradisi.",
                        "Aparat Pemerintah Desa: Kepala Desa, Sekdes, Kepala Dusun, Ketua RT/RW - pengambil kebijakan desa.",
                        "Kelompok Warga (Petani/Pedagang/Buruh): Penggerak roda ekonomi riil di pedesaan."
                    )
                ),
                LessonSection(
                    heading = "Strategi Khusus Sasaran",
                    content = "Gunakan topik yang relevan dengan masing-masing kelompok:",
                    bulletPoints = listOf(
                        "Untuk Tokoh Pemuda: Dorong kegiatan olahraga, keahlian digital, dan pencegahan narkoba.",
                        "Untuk Kelompok Tani: Bahas ketersediaan bibit, pupuk bersubsidi, dan saluran irigasi.",
                        "Untuk Aparat Desa: Sinkronisasi program gotong royong dan poskamling."
                    )
                )
            ),
            tips = listOf(
                "Catat nama lengkap, nomor kontak, dan alamat tokoh kunci di buku saku.",
                "Lakukan rotasi silaturahmi secara seimbang agar tidak timbul kecemburuan sosial.",
                "Rangkul pemuda yang berpotensi agar terhindar dari pergaulan negatif."
            )
        ),
        LessonModule(
            id = "modul_5",
            number = 5,
            title = "Penyusunan Laporan & Output Excel",
            subtitle = "Tata cara pencatatan terstruktur dan rekapitulasi spreadsheet",
            iconName = "table_chart",
            durationMinutes = 15,
            summary = "Pencatatan kegiatan Komsos yang tertib dan akurat adalah dasar pertanggungjawaban teritorial ke komando atas (Koramil & Kodim).",
            sections = listOf(
                LessonSection(
                    heading = "Unsur 5W + 1H dalam Laporan Komsos",
                    content = "Setiap entri kegiatan harus memenuhi kejelasan informasi:",
                    bulletPoints = listOf(
                        "Who (Siapa): Nama Babinsa dan nama tokoh/warga yang diajak komsos.",
                        "What (Apa): Topik pokok pembahasan atau masalah yang didialogkan.",
                        "When (Kapan): Hari, tanggal, dan jam pelaksanaan.",
                        "Where (Di mana): Desa, dusun, RT/RW atau lokasi spesifik pertemuan.",
                        "Why (Mengapa): Latar belakang atau tujuan pelaksanaan kegiatan.",
                        "How (Bagaimana): Hasil/kesimpulan serta rencana tindak lanjut yang disepakati."
                    )
                ),
                LessonSection(
                    heading = "Standar Output Spreadsheet Excel",
                    content = "Aplikasi ini memfasilitasi ekspor langsung ke format Spreadsheet Excel (.csv dan .xls) agar:",
                    bulletPoints = listOf(
                        "Mudah digabungkan ke formulir laporan mingguan/bulanan Koramil.",
                        "Dapat dibuka langsung di laptop staf Koramil maupun handphone Android.",
                        "Kolom tersusun rapi: No, Tanggal, Wilayah, Kategori Sasaran, Nama Tokoh, Topik, Hasil, Tindak Lanjut, Status."
                    )
                )
            ),
            tips = listOf(
                "Input data Komsos langsung setelah kegiatan selesai agar detail tidak terlupa.",
                "Periksa kembali ejaan nama tokoh dan kejelasan tindak lanjut sebelum ekspor.",
                "Simpan file spreadsheet berkala sebagai arsip digital pribadi Babinsa."
            )
        )
    )

    val quizQuestions = listOf(
        QuizQuestion(
            id = 1,
            question = "Apa kepanjangan dari KOMSOS dalam Pembinaan Teritorial TNI AD?",
            options = listOf(
                "Komando Militer Sosial",
                "Komunikasi Sosial",
                "Koordinasi Masyarakat Sosial",
                "Kemandirian Operasi Sosial"
            ),
            correctIndex = 1,
            explanation = "KOMSOS adalah singkatan dari Komunikasi Sosial, yaitu salah satu metode Binter TNI AD untuk berinteraksi erat dengan seluruh komponen bangsa."
        ),
        QuizQuestion(
            id = 2,
            question = "Manakah yang BUKAN merupakan salah satu dari 5 Kemampuan Teritorial Babinsa?",
            options = listOf(
                "Kemampuan Temu Cepat dan Lapor Cepat",
                "Kemampuan Manajemen Teritorial",
                "Kemampuan Taktik Serangan Udara",
                "Kemampuan Penguasaan Wilayah"
            ),
            correctIndex = 2,
            explanation = "Taktik Serangan Udara bukan bagian dari 5 Kemampuan Teritorial. 5 Kemampuan Teritorial meliputi: Temu Cepat & Lapor Cepat, Manajemen Teritorial, Penguasaan Wilayah, Bintahwil, dan Komsos."
        ),
        QuizQuestion(
            id = 3,
            question = "Tokoh agama (Toga) dan Tokoh masyarakat (Tomas) didekati dengan tujuan utama untuk...",
            options = listOf(
                "Meminta bantuan dana operasional",
                "Menjaga kerukunan, kondusivitas, dan membina ketahanan moral warga",
                "Menggantikan fungsi lurah",
                "Menyelenggarakan latihan militer wajib"
            ),
            correctIndex = 1,
            explanation = "Toga dan Tomas adalah pembentuk opini publik yang sangat didengar masyarakat, sehingga penting untuk menjaga kerukunan dan persatuan bangsa."
        ),
        QuizQuestion(
            id = 4,
            question = "Sikap teritorial Babinsa yang sesuai dengan butir ke-8 dari 8 Wajib TNI adalah...",
            options = listOf(
                "Memelopori usaha-usaha untuk mengatasi kesulitan rakyat sekelilingnya",
                "Menuntut rakyat memberikan jamuan",
                "Bersikap kaku dan formal di hadapan warga",
                "Menghindari warga yang memiliki keluhan"
            ),
            correctIndex = 0,
            explanation = "Butir ke-8 Wajib TNI berbunyi: 'Menjadi contoh dan memelopori usaha-usaha untuk mengatasi kesulitan rakyat sekelilingnya'."
        ),
        QuizQuestion(
            id = 5,
            question = "Dalam penyusunan rekap spreadsheet kegiatan Komsos, kolom apa yang wajib mencantumkan rekomendasi kegiatan berikutnya?",
            options = listOf(
                "Kolom Nama Babinsa",
                "Kolom Rencana Tindak Lanjut",
                "Kolom Nomor Urut",
                "Kolom Jam Pelaksanaan"
            ),
            correctIndex = 1,
            explanation = "Kolom Rencana Tindak Lanjut mencatat langkah nyata apa yang akan dilakukan Babinsa atau pihak terkait sebagai kelanjutan dari hasil pertemuan Komsos."
        )
    )
}
