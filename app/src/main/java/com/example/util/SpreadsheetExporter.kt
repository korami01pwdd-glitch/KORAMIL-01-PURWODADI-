package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.KomsosActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SpreadsheetExporter {

    /**
     * Generates a CSV file with UTF-8 Byte Order Mark (BOM) so Microsoft Excel
     * and WPS Office will render Indonesian special characters and quotes without encoding issues.
     */
    fun generateCsv(activities: List<KomsosActivity>): String {
        val sb = StringBuilder()
        // UTF-8 BOM
        sb.append('\uFEFF')
        
        // Headers
        val headers = listOf(
            "No",
            "Tanggal",
            "Jam",
            "Desa / Kelurahan",
            "Dusun / RT-RW / Lokasi",
            "Kategori Sasaran",
            "Nama Tokoh / Warga",
            "Jabatan / Posisi",
            "Topik Bahasan",
            "Hasil / Kesimpulan Komsos",
            "Rencana Tindak Lanjut",
            "Status Tindak Lanjut",
            "Dokumentasi Foto",
            "Babinsa Pelapor",
            "Kesatuan / Koramil"
        )
        sb.append(headers.joinToString(separator = ";") { escapeCsv(it) }).append("\r\n")

        activities.forEachIndexed { index, item ->
            val row = listOf(
                (index + 1).toString(),
                item.date,
                item.time,
                item.village,
                item.locationDetail,
                item.targetCategory,
                item.contactPerson,
                item.contactRole,
                item.topic,
                item.summary,
                item.followUpAction,
                item.status,
                if (item.photoUri != null) "Tersedia (Foto Terlampir)" else "Tidak Ada",
                item.babinsaName,
                item.koramilName
            )
            sb.append(row.joinToString(separator = ";") { escapeCsv(it) }).append("\r\n")
        }

        return sb.toString()
    }

    private fun escapeCsv(value: String): String {
        var str = value.replace("\"", "\"\"")
        if (str.contains(";") || str.contains(",") || str.contains("\n") || str.contains("\r") || str.contains("\"")) {
            str = "\"$str\""
        }
        return str
    }

    /**
     * Generates genuine Excel XML Spreadsheet format (.xls) which Microsoft Excel,
     * Google Sheets, and WPS Office open with rich formatting, styling, and column widths.
     */
    fun generateXmlSpreadsheet(activities: List<KomsosActivity>): String {
        val timeStamp = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID")).format(Date())
        return buildString {
            append("""<?xml version="1.0" encoding="UTF-8"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:html="http://www.w3.org/TR/REC-html40">
 <Styles>
  <Style ss:ID="Default" ss:Name="Normal">
   <Alignment ss:Vertical="Center"/>
   <Borders/>
   <Font ss:FontName="Calibri" x:Family="Swiss" ss:Size="11" ss:Color="#000000"/>
  </Style>
  <Style ss:ID="TitleStyle">
   <Font ss:FontName="Calibri" ss:Size="14" ss:Bold="1" ss:Color="#1B3E17"/>
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
  </Style>
  <Style ss:ID="SubTitleStyle">
   <Font ss:FontName="Calibri" ss:Size="10" ss:Italic="1" ss:Color="#555555"/>
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
  </Style>
  <Style ss:ID="HeaderStyle">
   <Font ss:FontName="Calibri" ss:Size="11" ss:Bold="1" ss:Color="#FFFFFF"/>
   <Interior ss:Color="#234C1E" ss:Pattern="Solid"/>
   <Alignment ss:Horizontal="Center" ss:Vertical="Center" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>
   </Borders>
  </Style>
  <Style ss:ID="DataRowEven">
   <Font ss:FontName="Calibri" ss:Size="10" ss:Color="#000000"/>
   <Interior ss:Color="#F8FAF7" ss:Pattern="Solid"/>
   <Alignment ss:Vertical="Top" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
   </Borders>
  </Style>
  <Style ss:ID="DataRowOdd">
   <Font ss:FontName="Calibri" ss:Size="10" ss:Color="#000000"/>
   <Interior ss:Color="#FFFFFF" ss:Pattern="Solid"/>
   <Alignment ss:Vertical="Top" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
   </Borders>
  </Style>
  <Style ss:ID="StatusDone">
   <Font ss:FontName="Calibri" ss:Size="10" ss:Bold="1" ss:Color="#1E5E20"/>
   <Interior ss:Color="#E8F5E9" ss:Pattern="Solid"/>
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
   </Borders>
  </Style>
  <Style ss:ID="StatusPending">
   <Font ss:FontName="Calibri" ss:Size="10" ss:Bold="1" ss:Color="#E65100"/>
   <Interior ss:Color="#FFF3E0" ss:Pattern="Solid"/>
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#D1D8CB"/>
   </Borders>
  </Style>
 </Styles>
 <Worksheet ss:Name="Rekap Kegiatan Komsos">
  <Table ss:DefaultRowHeight="20">
   <Column ss:Width="30"/> <!-- No -->
   <Column ss:Width="75"/> <!-- Tanggal -->
   <Column ss:Width="50"/> <!-- Jam -->
   <Column ss:Width="110"/> <!-- Desa -->
   <Column ss:Width="130"/> <!-- Dusun/Lokasi -->
   <Column ss:Width="110"/> <!-- Sasaran -->
   <Column ss:Width="120"/> <!-- Nama Tokoh -->
   <Column ss:Width="130"/> <!-- Jabatan -->
   <Column ss:Width="180"/> <!-- Topik -->
   <Column ss:Width="240"/> <!-- Hasil Komsos -->
   <Column ss:Width="200"/> <!-- Tindak Lanjut -->
   <Column ss:Width="90"/> <!-- Status -->
   <Column ss:Width="120"/> <!-- Foto -->
   <Column ss:Width="130"/> <!-- Babinsa -->
   
   <!-- Header Title Rows -->
   <Row ss:Height="26">
    <Cell ss:MergeAcross="13" ss:StyleID="TitleStyle"><Data ss:Type="String">LAPORAN REKAPITULASI KEGIATAN KOMUNIKASI SOSIAL (KOMSOS) BABINSA</Data></Cell>
   </Row>
   <Row ss:Height="18">
    <Cell ss:MergeAcross="13" ss:StyleID="SubTitleStyle"><Data ss:Type="String">Format Pembinaan Teritorial - Dicetak pada: $timeStamp</Data></Cell>
   </Row>
   <Row ss:Height="10"/>

   <!-- Table Headers -->
   <Row ss:Height="25">
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">NO</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">TANGGAL</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">JAM</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">DESA / WILAYAH</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">LOKASI / RT-RW</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">SASARAN KOMSOS</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">NAMA TOKOH / WARGA</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">JABATAN / PERAN</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">TOPIK PEMBAHASAN</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">HASIL &amp; KESIMPULAN</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">RENCANA TINDAK LANJUT</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">STATUS</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">DOKUMENTASI FOTO</Data></Cell>
    <Cell ss:StyleID="HeaderStyle"><Data ss:Type="String">BABINSA PELAPOR</Data></Cell>
   </Row>
""")
            activities.forEachIndexed { index, act ->
                val styleId = if (index % 2 == 0) "DataRowEven" else "DataRowOdd"
                val statusStyle = if (act.status == "Selesai") "StatusDone" else "StatusPending"
                val photoText = if (act.photoUri != null) "Tersedia (Terlampir)" else "Tidak Ada"
                append("""   <Row ss:Height="40">
    <Cell ss:StyleID="$styleId"><Data ss:Type="Number">${index + 1}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.date)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.time)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.village)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.locationDetail)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.targetCategory)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.contactPerson)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.contactRole)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.topic)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.summary)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.followUpAction)}</Data></Cell>
    <Cell ss:StyleID="$statusStyle"><Data ss:Type="String">${escapeXml(act.status)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(photoText)}</Data></Cell>
    <Cell ss:StyleID="$styleId"><Data ss:Type="String">${escapeXml(act.babinsaName)}</Data></Cell>
   </Row>
""")
            }

            append("""  </Table>
 </Worksheet>
</Workbook>""")
        }
    }

    private fun escapeXml(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    /**
     * Saves the spreadsheet data to cache directory and triggers Android Share Intent
     */
    fun shareSpreadsheet(context: Context, activities: List<KomsosActivity>, asXmlExcel: Boolean = true) {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val extension = if (asXmlExcel) "xls" else "csv"
            val mimeType = if (asXmlExcel) "application/vnd.ms-excel" else "text/csv"
            val fileName = "Rekap_Komsos_Babinsa_$timeStamp.$extension"
            val file = File(exportDir, fileName)

            val content = if (asXmlExcel) {
                generateXmlSpreadsheet(activities)
            } else {
                generateCsv(activities)
            }

            FileOutputStream(file).use { fos ->
                fos.write(content.toByteArray(Charsets.UTF_8))
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Laporan Rekap Kegiatan Komsos Babinsa")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Berikut terlampir file spreadsheet Excel rekapitulasi kegiatan Komunikasi Sosial (Komsos) Babinsa sebanyak ${activities.size} kegiatan."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Bagikan Spreadsheet Komsos via:"))
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mengekspor file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Copies tab-separated values to system clipboard for easy paste into Excel or Google Sheets.
     */
    fun copyTableToClipboard(context: Context, activities: List<KomsosActivity>) {
        val sb = StringBuilder()
        // Headers
        sb.append("No\tTanggal\tJam\tDesa\tLokasi\tSasaran\tTokoh\tJabatan\tTopik\tHasil\tTindak Lanjut\tStatus\tFoto\r\n")
        activities.forEachIndexed { i, a ->
            val foto = if (a.photoUri != null) "Ada" else "Tidak"
            sb.append("${i + 1}\t${a.date}\t${a.time}\t${a.village}\t${a.locationDetail}\t${a.targetCategory}\t${a.contactPerson}\t${a.contactRole}\t${a.topic}\t${a.summary}\t${a.followUpAction}\t${a.status}\t$foto\r\n")
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Komsos Spreadsheet Data", sb.toString())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Data tabel spreadsheet disalin ke papan klip!", Toast.LENGTH_SHORT).show()
    }
}
