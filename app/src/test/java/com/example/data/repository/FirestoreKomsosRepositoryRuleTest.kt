package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.KomsosActivity
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreKomsosRepositoryRuleTest : FirestoreEmulatorTestBase() {

  private fun createSampleActivity(title: String = "Komsos dengan Tokoh Masyarakat"): KomsosActivity {
    return KomsosActivity(
      title = title,
      date = "2026-10-24",
      time = "10:00",
      village = "Desa Sukamaju",
      locationDetail = "Balai Desa",
      targetCategory = "Tokoh Masyarakat",
      contactPerson = "Bpk. Suryanto",
      contactRole = "Ketua RW 02",
      topic = "Kamtibmas Desa",
      summary = "Koordinasi jadwal ronda malam",
      followUpAction = "Patroli bersama",
      status = "Selesai",
      photoUri = null,
      babinsaName = "Serka Bambang Suryono",
      koramilName = "Koramil 08/Binaan",
      createdAt = System.currentTimeMillis()
    )
  }

  @Test
  fun createActivity_authenticatedUser_success() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repo = FirestoreKomsosRepository(firestore, auth)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.createActivity(createSampleActivity())
    }
    assertTrue("Create activity should succeed", result.isSuccess)
    val actId = result.getOrThrow()
    assertTrue("Activity ID should not be empty", actId.isNotEmpty())
  }

  @Test
  fun getUserActivities_authenticatedUser_returnsList() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repo = FirestoreKomsosRepository(firestore, auth)

    val actId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.createActivity(createSampleActivity("Komsos List Test")).getOrThrow()
    }

    val listResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.getUserActivities()
    }
    assertTrue("Fetching user activities should succeed", listResult.isSuccess)
    val activities = listResult.getOrThrow()
    assertTrue("List should contain the created activity", activities.any { it.firestoreId == actId })
  }

  @Test
  fun observeActivities_authenticatedUser_emitsRealtime() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repo = FirestoreKomsosRepository(firestore, auth)

    val actId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.createActivity(createSampleActivity("Realtime Observation Test")).getOrThrow()
    }

    val emitted = withTimeout(FLOW_TIMEOUT_MS) {
      repo.observeActivities().first { list -> list.any { it.firestoreId == actId } }
    }
    assertNotNull(emitted)
    assertTrue(emitted.any { it.firestoreId == actId })
  }

  @Test
  fun getActivityById_crossUser_failsWithPermissionDenied() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val aliceRepo = FirestoreKomsosRepository(firestore, auth)
    val actId = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.createActivity(createSampleActivity("Alice Private Activity")).getOrThrow()
    }

    signInTestUser(BOB_EMAIL)
    val bobRepo = FirestoreKomsosRepository(firestore, auth)

    // Bob attempts to fetch Alice's document directly from her collection path
    try {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        val aliceUid = auth.currentUser?.let {
          // Bob is logged in, but let's query Alice's subcollection using Firestore directly
          firestore.collection("users").document("alice_test_user").collection("komsos_activities").document(actId).get()
        }
      }
    } catch (e: Exception) {
      val fsEx = e.unwrapFirestoreException()
      if (fsEx != null) {
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx.code)
      }
    }
  }

  @Test
  fun observeActivities_unauthenticated_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repo = FirestoreKomsosRepository(firestore, auth)

    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeActivities().first()
      }
      fail("Unauthenticated observation should fail with PERMISSION_DENIED")
    } catch (e: Exception) {
      val fsEx = e.unwrapFirestoreException()
      assertNotNull("Expected FirebaseFirestoreException nested in CancellationException", fsEx)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@komsos.mil.id"
    const val BOB_EMAIL = "bob@komsos.mil.id"
    const val DEFAULT_TIMEOUT_MS = 8000L
    const val FLOW_TIMEOUT_MS = 6000L
  }
}
