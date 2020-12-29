package io.carrotquest.sample.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.presentation.mvp.notifications.NotificationsConstants


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        if (data.containsKey(NotificationsConstants.CQ_SDK_PUSH) && "true" == data[NotificationsConstants.CQ_SDK_PUSH]
        ) {
            Carrot.sendFirebasePushNotification(remoteMessage)
        } else {
            //Yot code
        }
    }
}