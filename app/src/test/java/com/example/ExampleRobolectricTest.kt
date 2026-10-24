package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.KomsosDatabase
import com.example.data.model.KomsosEducationData
import com.example.util.SpreadsheetExporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Portal Komsos", appName)
  }

  @Test
  fun verifySpreadsheetCsvExportHasBomAndHeaders() {
    val sample = KomsosDatabase.initialSampleActivities
    val csv = SpreadsheetExporter.generateCsv(sample)
    assertTrue("CSV must start with UTF-8 BOM", csv.startsWith("\uFEFF"))
    assertTrue("CSV must have headers", csv.contains("No;Tanggal;Jam;Desa / Kelurahan"))
    assertTrue("CSV must contain sample activity", csv.contains("Subagio"))
    assertTrue("CSV must contain photo column", csv.contains("Dokumentasi Foto"))
    assertTrue("CSV must indicate photo status", csv.contains("Tersedia (Foto Terlampir)"))
  }

  @Test
  fun verifySpreadsheetXmlExportContainsExcelTags() {
    val sample = KomsosDatabase.initialSampleActivities
    val xml = SpreadsheetExporter.generateXmlSpreadsheet(sample)
    assertTrue(xml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
    assertTrue(xml.contains("urn:schemas-microsoft-com:office:spreadsheet"))
    assertTrue(xml.contains("DOKUMENTASI FOTO"))
    assertTrue(xml.contains("LAPORAN REKAPITULASI KEGIATAN KOMUNIKASI SOSIAL"))
  }

  @Test
  fun verifyEducationCurriculum() {
    val modules = KomsosEducationData.modules
    assertEquals(5, modules.size)
    val questions = KomsosEducationData.quizQuestions
    assertEquals(5, questions.size)
  }
}
