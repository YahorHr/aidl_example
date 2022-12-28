package com.imfra

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.util.Log
import kotlinx.coroutines.*
import kotlin.random.Random


class AidlDemonstration : Service() {

    private var personUpdater: IPersonUpdater? = null
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.e("GG", Thread.currentThread().name)

        job = CoroutineScope(Dispatchers.IO).launch {
            val names = listOf("alice", "nick", "bob", "john", "albert")
            while (true) {
                val person = Person(names.random(), Random.nextInt(10, 70))
                Log.e("GG", "$person, pid=${Process.myPid()} thread: ${Thread.currentThread().name}")
                personUpdater?.processPerson(person, Process.myPid())
                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        job?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return object : IAidlDemonstration.Stub() {
            override fun register(personUpdater: IPersonUpdater?) {
                this@AidlDemonstration.personUpdater = personUpdater
            }

            override fun unregister() {
                this@AidlDemonstration.personUpdater = null
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Channel 1",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "This is channel"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification = Notification.Builder(this, channel.id)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText("ggwp")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("gg")
            .build()

        startForeground(1, notification)
        return START_NOT_STICKY
    }

    private companion object {
        const val CHANNEL_ID = "aidl_service_channel"
    }
}