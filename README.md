# Оглавление

* [Carrot quest для Android](#carrot-quest-для-android)
* [Установка](#установка)
* [Обновление до версии 3.0.0](#обновление)
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

Carrot quest для Android поддерживает API 21 и выше.

Подробная документация по SDK доступна на [developers-sdk.carrotquest.io](https://developers-sdk.carrotquest.io).

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
    packagingOptions {
        exclude 'META-INF/*.kotlin_module'
    }
}

dependencies {
    ...
    implementation 'io.carrotquest:android-sdk:3.0.0-commonRelease'
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
Версия **3.0.0** — мажорное обновление с ломающими изменениями в публичном API. Что нужно учесть при обновлении:

* **Новая система логирования.** `setDebug(boolean)` / `isDebug()` удалены — уровень задаётся через `setLogLevel(SdkLogLevel)` со значениями `NONE`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `VERBOSE`. По умолчанию `NONE`: SDK не пишет в логи ничего, в том числе в release (раньше часть логов сыпалась всегда). Дополнительно можно получать записи логов прямо в своём коде через `setLogSink(...)` (например, прокинуть в свой логгер или крэш-репортер) и снимать одномоментный срез состояния для баг-репортов через `getDiagnostics()`. Чувствительные данные (токены, e-mail, идентификаторы) маскируются, пока не включён `setLogIncludeSensitive(true)`. Примеры использования — в разделе [«Инициализация»](#инициализация).
* **Подписки на состояние удалены** (`addStateObserver` / `getStateFlow` / `addAuthStateObserver` / `getAuthStateFlow` / `addSessionExpiredObserver` / `getInitObservable`, а также модели `CarrotState` / `CarrotAuthState`). Восстановление сессии теперь автоматическое: результат операций берите из их `Callback`, а факт инициализации — из `Carrot.isInit()`.
* **Отписка через `Cancellable`.** Методы подписки (например, `setUnreadConversationsCallback`) возвращают `Cancellable` — для отписки вызывайте `.cancel()`.
* **`UserProperty` / `Operation` консолидированы.** `Operation` — единый enum `io.carrotquest_sdk.android.models.Operation` со значениями в `UPPER_CASE` (`set_once` → `SET_ONCE`, `update_or_create` → `UPDATE_OR_CREATE` и т.д.). Поле `UserProperty.operation` больше не публичное мутабельное — читается через `getOperation()`, класс стал неизменяемым.
* **Тип колбэка `CarrotSDK.Callback` убран** → top-level `io.carrotquest_sdk.android.Callback<T>`; брендовые `Carrot.Callback` / `Dashly.Callback` сохранены как взаимозаменяемые подтипы.
* **Push-хелперы — только `Map<String, String>`.** `RemoteMessage`-перегрузки и `sendFcmToken(...)` удалены — используйте `sendPushToken(token)` и Map-варианты (см. раздел [«Уведомления»](#уведомления)).

Если в вашем приложении есть авторизация пользователей, вызывайте её при старте приложения — наилучшее место — `onResponse` колбэка `setup`. Это предотвратит лишнее возникновение анонимных пользователей:
```kotlin
Carrot.setup(this, yourApiKey, object : Carrot.Callback<Boolean> {
    override fun onResponse(result: Boolean) {
        if (result) {
            Carrot.auth(userId, userAuthKey, object : Carrot.Callback<String> {
                override fun onResponse(result: String?) { }
                override fun onFailure(t: Throwable) { }
            })
        }
    }

    override fun onFailure(t: Throwable) { }
})
```

## Инициализация
Для работы с Carrot quest для Android вам понадобится API Key и User Auth Key. Вы можете найти эти ключи на вкладке Настройки > Разработчикам:
![Api keys](https://github.com/carrotquest/android-sdk/blob/carrotquest/img/carrot_api_keys.png?raw=true)

Для инициализации Carrot quest вам нужно выполнить следующий код в методе onCreate() вашего приложения:


```kotlin
Carrot.setup(this, apiKey, callback)
```

Для вывода подробных логов SDK в logcat во время отладки задайте уровень логирования (по умолчанию `SdkLogLevel.NONE` — SDK не пишет в логи ничего):
```kotlin
Carrot.setLogLevel(SdkLogLevel.DEBUG)   // выключить: Carrot.setLogLevel(SdkLogLevel.NONE)
```
Опционально можно получать записи логов в своём коде (например, прокинуть в свой логгер/крэш-репортер) и снять одномоментный срез состояния SDK для баг-репортов:
```kotlin
Carrot.setLogSink { entry -> myLogger.log(entry.category.toString() + " " + entry.message) }
// чувствительные значения (токены/e-mail/идентификаторы) маскируются по умолчанию;
// показать полностью (только для локальной отладки):
Carrot.setLogIncludeSensitive(true)

val report = Carrot.getDiagnostics().toFormattedString()
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

Для описания свойств пользователя используйте класс `UserProperty` (`io.carrotquest_sdk.android.models.UserProperty`):
```java
public UserProperty(String key, String value)
public UserProperty(Operation operation, String key, String value)
```
`Operation` — это enum `io.carrotquest_sdk.android.models.Operation` со значениями в `UPPER_CASE` (`UPDATE_OR_CREATE`, `SET_ONCE`, `ADD`, `DELETE`, `APPEND`, `UNION`, `EXCLUDE`). Более подробно про операции можно прочитать в разделе [«Cвойства пользователя»](https://carrotquest.io/developers/props/#_3).

`Внимание!`

Поле `key` не может начинаться с символа `$`.


Для установки [системных свойств](https://carrotquest.io/developers/props#_4) реализовано 2 класса `CarrotUserProperty` и `EcommerceUserProperty` (пакет `io.carrotquest_sdk.android.models`).

Для отслеживания событий используйте
```kotlin
Carrot.trackEvent(eventName)
```
Вы можете указать дополнительные параметры для события. Соберите их типобезопасным билдером `EventParams` — SDK сам сериализует значения в JSON (передавать JSON-строку вручную больше не нужно):
```kotlin
Carrot.trackEvent("purchase", EventParams.builder()
    .put("item", "book")
    .put("price", 9.99)
    .put("gift", true)
    .build())
```
`put(...)` перегружен для `String`/`Int`/`Long`/`Double`/`Boolean`.
В SDK есть возможность трекинга навигации внутри приложения для того, чтобы при необходимости запускать различные триггерные сообщения на определенных экранах. Для этого используйте метод
```kotlin
Carrot.trackScreen(screenName)
```
Для передачи UTM-меток из ссылки используйте
```kotlin
Carrot.trackUtm(url)
```
Метод извлекает UTM-параметры из строки запроса переданного URL, фиксирует их как события и устанавливает соответствующие свойства пользователя. Типичный сценарий использования — вызов метода при открытии приложения по диплинку:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    intent?.data?.toString()?.let { Carrot.trackUtm(it) }
}
```
Вы можете получить список идентификаторов непрочитанных на данный момент диалогов. Это единственный метод SDK, который бросает исключение (`CarrotException`), — вызывайте его в `try/catch`:
```kotlin
 try {
     val unread = Carrot.getUnreadConversations()
 } catch (e: CarrotException) {
     // SDK не инициализирован или пользователь недоступен
 }
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