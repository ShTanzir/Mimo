package com.mimo.app.util

import com.mimo.app.data.AppRule
import org.json.JSONArray
import org.json.JSONObject

/** Exports/imports guarded-app rules as a small, human-readable JSON backup. */
object RulesBackup {

    fun toJson(rules: List<AppRule>): String {
        val array = JSONArray()
        rules.forEach { rule ->
            val obj = JSONObject()
            obj.put("packageName", rule.packageName)
            obj.put("appLabel", rule.appLabel)
            obj.put("enabled", rule.enabled)
            obj.put("delayMillis", rule.delayMillis)
            obj.put("delayPresetLabel", rule.delayPresetLabel)
            obj.put("closeMessage", rule.closeMessage)
            obj.put("allowSnoozeOnce", rule.allowSnoozeOnce)
            obj.put("vibrateOnWarning", rule.vibrateOnWarning)
            array.put(obj)
        }
        val root = JSONObject()
        root.put("mimoBackupVersion", 1)
        root.put("rules", array)
        return root.toString(2)
    }

    /** Returns the parsed rules, or throws if the JSON isn't a MIMO backup. */
    fun fromJson(json: String): List<AppRule> {
        val root = JSONObject(json)
        val array = root.getJSONArray("rules")
        val result = mutableListOf<AppRule>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                AppRule(
                    packageName = obj.getString("packageName"),
                    appLabel = obj.optString("appLabel", obj.getString("packageName")),
                    enabled = obj.optBoolean("enabled", true),
                    delayMillis = obj.optLong("delayMillis", 60_000L),
                    delayPresetLabel = obj.optString("delayPresetLabel", "Custom"),
                    closeMessage = obj.optString("closeMessage", "Time's up! MIMO is closing this app."),
                    allowSnoozeOnce = obj.optBoolean("allowSnoozeOnce", true),
                    vibrateOnWarning = obj.optBoolean("vibrateOnWarning", true)
                )
            )
        }
        return result
    }
}
