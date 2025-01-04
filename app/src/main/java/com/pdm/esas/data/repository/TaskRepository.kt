package com.pdm.esas.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.models.Task
import com.pdm.esas.data.models.UserStatus
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
) {
    companion object {
        private const val COLLECTION_NAME = "tasks"
    }

    private val tasksCollection = firebaseStore.collection(COLLECTION_NAME)

    suspend fun createTask(task: Task): Result<String> {
        return try {
            val data = hashMapOf<String, Any?>()
            data["created_by"] = task.created_by
            data["title"] = task.title
            data["description"] = task.description
            data["task_limit"] = task.task_limit
            data["created_at"] = task.created_at
            data["updated_at"] = task.updated_at
            data["task_date"] = task.task_date
            data["users"] = task.users
            val docRef = tasksCollection.document()
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTask(documentId: String): Result<Task> {
        return try {
            val snapshot = tasksCollection.document(documentId).get().await()
            if (!snapshot.exists()) {
                Result.failure(Exception("Documento não encontrado: $documentId"))
            } else {
                val task = snapshot.toObject(Task::class.java)
                if (task == null) {
                    Result.failure(Exception("Falha ao converter Task"))
                } else {
                    Result.success(task.copy(id = snapshot.id))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllTasks(): Result<List<Task>> {
        return try {
            val snapshot = tasksCollection.get().await()
            val list = snapshot.documents.mapNotNull {
                it.toObject(Task::class.java)?.copy(id = it.id)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllTasksByDate(date: LocalDate): Result<List<Task>> {
        return try {
            val start = date.toEpochDay() * 86400000
            val end = start + 86399999
            val snapshot = tasksCollection
                .whereGreaterThanOrEqualTo("task_date", Timestamp(Date(start)))
                .whereLessThanOrEqualTo("task_date", Timestamp(Date(end)))
                .get()
                .await()
            val list = snapshot.documents.mapNotNull {
                it.toObject(Task::class.java)?.copy(id = it.id)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> {
        val docId = task.id ?: return Result.failure(Exception("ID do documento está vazio"))
        val data = hashMapOf<String, Any?>()
        data["created_by"] = task.created_by
        data["title"] = task.title
        data["description"] = task.description
        data["task_limit"] = task.task_limit
        data["created_at"] = task.created_at
        data["updated_at"] = task.updated_at
        data["task_date"] = task.task_date
        data["users"] = task.users
        return try {
            tasksCollection.document(docId).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(documentId: String): Result<Unit> {
        return try {
            tasksCollection.document(documentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun subscribeTask(documentId: String, userId: String): Result<Unit> {
        return try {
            val snap = tasksCollection.document(documentId).get().await()
            if (!snap.exists()) {
                Result.failure(Exception("Documento não encontrado: $documentId"))
            } else {
                val task = snap.toObject(Task::class.java)
                if (task == null) {
                    Result.failure(Exception("Falha ao converter Task"))
                } else {
                    val currentUsers = task.users ?: emptyMap()
                    val taskLimit = task.task_limit ?: 0
                    if (currentUsers.size >= taskLimit) {
                        Result.failure(Exception("Tarefa atingiu o limite de usuários ($taskLimit)"))
                    } else {
                        tasksCollection.document(documentId).update("users.$userId", UserStatus(false)).await()
                        Result.success(Unit)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unsubscribeTask(documentId: String, userId: String): Result<Unit> {
        return try {
            tasksCollection.document(documentId).update("users.$userId", FieldValue.delete()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
