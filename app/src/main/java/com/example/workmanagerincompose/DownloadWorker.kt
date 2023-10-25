package com.example.workmanagerincompose

import android.content.Context
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadWorker constructor(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        val response = FileApi.instance.downloadImage()
        response.body()?.let { body ->
            withContext(IO) {
                val file = File(context.cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        stream.write(body.bytes())
                    } catch (e: IOException) {
                        return@withContext Result.failure(
                            workDataOf(
                                WorkerKeys.ERROR_MSG to e.localizedMessage
                            )
                        )
                    }
                }
                Result.success(
                    workDataOf(
                        WorkerKeys.IMAGE_URI to file.toUri().toString()
                    )
                )

            }
        }

        if (!response.isSuccessful) {
            if (response.code().toString().startsWith("5")) return Result.retry()
            return Result.failure(
                workDataOf(
                    WorkerKeys.ERROR_MSG to "Network Error"
                )
            )
        }

        return Result.failure(
            workDataOf(
                WorkerKeys.ERROR_MSG to "Unknown Error"
            )
        )

    }
}