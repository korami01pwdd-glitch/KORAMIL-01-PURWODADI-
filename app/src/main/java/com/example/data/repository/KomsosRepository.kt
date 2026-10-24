package com.example.data.repository

import com.example.data.local.KomsosDao
import com.example.data.local.KomsosDatabase
import com.example.data.model.KomsosActivity
import com.example.data.model.KomsosEducationData
import com.example.data.model.LessonModule
import com.example.data.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class KomsosRepository(private val dao: KomsosDao) {

    val allActivities: Flow<List<KomsosActivity>> = dao.getAllActivities()

    fun searchActivities(query: String): Flow<List<KomsosActivity>> {
        return if (query.isBlank()) {
            dao.getAllActivities()
        } else {
            dao.searchActivities(query.trim())
        }
    }

    suspend fun getActivityById(id: Long): KomsosActivity? = withContext(Dispatchers.IO) {
        dao.getActivityById(id)
    }

    suspend fun insertActivity(activity: KomsosActivity): Long = withContext(Dispatchers.IO) {
        dao.insertActivity(activity)
    }

    suspend fun updateActivity(activity: KomsosActivity) = withContext(Dispatchers.IO) {
        dao.updateActivity(activity)
    }

    suspend fun deleteActivity(activity: KomsosActivity) = withContext(Dispatchers.IO) {
        dao.deleteActivity(activity)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        if (dao.getCount() == 0) {
            dao.insertAll(KomsosDatabase.initialSampleActivities)
        }
    }

    fun getModules(): List<LessonModule> = KomsosEducationData.modules

    fun getQuizQuestions(): List<QuizQuestion> = KomsosEducationData.quizQuestions
}
