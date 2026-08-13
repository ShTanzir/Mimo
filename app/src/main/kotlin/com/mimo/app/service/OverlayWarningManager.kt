package com.mimo.app.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.mimo.app.util.TimeUtils

/**
 * A lightweight, full-screen semi-transparent warning shown right before
 * MIMO closes a guarded app (the last few seconds of the countdown). Built
 * with plain Android views (not Compose) since it's attached directly to
 * the WindowManager from a Service context, outside any Activity.
 */
object OverlayWarningManager {

    private var overlayView: android.view.View? = null

    fun show(
        context: Context,
        appLabel: String,
        closeMessage: String,
        remainingMillis: Long,
        allowSnooze: Boolean,
        onSnoozeClick: () -> Unit
    ) {
        if (!android.provider.Settings.canDrawOverlays(context)) return
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (overlayView == null) {
            val container = FrameLayout(context).apply {
                setBackgroundColor(0xAA13241A.toInt())
            }
            val column = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(64, 64, 64, 64)
            }
            val title = TextView(context).apply {
                text = "MIMO"
                textSize = 22f
                setTextColor(0xFFF4F1E8.toInt())
                gravity = Gravity.CENTER
                alpha = 0.85f
            }
            val timerText = TextView(context).apply {
                id = TIMER_ID
                textSize = 56f
                setTextColor(0xFFF4F1E8.toInt())
                gravity = Gravity.CENTER
                setPadding(0, 24, 0, 24)
            }
            val messageText = TextView(context).apply {
                id = MESSAGE_ID
                text = "$appLabel is about to close — $closeMessage"
                textSize = 16f
                setTextColor(0xFFE9F1EA.toInt())
                gravity = Gravity.CENTER
            }
            column.addView(title)
            column.addView(timerText)
            column.addView(messageText)

            if (allowSnooze) {
                val snoozeBtn = Button(context).apply {
                    text = "Give me one more minute"
                    setOnClick { onSnoozeClick() }
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.topMargin = 32
                column.addView(snoozeBtn, params)
            }

            container.addView(
                column,
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
            )
            overlayView = container

            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
            try {
                windowManager.addView(container, params)
            } catch (e: Exception) {
                overlayView = null
                return
            }
        }

        overlayView?.findViewById<TextView>(TIMER_ID)?.text = TimeUtils.formatShortTimer(remainingMillis)
    }

    fun hide(context: Context) {
        val view = overlayView ?: return
        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(view)
        } catch (e: Exception) { /* already removed */ }
        overlayView = null
    }

    private const val TIMER_ID = 881001
    private const val MESSAGE_ID = 881002
}

private fun Button.setOnClick(action: () -> Unit) {
    setOnClickListener { action() }
}
