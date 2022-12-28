package com.imfra.aidlreceiver

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.util.Log
import com.imfra.IAidlDemonstration
import com.imfra.IPersonUpdater
import com.imfra.aidlreceiver.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent().apply {
            component = ComponentName.unflattenFromString("com.imfra/com.imfra.AidlDemonstration")
            action = "com.imfra.AidlDemonstration.BIND"

        }
        Log.d("WP", "binding ${intent.component?.className} -- ${intent.component?.packageName}")

        bindService(intent, mConnection, Service.BIND_AUTO_CREATE).also {
            Log.d("WP", "binding to remote service is $it")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
    }

    var iAidlDemonstration: IAidlDemonstration? = null

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            iAidlDemonstration = IAidlDemonstration.Stub.asInterface(service)
            iAidlDemonstration?.register(personUpdater)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            iAidlDemonstration = null
        }
    }

    private val personUpdater = object : IPersonUpdater.Stub() {
        @SuppressLint("SetTextI18n")
        override fun processPerson(person: Person, pid: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                viewBinding.output.text = "myPid: ${Process.myPid()}, pid: $pid, p: ${person.name} - ${person.age}"
            }
        }
    }
}