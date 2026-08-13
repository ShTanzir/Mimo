package com.mimo.app.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticUtils {

    private fun vibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * A short pulse whose strength grows the closer [remainingMillis] gets to
     * zero — used during the last few seconds before MIMO closes an app, so
     * the warning feels more urgent as the deadline approaches.
     */
    fun progressivePulse(context: Context, remainingMillis: Long, warningWindowMillis: Long) {
        val v = vibrator(context)
        if (!v.hasVibrator()) return

        val progress = (1f - (remainingMillis.toFloat() / warningWindowMillis.toFloat())).coerceIn(0f, 1f)
        val durationMs = (40 + progress * 110).toLong() // 40ms -> 150ms
        val amplitude = (80 + progress * 175).toInt().coerceIn(1, 255) // 80 -> 255

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(durationMs)
        }
    }

    fun confirmTap(context: Context) {
        val v = vibrator(context)
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(25, 120))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(25)
        }
    }
}
