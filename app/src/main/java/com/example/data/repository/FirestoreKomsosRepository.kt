package com.example.data.repository

import com.example.data.model.KomsosActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreKomsosRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    val currentUserId: String?
        get() = auth.currentUser?.uid

    private fun getUserCollection(userId: String) =
        firestore.collection("users").document(userId).collection("komsos_activities")

    private fun activityToMap(activity: KomsosActivity, userId: String): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "title" to activity.title,
            "date" to activity.date,
            "time" to activity.time,
            "village" to activity.village,
            "locationDetail" to activity.locationDetail,
            "targetCategory" to activity.targetCategory,
            "contactPerson" to activity.contactPerson,
            "contactRole" to activity.contactRole,
            "topic" to activity.topic,
            "summary" to activity.summary,
            "followUpAction" to activity.followUpAction,
            "status" to activity.status,
            "babinsaName" to activity.babinsaName,
            "koramilName" to activity.koramilName,
            "createdAt" to activity.createdAt,
            "userId" to userId,
            "participantCount" to activity.participantCount
        )
        if (activity.photoUri != null) {
            map["photoUri"] = activity.photoUri
        } else {
            map["photoUri"] = null
        }
        return map
    }

    private fun documentToActivity(docId: String, data: Map<String, Any?>): KomsosActivity {
        return KomsosActivity(
            id = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            firestoreId = docId,
            title = data["title"] as? String ?: "",
            date = data["date"] as? String ?: "",
            time = data["time"] as? String ?: "",
            village = data["village"] as? String ?: "",
            locationDetail = data["locationDetail"] as? String ?: "",
            targetCategory = data["targetCategory"] as? String ?: "",
            contactPerson = data["contactPerson"] as? String ?: "",
            contactRole = data["contactRole"] as? String ?: "",
            topic = data["topic"] as? String ?: "",
            summary = data["summary"] as? String ?: "",
            followUpAction = data["followUpAction"] as? String ?: "",
            status = data["status"] as? String ?: "Selesai",
            photoUri = data["photoUri"] as? String,
            babinsaName = data["babinsaName"] as? String ?: "Serka Bambang Suryono",
            koramilName = data["koramilName"] as? String ?: "Koramil 08/Binaan",
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            participantCount = (data["participantCount"] as? Number)?.toInt() ?: 1
        )
    }

    suspend fun createActivity(activity: KomsosActivity): Result<String> {
        val uid = currentUserId ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            val collection = getUserCollection(uid)
            val docRef = collection.document()
            val data = activityToMap(activity, uid)
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateActivity(activityId: String, activity: KomsosActivity): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            val docRef = getUserCollection(uid).document(activityId)
            val data = activityToMap(activity, uid)
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteActivity(activityId: String): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            getUserCollection(uid).document(activityId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActivityById(activityId: String): Result<KomsosActivity> {
        val uid = currentUserId ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            val snapshot = getUserCollection(uid).document(activityId).get().await()
            if (snapshot.exists() && snapshot.data != null) {
                Result.success(documentToActivity(snapshot.id, snapshot.data!!))
            } else {
                Result.failure(NoSuchElementException("Activity $activityId not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserActivities(): Result<List<KomsosActivity>> {
        val uid = currentUserId ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            val snapshot = getUserCollection(uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { documentToActivity(doc.id, it) }
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeActivities(): Flow<List<KomsosActivity>> = callbackFlow {
        val uid = currentUserId
        if (uid == null) {
            // Unauthenticated: try to query or fail with permission denied on emulator
            val listener = firestore.collection("users").document("unauth_user").collection("komsos_activities")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    trySend(emptyList())
                }
            awaitClose { listener.remove() }
            return@callbackFlow
        }

        val listener = getUserCollection(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val activities = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { documentToActivity(doc.id, it) }
                    }
                    trySend(activities)
                }
            }
        awaitClose { listener.remove() }
    }
}
