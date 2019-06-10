package io.carrotquest.testsdkapplication.app

import android.app.Application
import android.content.IntentFilter
import io.carrotquest.testsdkapplication.rts_and_push_notifications.MyNewMessageBroadcastReceiver
import io.carrotquest_sdk.android.notifications.NotificationsConstants

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val messageReceiver = MyNewMessageBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(NotificationsConstants.CQ_SDK_NEW_MESSAGE_ACTION)
        registerReceiver(messageReceiver, filter)
    }
}