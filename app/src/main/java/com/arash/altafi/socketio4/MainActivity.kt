package com.arash.altafi.socketio4

import android.app.Notification
import android.app.NotificationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var btnSendNotification: MaterialButton
    private lateinit var tvTitle: MaterialTextView
    private lateinit var tvDescription: MaterialTextView
    private lateinit var mSocket: Socket
    private val channelId = "channel_test"
    private var title: String = ""
    private var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createChannel()
        bindViews()
        init()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createChannel(
                this,
                channelId,
                "socket name",
                "socket description",
                NotificationManager.IMPORTANCE_HIGH,
                Notification.VISIBILITY_PUBLIC,
                soundUri = Uri.parse("android.resource://$packageName/raw/notif")
            )
        }
    }

    private fun bindViews() {
        btnSendNotification = findViewById(R.id.btnSendNotification)
        tvTitle = findViewById(R.id.txtTitle)
        tvDescription = findViewById(R.id.txtDescription)
    }

    private fun init() {
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()

        btnSendNotification.setOnClickListener {
            mSocket.emit("notification")
        }

        mSocket.on("title") { args ->
            title = args[0].toString()
            runOnUiThread {
                tvTitle.text = title
            }
        }
        mSocket.on("description") { args ->
            description = args[0].toString()
            runOnUiThread {
                tvDescription.text = description

                NotificationUtils.send(
                    context = this,
                    channelId = channelId,
                    title = title,
                    body = description
                )
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.closeConnection()
    }

}