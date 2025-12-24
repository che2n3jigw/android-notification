package com.che2n3jigw.android.app.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.che2n3jigw.android.app.notification.databinding.ActivityNotificationBinding

/**
 * 通知栏演示页面
 */
class NotificationActivity : AppCompatActivity() {
    companion object {
        private const val CHANNEL_ID = "test_channel"
        private const val GROUP_ID = "my_group_01"
        private const val NOTIFICATION_ID = 100
    }

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationManager: NotificationManagerCompat

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                val tips = "通知权限申请失败"
                Toast.makeText(this@NotificationActivity, tips, Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 检查通知权限
        checkPostNotificationPermission()
        // 创建
        notificationManager = NotificationManagerCompat.from(this)

        initListener()
    }

    private fun initListener() {
        // <editor-fold defaultState="collapsed" desc="渠道相关">
        binding.btnCreateChannel.setOnClickListener {
            val name = "通知渠道"
            val description = "用于测试的通知渠道"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            createNotificationChannel(name, description, importance, CHANNEL_ID, GROUP_ID)
        }
        binding.btnReadChannel.setOnClickListener {
            readNotificationChannel(CHANNEL_ID)
        }
        binding.btnOpenChannelSettings.setOnClickListener {
            openChannelSettings(packageName, CHANNEL_ID)
        }
        binding.btnDeleteChannel.setOnClickListener {
            deleteChannel(CHANNEL_ID)
        }
        binding.btnCreateChannelGroup.setOnClickListener {
            val groupName = "我自定义的组名"
            createChannelGroup(GROUP_ID, groupName)
        }

        binding.btnDeleteChannelGroup.setOnClickListener {
            deleteChannelGroup(GROUP_ID)
        }
        // </editor-fold>

        // <editor-fold defaultState="collapsed" desc="通知相关">
        binding.btnCreateNotification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            createNotification("标题", "内容", pendingIntent)
        }
        binding.btnUpdateNotification.setOnClickListener {
            updateNotification("标题一", "内容一")
        }
        binding.btnDeleteNotification.setOnClickListener {
            deleteNotification()
        }
        // </editor-fold>
    }

    /**
     * 检查通知权限
     */
    private fun checkPostNotificationPermission() {
        if (!checkPermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            // 请求权限
            notificationPermissionLauncher.launch(permission)
        }
    }

    /**
     * 是否有通知权限
     */
    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, permission)
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    /**
     * 创建渠道
     */
    private fun createNotificationChannel(
        name: String,
        description: String,
        importance: Int,
        channelId: String,
        groupId: String
    ) {
        val channel = NotificationChannelCompat.Builder(channelId, importance).apply {
            setName(name)
            setDescription(description)
            notificationManager.getNotificationChannelGroupCompat(groupId)?.let {
                setGroup(it.id)
            }
        }.build()
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * 读取通知渠道
     */
    private fun readNotificationChannel(channelId: String) {
        val channel = notificationManager.getNotificationChannelCompat(channelId) ?: return
        ChannelPrintUtils.printChannelDetails(channel)
    }

    /**
     * 打开渠道设置
     */
    private fun openChannelSettings(packageName: String, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            }
            startActivity(intent)
        }
    }

    /**
     * 删除渠道
     */
    private fun deleteChannel(channelId: String) {
        notificationManager.deleteNotificationChannel(channelId)
    }


    private fun createChannelGroup(groupId: String, groupName: String) {
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroupCompat.Builder(groupId).setName(groupName).build()
        )
    }

    private fun deleteChannelGroup(groupId: String) {
        notificationManager.deleteNotificationChannelGroup(groupId)
    }

    /**
     * 创建通知
     */
    private fun createNotification(title: String, content: String, intent: PendingIntent) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            // 图标
            .setSmallIcon(R.mipmap.ic_launcher)
            // 标题
            .setContentTitle(title)
            // 内容
            .setContentText(content)
            // 重要性
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            // 点击后自动移除
            .setAutoCancel(true)
        if (checkPermission()) {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun updateNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            // 图标
            .setSmallIcon(R.mipmap.ic_launcher)
            // 标题
            .setContentTitle(title)
            // 内容
            .setContentText(content)
            // 重要性
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (checkPermission()) {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun deleteNotification() {
        if (checkPermission()) {
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }
}