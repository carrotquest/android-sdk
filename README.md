# Оглавление

* [Carrot quest для Android](#carrot-quest-для-android)
* [Установка](#установка)
* [Обновление до версии 2.0.0](#обновление)
* [Инициализация](#инициализация)
* [Авторизация пользователей](#авторизация-пользователей)
* [Свойства пользователей и события](#свойства-пользователей-и-события)
* [Чат с оператором](#чат-с-оператором)
   * [Плавающая кнопка (Floating Button)](#плавающая-кнопка-floating-button)
   * [Открытие чата из произвольного места](#открытие-чата-из-произвольного-места)
* [Уведомления](#уведомления)
   * [Настройка Firebase Cloud Messaging](#настройка-firebase-cloud-messaging)
   * [Настройка Huawei Push Kit](#настройка-huawei-push-kit)
   * [Общие настройки уведомлений](#общие-настройки-уведомлений)
   * [Метод отписки от пушей](#метод-отписки-от-пушей)


## Carrot quest для Android

Carrot quest для Android поддерживает API 19 и выше.

## Установка
На данный момент Carrot quest для Android можно установить с помощью gradle.
Для этого добавьте репозиторий в `build.gradle` файле проекта:
```groovy
allprojects {
    ...
    repositories {
        ...
        maven { url "https://raw.github.com/carrotquest/android-sdk/carrotquest" }
        maven { url "https://jitpack.io" }
    }
}
```
Укажите зависимости в `build.gradle` файле вашего приложения:
```groovy
android {
    ...
    defaultConfig {
        ...
        multiDexEnabled true
    }
    packagingOptions {
        exclude 'META-INF/*.kotlin_module'
    }
}

dependencies {
    ...
    implementation 'com.android.support:multidex:1.0.3'
    implementation 'io.carrotquest:android-sdk:2.0.3-commonRelease'
}
```

Библиотека использует Java 17. Если ваш проект использует версию Java ниже 17, добавьте следующие настройки:
```groovy
android {
    ...
    compileOptions {
        sourceCompatibility '17'
        targetCompatibility '17'
    }
}
```


## Обновление
Обратите внимание, что при переходе на версию 2.0.0 были внесены некоторые важные изменения в способ взаимодействия с библиотекой.

Для унификации кода с iOS SDK, в методе инициализации библиотеки исчез один параметр - appId. Теперь, наилучший способ инициализировать библиотеку выглядит так:
```kotlin
Carrot.setup(this, yourApiKey, object : Carrot.Callback<Boolean> {
    override fun onResponse(result: Boolean) {
        
    }

    override fun onFailure(t: Throwable) {

    }
})
```

Если у вас есть авторизация пользователей, необходимо вызывать ее при старте приложения. Наилучшим местом для этого является onResponse колбэка у метода setup:
```kotlin
Carrot.setup(this, yourApiKey, object : Carrot.Callback<Boolean> {
    override fun onResponse(result: Boolean) {
        if(result) {
            Carrot.auth(userId, userAuthKey, object : Carrot.Callback<String> {
                override fun onResponse(result: String?) {
                    
                }

                override fun onFailure(t: Throwable) {
                    
                }
            })
        }
    }

    override fun onFailure(t: Throwable) {

    }
})
```
Таким образом это предотвратит лишнее возникновение анонимных пользователей.

## Инициализация
Для работы с Carrot quest для Android вам понадобится API Key и User Auth Key. Вы можете найти эти ключи на вкладке Настройки > Разработчикам:
![Api keys](https://github.com/carrotquest/android-sdk/blob/carrotquest/img/carrot_api_keys.png?raw=true)

Для инициализации Carrot quest вам нужно выполнить следующий код в методе onCreate() вашего приложения:


```kotlin
Carrot.setup(this, apiKey, callback)
```

Для вывода дополнительной информации во время отладки используйте метод:
```kotlin
Carrot.setDebug(true)
```

## Авторизация пользователей

Если в вашем приложении присутствует авторизация пользователей, вы можете передать id пользователя в Carrot quest. Существует два способа авторизации: напрямую передать userAuthKey, передать hash генерируемый у вас на бэке. В callback при успешном входе вернется значение свойства carrot_id.

1. Вход через user auth key:


```kotlin
Carrot.auth(userId, userAuthKey, callback)
```

2. Вход через hash:


```kotlin
Carrot.hashedAuth(userId, hash, callback)
```

Чтобы сменить пользователя, нужно сначала вызвать метод деинициализации, а после завново вызвать методы ининциализации и (опционально) авторизации:
```kotlin
Carrot.deInit(object : Carrot.Callback<Boolean> {
    override fun onResponse(result: Boolean) {
        Carrot.setup(this, yourApiKey, callbackSetup)
    }

    override fun onFailure(t: Throwable) {
        
    }
})
```



## Свойства пользователей и события

Вы можете установить необходимые свойства пользователя с помощью
```kotlin
Carrot.setUserProperty(userProperty)
Carrot.setUserProperty(userPropertyList)
```

Для описания свойств пользователя используйте класс `UserProperty`
```java
public UserProperty(String key, String value)
public UserProperty(Operation operation, String key, String value)
```
Более подробно про `Operations` можно прочитать в разделе [«Cвойства пользователя»](https://carrotquest.io/developers/props/#_3).

`Внимание!`

Поле `key` не может начинаться с символа `$`.


Для установки [системных свойств](https://carrotquest.io/developers/props#_4) реализовано 2 класса `CarrotUserProperty` и `EcommerceUserProperty`.

Для отслеживания событий используйте
```kotlin
Carrot.trackEvent(eventName)
```
Вы можете указать дополнительные параметры для события в виде JSON-строки и передать их в метод
```kotlin
Carrot.trackEvent(eventName, eventParams)
```
В SDK есть возможность трекинга навигации внутри приложения для того, чтобы при необходимости запускать различные триггерные сообщения на определенных экранах. Для этого используйте метод
```kotlin
Carrot.trackScreen(screenName)
```
Вы можете получить список идентификаторов непрочитанных на данный момент диалогов
```kotlin
 Carrot.getUnreadConversations()
```
Также можно подписаться на изменения в списке идентификаторов непрочитанных диалогов
```kotlin
 Carrot.setUnreadConversationsCallback(callback)
```

## Чат с оператором
Вы можете дать пользователю мобильного приложения возможность перейти в чат с оператором из любого места. Это можно реализовать двумя разными путями - через плавающую кнопку, либо напрямую вызвав метод открытия чата в любое нужное время.

### Плавающая кнопка (Floating Button)
По своей сути - это элемент интерфейса, наследующийся от `ConstraintLayout`. Вы можете встроить его в свою разметку:
``` xml
<io.carrotquest_sdk.android.ui.fab.FloatingButton
        android:id="@+id/cq_sdk_float_button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:cq_location_fab="BOTTOM_RIGHT"
        app:cq_visibility_background="false"
        app:cq_icon_fab="@drawable/ic_send"
        app:cq_margin_fab="8dp"
        app:cq_show_social_labels="false"
        app:cq_auto_hide_fab="true"
/>
```
У этого элемента есть свои атрибуты:
* `app:cq_location_fab` отвечает за расположение плавающей кнопки относительно её родительского контейнера. Возможны 4 варианта - `TOP_LEFT`, `TOP_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_RIGHT`. По умолчанию `BOTTOM_RIGHT`.
* `app:cq_visibility_background` отвечает за видимость эффекта затемнения при нажатии на плавающую кнопку. По умолчанию `true`.
* `app:cq_icon_fab` задаёт иконку плавающей кнопки. По умолчанию `@id/ic_cq_message`.
* `app:cq_margin_fab` задаёт отступы плавающей кнопки относительно своего родительского контейнера. По умолчанию `16dp`.
* `app:cq_show_social_labels` отвечает за видимость надписей рядом с иконками социальных сетей. По умолчанию `true`
* `app:cq_auto_hide_fab` отвечает за автоматическое скрытие кнопки чата когда интернет не доступен. По умолчанию `false`

#### Интерфейс плавающей кнопки
Доступные методы для настройки и управления поведением плавающей кнопки.

``` java
/**
 * Показать плавающую кнопку
 */
public void showFab()
```

``` java
/**
 * Скрыть плавающую кнопку
 */
 public void hideFab()
```

``` java
/**
 * Показать кнопки интеграции
 */
public void expandMenu()
```

``` java
/**
 * Скрыть кнопки интеграции
 */
public void collapseMenu()
```

``` java
/**
 * Установить иконку чата
 * @param iconFAB Иконка
 */
public void setIconFAB(Drawable iconFAB)
```

``` java
/**
 * Установить отступы кнопки от краев экрана
 * @param margin Значение отступа
 */
public void setMarginFAB(int margin)
```

``` java
/**
 * Установить расположение кнопки
 * @param location Расположение кнопки
 */
public void setLocationFAB(LocationFAB location)
```


### Открытие чата из произвольного места
Открыть чат можно также, вызвав из произвольного места (после инициализации) следующий код:
```kotlin
Carrot.openChat(context)
```

# Уведомления

SDK поддерживает два провайдера push-уведомлений - Firebase Cloud Messaging и HUAWEI
Push Kit.

## Настройка Firebase Cloud Messaging

В первую очередь необходимо получить ключ и отправить его в Carrot. Вы можете найти
поле для ввода ключа на вкладке Настройки > Разработчикам > Push-уведомления для SDK.
Процесс настройки сервиса Firebase Cloud Messaging описан здесь

Если вы уже используете сервис Firebase Cloud Messaging для своих push-уведомлений, то
для корректной работы push-уведомлений в SDK необходимо отредактировать вашу службу
FirebaseMessagingService. Это необходимо для "прокидывания" токена и наших сообщений
внутрь SDK. Пример:

```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService () {
    override fun onMessageReceived (message: RemoteMessage) {
        val pushData: Map<String, String> = message. data

        if (Carrot.isCarrotPush(pushData)) {
            Carrot.sendPushNotification(pushData, this)
        } else {
            //Your code
        }
    }

    override fun onNewToken (token: String) {
        Carrot.sendPushToken(token)
        super .onNewToken(token)
    }
}
```
## Настройка Huawei Push Kit

Чтобы уведомления доходили до пользователям с устройствами без Google-сервисов, можно
использовать службу доставки push-уведомлений от Huawei. Для начала вам нужно
интегрировать HPK в свое приложение. Как это слделать можно прочитать здесь. После
этого на вкладке Настройки > Разработчикам > Push-уведомления для SDK нужно указать
Client ID, Client Secret и Webhook Secret. Далее, внесите изменения в службу,
унаследованную от HmsMessageService. Пример:

```kotlin
class MyHuaweiPushKitService : HmsMessageService () {
    override fun onMessageReceived (remoteMessage: RemoteMessage?) {
        val pushData: Map<String, String> = remoteMessage?.dataOfMap ?: HashMap()
        if (Carrot.isCarrotPush(pushData)) {
            Carrot.sendPushNotification(pushData, this )
        } else {
            //Your code
        }
    }

    override fun onNewToken (token: String?) {
        Carrot.sendPushToken(token);
        super .onNewToken(token)
    }

    override fun onNewToken (token: String?, p1: Bundle?) {
        Carrot.sendPushToken(token);
        super .onNewToken(token, p1)
    }
}
```
## Общие настройки уведомлений

Иконку и цвет уведомлений о новых сообщениях можно изменить. Для установки иконки на
уведомления вызовете следующий метод после инициализации SDK:

```
Carrot.setNotificationIcon(R.drawable.ic_notificatrion_icon);
```
Либо добавьте иконку с названием ic_cq_notification.xml в директорию res/drawable
Для установки цвета уведомлений в файл ресурсов пропишите цвет с названием
colorCqNotify и нужным вам значением:

```
<color name="colorCqNotify">#EF7F28</color>
```

Если вы хотите из любого места вашего приложения получать информацию о новых сообщениях в SDK, то вы можете реализовать BroadcastReceiver. Пример реализации:
```java
public class MyNewMessageBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra(NotificationsConstants.CQ_SDK_NEW_MESSAGE_ARG)) {
            IncomingMessage incomingMessage = (IncomingMessage) intent.getSerializableExtra(NotificationsConstants.CQ_SDK_NEW_MESSAGE_ARG);
            if (incomingMessage != null) {
                Toast.makeText(context, incomingMessage.getText(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
```
`IncomingMessage` - класс, который описывает входящее сообщение.

Далее нужно зарегистрировать его:
``` java
MyNewMessageBroadcastReceiver messageReceiver = new MyNewMessageBroadcastReceiver();
IntentFilter filter = new IntentFilter();
filter.addAction(NotificationsConstants.CQ_SDK_NEW_MESSAGE_ACTION);
registerReceiver(messageReceiver, filter);
```

Важно! Если приложение закрыто и пользователь откроет чат по нажатию на пуш, то ваша стартовая активность не запустится. Приложение закроется вместе с закрытием чата. Чтобы исправить это, вы можете передать полное имя активности, которая должна запуститься при закрытии чата:
```java
Carrot.setParentActivityClassName("io.test.MainActivity");
```

## Метод отписки от пушей

Существуют методы отписать конкретного пользователя от пушей и от всех рассылок в принципе. 

Метод для отписки от пушей:

```kotlin
Carrot.pushNotificationsUnsubscribe()
```

Метод для отписки от всех рассылок:

```kotlin
Carrot.pushCampaignsUnsubscribe()
```