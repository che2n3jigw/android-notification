package com.che2n3jigw.android.app.notification

import android.Manifest
import android.app.NotificationManager
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
        checkPostNotificationPermission()

        notificationManager = NotificationManagerCompat.from(this)

        binding.btnCreateNotificationChannel.setOnClickListener {
            val name = "通知渠道"
            val description = "用于测试的通知渠道"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            createNotificationChannel(name, description, importance, CHANNEL_ID, GROUP_ID)
        }
        binding.btnReadNotificationChannel.setOnClickListener {
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
    }

    /**
     * 检查通知权限
     */
    private fun checkPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, permission)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                notificationPermissionLauncher.launch(permission)
            }
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
}