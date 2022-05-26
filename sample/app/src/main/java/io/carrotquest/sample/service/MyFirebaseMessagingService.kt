package io.carrotquest.sample.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.carrotquest_sdk.android.Carrot

class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Для того, чтобы вам приходили пуши от Carrot quest, вам необходимо настроить ваше приложение.
     * Как это сделать мы написали тут - https://developers.carrotquest.io/push_android_sdk/
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //Определяем, является ли сообщение сообщением от сервиса Carrot quest
        if (Carrot.isCarrotPush(remoteMessage)) {
            Carrot.sendFirebasePushNotification(remoteMessage, this)

            //Кроме прочего, можно определить является ли это сообщение автосообщением, если,
            // например, вы не хотите показывать их, а показывать только
            // уведомления для пушей из чата поддержки
            if (Carrot.isAutoMessage(remoteMessage)) {
                //do something
            }
        }
    }
}