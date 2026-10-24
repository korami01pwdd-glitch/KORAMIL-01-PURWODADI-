package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.KomsosActivity
import kotlinx.coroutines.flow.Flow

@Dao
interface KomsosDao {
    @Query("SELECT * FROM komsos_activities ORDER BY date DESC, time DESC, id DESC")
    fun getAllActivities(): Flow<List<KomsosActivity>>

    @Query("SELECT * FROM komsos_activities WHERE id = :id")
    suspend fun getActivityById(id: Long): KomsosActivity?

    @Query("SELECT * FROM komsos_activities WHERE firestoreId = :firestoreId LIMIT 1")
    suspend fun getByFirestoreId(firestoreId: String): KomsosActivity?

    @Query("""
        SELECT * FROM komsos_activities 
        WHERE title LIKE '%' || :query || '%' 
           OR contactPerson LIKE '%' || :query || '%' 
           OR village LIKE '%' || :query || '%' 
           OR topic LIKE '%' || :query || '%' 
           OR targetCategory LIKE '%' || :query || '%'
        ORDER BY date DESC, time DESC
    """)
    fun searchActivities(query: String): Flow<List<KomsosActivity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: KomsosActivity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<KomsosActivity>)

    @Update
    suspend fun updateActivity(activity: KomsosActivity)

    @Delete
    suspend fun deleteActivity(activity: KomsosActivity)

    @Query("DELETE FROM komsos_activities WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM komsos_activities")
    suspend fun getCount(): Int
}
