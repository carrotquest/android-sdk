package io.carrotquest.testsdkapplication.rts_and_push_notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.carrotquest_sdk.android.notifications.NotificationsConstants
import io.carrotquest_sdk.android.notifications.models.IncomingMessage

class MyNewMessageBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.hasExtra(NotificationsConstants.CQ_SDK_NEW_MESSAGE_ARG)) {
            val incomingMessage = intent.getSerializableExtra(NotificationsConstants.CQ_SDK_NEW_MESSAGE_ARG) as IncomingMessage
            //Your code
            //Toast.makeText(context, incomingMessage.text, Toast.LENGTH_SHORT).show()
        }
    }
}