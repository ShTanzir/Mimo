package com.mimo.app.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object BackupFileUtil {

    /** Writes [json] to a cache file and returns a shareable content:// Uri. */
    fun writeBackupFile(context: Context, json: String): Uri {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "mimo_backup_${System.currentTimeMillis()}.json")
        file.writeText(json)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun readText(context: Context, uri: Uri): String? =
        context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().readText()
        }
}
