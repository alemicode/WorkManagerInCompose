package com.example.workmanagerincompose.periodicWorkManager

import android.content.Context
import androidx.compose.runtime.livedata.observeAsState
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanagerincompose.DownloadWorker
import java.util.concurrent.TimeUnit

class PeriodicWorkManagerExample constructor(
    private val context: Context
) {
    fun runWorkManager() {

        /**
         * In a nutshell, you can specify a second interval that controls when your periodic Worker will be
         * allowed to run inside a portion of the repetition period.
         * This second interval (the flexInterval) it’s positioned at the end of the repetition interval itself.
         * */
        val workRequest = PeriodicWorkRequestBuilder<DownloadWorker>(
            30, TimeUnit.MINUTES,
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            "downloads",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

    }
}