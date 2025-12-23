package com.che2n3jigw.android.app.notification

import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 通知渠道打印工具
 */
object ChannelPrintUtils {
    private const val TAG =  "ChannelPrintUtils"
    
    fun printChannelDetails(channel: NotificationChannelCompat) {
        Log.d(TAG, "========== 通知渠道详情 ==========")
        Log.d(TAG, "渠道ID: ${channel.id}")
        Log.d(TAG, "渠道名称: ${channel.name}")
        Log.d(TAG, "渠道描述: ${channel.description}")
        Log.d(TAG, "重要性级别: ${getImportanceText(channel.importance)}")
        Log.d(TAG, "是否显示角标: ${channel.canShowBadge()}")
        Log.d(TAG, "是否绕过免打扰: ${channel.canBypassDnd()}")
        Log.d(TAG, "是否启用指示灯: ${channel.shouldShowLights()}")
        Log.d(TAG, "是否启用震动: ${channel.shouldVibrate()}")
        Log.d(TAG, "是否发出声音: ${channel.sound != null}")

        // 震动模式
        if (channel.vibrationPattern != null) {
            Log.d(TAG, "震动模式: ${channel.vibrationPattern?.joinToString()}")
        }

        // 指示灯颜色（如果有）
        if (channel.lightColor != 0) {
            Log.d(TAG, "指示灯颜色: #${Integer.toHexString(channel.lightColor)}")
        }

        // 锁屏显示设置
        Log.d(TAG, "锁屏可见性: ${getLockscreenVisibilityText(channel.lockscreenVisibility)}")

        // 声音
        channel.sound?.let { uri ->
            Log.d(TAG, "通知声音: $uri")
        }

        // 渠道组（如果有）
        channel.group?.let { groupId ->
            Log.d(TAG, "所属组ID: $groupId")
        }

        // 对话设置（Android 11+）
        channel.parentChannelId?.let { parentId ->
            Log.d(TAG, "父渠道ID: $parentId")
        }

        channel.conversationId?.let { conversationId ->
            Log.d(TAG, "对话ID: $conversationId")
        }

        Log.d(TAG, "========== 详情结束 ==========")
    }

    // 辅助函数：重要性级别转文本
    private fun getImportanceText(importance: Int): String {
        return when (importance) {
            NotificationManagerCompat.IMPORTANCE_NONE -> "无 (不显示)"
            NotificationManagerCompat.IMPORTANCE_MIN -> "最小 (只在通知栏显示)"
            NotificationManagerCompat.IMPORTANCE_LOW -> "低 (有声音但不可见)"
            NotificationManagerCompat.IMPORTANCE_DEFAULT -> "默认 (有声音和状态栏图标)"
            NotificationManagerCompat.IMPORTANCE_HIGH -> "高 (弹出通知)"
            NotificationManagerCompat.IMPORTANCE_MAX -> "紧急 (最高优先级)"
            else -> "未知 ($importance)"
        }
    }

    // 辅助函数：锁屏可见性转文本
    private fun getLockscreenVisibilityText(visibility: Int): String {
        return when (visibility) {
            NotificationCompat.VISIBILITY_PUBLIC -> "公开 (完全显示)"
            NotificationCompat.VISIBILITY_PRIVATE -> "私密 (只显示基本信息)"
            NotificationCompat.VISIBILITY_SECRET -> "隐藏 (不在锁屏显示)"
            else -> "未设置 ($visibility)"
        }
    }
}