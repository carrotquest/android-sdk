package io.carrotquest.testsdkapplication.rts_and_push_notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.notifications.NotificationsConstants

/**
 * Если нужны пуши через Firebase
 */
class TestFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val data = remoteMessage!!.data
        if (data.containsKey(NotificationsConstants.CQ_SDK_PUSH) && "true" == data[NotificationsConstants.CQ_SDK_PUSH]) {
            Carrot.sendFirebasePushNotification(remoteMessage)
        } else {
            //Your code
        }
    }
}