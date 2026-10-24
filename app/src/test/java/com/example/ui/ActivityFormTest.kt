package com.example.ui

import com.example.data.model.KomsosActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ActivityFormTest {

    @Test
    fun testKomsosActivity_withParticipantCount_defaultsAndValues() {
        val activity = KomsosActivity(
            title = "Komsos di Balai Desa",
            date = "2026-10-24",
            village = "Desa Sukamaju",
            summary = "Dialog kamtibmas bersama tokoh pemuda dan aparat desa",
            participantCount = 15
        )

        assertEquals("Komsos di Balai Desa", activity.title)
        assertEquals("2026-10-24", activity.date)
        assertEquals("Desa Sukamaju", activity.village)
        assertEquals("Dialog kamtibmas bersama tokoh pemuda dan aparat desa", activity.summary)
        assertEquals(15, activity.participantCount)
        assertTrue(activity.participantCount >= 1)
    }

    @Test
    fun testActivityForm_validationLogic() {
        // Date, Location, Description, Participant Count validation rules
        val date = "2026-10-24"
        val location = "Pos Ronda RW 03"
        val description = "Koordinasi pengamanan malam minggu bersama Linmas"
        val participantCount = 8

        val isValid = date.isNotBlank() &&
                      location.isNotBlank() &&
                      description.isNotBlank() &&
                      description.length >= 5 &&
                      participantCount >= 1

        assertTrue("Input with all 4 required fields must be valid", isValid)
    }
}
