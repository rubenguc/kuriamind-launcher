package com.rubenguc.kuriamindlauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import com.rubenguc.kuriamindlauncher.domain.model.InstalledApp
import com.rubenguc.kuriamindlauncher.domain.repository.AppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppsRepositoryImpl(
    private val context: Context
) : AppsRepository {

    override suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos: List<ResolveInfo> = context.packageManager
            .queryIntentActivities(intent, 0)
            .orEmpty()

        resolveInfos
            .map { resolveInfo ->
                val label = resolveInfo.loadLabel(context.packageManager).toString()
                val packageName = resolveInfo.activityInfo?.packageName
                    ?: resolveInfo.resolvePackageName
                    ?: return@map null
                InstalledApp(packageName = packageName, label = label)
            }
            .filterNotNull()
            .sortedBy { it.label.lowercase() }
    }

    override fun launchApp(packageName: String): Boolean {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        return true
    }
}
