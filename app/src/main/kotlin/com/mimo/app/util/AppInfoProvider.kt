package com.mimo.app.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledApp(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val isSystemApp: Boolean
)

object AppInfoProvider {

    /** MIMO's own package + a short list of core system UI packages we never guard. */
    private val EXCLUDED = setOf(
        "com.mimo.app",
        "com.android.systemui",
        "android"
    )

    suspend fun getLaunchableApps(context: Context, includeSystemApps: Boolean = false): List<InstalledApp> =
        withContext(Dispatchers.Default) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolved = pm.queryIntentActivities(intent, 0)
            resolved
                .asSequence()
                .map { it.activityInfo.packageName }
                .distinct()
                .filter { it !in EXCLUDED }
                .mapNotNull { pkg ->
                    try {
                        val appInfo: ApplicationInfo = pm.getApplicationInfo(pkg, 0)
                        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        if (isSystem && !includeSystemApps) return@mapNotNull null
                        InstalledApp(
                            packageName = pkg,
                            label = pm.getApplicationLabel(appInfo).toString(),
                            icon = try { pm.getApplicationIcon(pkg) } catch (e: Exception) { null },
                            isSystemApp = isSystem
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                }
                .sortedBy { it.label.lowercase() }
                .toList()
        }

    fun getAppLabel(context: Context, packageName: String): String =
        try {
            val pm = context.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        } catch (e: Exception) {
            packageName
        }
}
