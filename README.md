# Table of Contents

* [Dashly for Android](#dashly-for-android)
* [Installation](#installation)
* [Upgrade to Version 3.0.0](#upgrade)
* [Initialization](#initialization)
* [User Authentication](#user-authentication)
* [User Properties and Events](#user-properties-and-events)
* [Chat with Operator](#chat-with-operator)
  * [Floating Button](#floating-button)
  * [Opening Chat from Any Location](#opening-chat-from-any-location)
* [Notifications](#notifications)
  * [Firebase Cloud Messaging Setup](#firebase-cloud-messaging-setup)
  * [Huawei Push Kit Setup](#huawei-push-kit-setup)
  * [General Notification Settings](#general-notification-settings)
  * [Notifications unsubscribe method](#notifications-unsubscribe-method)


## Dashly for Android

Dashly for Android supports API 21 and above.

Detailed SDK documentation is available at [developers.dashly.io](https://developers.dashly.io).

## Installation
Currently, Dashly for Android can be installed using gradle.
To do this, add the repository to the project `build.gradle` file:
```groovy
allprojects {
    ...
    repositories {
        ...
        maven { url "https://raw.github.com/carrotquest/android-sdk/dashly" }
        maven { url "https://jitpack.io" }
    }
}
```
Specify the dependencies in your application's `build.gradle` file:
```groovy
android {
    ...
    packagingOptions {
        exclude 'META-INF/*.kotlin_module'
    }
}

dependencies {
    ...
    implementation 'io.carrotquest:android-sdk:3.3.0-usRelease'
}
```

The library uses Java 17. If your project uses a Java version below 17, add the following settings:
```groovy
android {
    ...
    compileOptions {
        sourceCompatibility '17'
        targetCompatibility '17'
    }
}
```

## Upgrade
Version **3.0.0** is a major update with breaking changes to the public API. What to take into account when upgrading:

* **New logging system.** `setDebug(boolean)` / `isDebug()` are removed — the level is set via `setLogLevel(SdkLogLevel)` with the values `NONE`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `VERBOSE`. The default is `NONE`: the SDK writes nothing to the logs, including in release builds (previously some logs were always emitted). Additionally, you can receive log records directly in your own code via `setLogSink(...)` (for example, forward them to your logger or crash reporter) and take a point-in-time snapshot of the SDK state for bug reports via `getDiagnostics()`. Sensitive data (tokens, e-mails, identifiers) is masked until `setLogIncludeSensitive(true)` is called. Usage examples are in the [«Initialization»](#initialization) section.
* **State subscriptions removed** (`addStateObserver` / `getStateFlow` / `addAuthStateObserver` / `getAuthStateFlow` / `addSessionExpiredObserver` / `getInitObservable`, as well as the `DashlyState` / `DashlyAuthState` models). Session recovery is now automatic: take operation results from their `Callback`, and the initialization status from `Dashly.isInit()`.
* **Unsubscribe via `Cancellable`.** Subscription methods (for example, `setUnreadConversationsCallback`) return a `Cancellable` — call `.cancel()` to unsubscribe.
* **`UserProperty` / `Operation` consolidated.** `Operation` is a single enum `io.carrotquest_sdk.android.models.Operation` with `UPPER_CASE` values (`set_once` → `SET_ONCE`, `update_or_create` → `UPDATE_OR_CREATE`, etc.). The `UserProperty.operation` field is no longer a public mutable field — it is read via `getOperation()`, and the class is now immutable.
* **The `CarrotSDK.Callback` callback type is removed** → top-level `io.carrotquest_sdk.android.Callback<T>`; the branded `Carrot.Callback` / `Dashly.Callback` are kept as interchangeable subtypes.
* **Push helpers accept only `Map<String, String>`.** The `RemoteMessage` overloads and `sendFcmToken(...)` are removed — use `sendPushToken(token)` and the Map variants (see the [«Notifications»](#notifications) section).

If your application has user authentication, call it at the start of the application — the best place is the `onResponse` callback of `setup`. This prevents unnecessary anonymous user occurrences:
```kotlin
Dashly.setup(this, yourApiKey, object : Dashly.Callback<Boolean> {
    override fun onResponse(result: Boolean) {
        if (result) {
            Dashly.auth(userId, userAuthKey, object : Dashly.Callback<String> {
                override fun onResponse(result: String?) { }
                override fun onFailure(t: Throwable) { }
            })
        }
    }

    override fun onFailure(t: Throwable) { }
})
```

## Initialization
To use Dashly for Android, you need the API Key and User Auth Key. You can find these keys in the Settings > Developers tab:
![Api keys](https://github.com/carrotquest/android-sdk/blob/dashly/img/dashly_api_keys.png?raw=true)

To initialize Dashly, you need to execute the following code in your application's onCreate() method:

```kotlin
Dashly.setup(this, apiKey, callback)
```

To print detailed SDK logs to logcat during debugging, set the log level (the default is `SdkLogLevel.NONE` — the SDK writes nothing to the logs):
```kotlin
Dashly.setLogLevel(SdkLogLevel.DEBUG)   // to turn off: Dashly.setLogLevel(SdkLogLevel.NONE)
```
Optionally, you can receive log records in your own code (for example, forward them to your logger/crash reporter) and take a point-in-time snapshot of the SDK state for bug reports:
```kotlin
Dashly.setLogSink { entry -> myLogger.log(entry.category.toString() + " " + entry.message) }
// sensitive values (tokens/e-mails/identifiers) are masked by default;
// show them in full (for local debugging only):
Dashly.setLogIncludeSensitive(true)

val report = Dashly.getDiagnostics().toFormattedString()
```

## User Authentication

If your application includes user authentication, you can pass the user id to Dashly. There are two ways to authenticate: directly pass the userAuthKey, or send a hash generated on your backend. Upon successful login, the dashly_id property value is returned in the callback.

1. Login via user auth key:

```kotlin
Dashly.auth(userId, userAuthKey, callback)
```

2. Login via hash:

```kotlin
Dashly.hashedAuth(userId, hash, callback)
```

To change the user, you need to first call the deinitialization method, and then re-call the initialization and (optionally) authentication methods:
```kotlin
Dashly.deInit(object : Dashly.Callback<Boolean> {
    override fun onResponse(result: Boolean) {
        Dashly.setup(this, yourApiKey, callbackSetup)
    }

    override fun onFailure(t: Throwable) {
        
    }
})
```

## User Properties and Events

You can set user properties using:
```kotlin
Dashly.setUserProperty(userProperty)
Dashly.setUserProperty(userPropertyList)
```

To describe user properties, use the `UserProperty` class (`io.carrotquest_sdk.android.models.UserProperty`):
```java
public UserProperty(String key, String value)
public UserProperty(Operation operation, String key, String value)
```
`Operation` is the enum `io.carrotquest_sdk.android.models.Operation` with `UPPER_CASE` values (`UPDATE_OR_CREATE`, `SET_ONCE`, `ADD`, `DELETE`, `APPEND`, `UNION`, `EXCLUDE`). For more details on operations, please refer to the [«User Properties»](https://developers.dashly.io/props/#_3) section.

`Attention!`

The `key` field cannot start with the `$` symbol.

For setting [system properties](https://developers.dashly.io/props#_4), two classes `CarrotUserProperty` and `EcommerceUserProperty` are implemented (package `io.carrotquest_sdk.android.models`).

To track events, use:
```kotlin
Dashly.trackEvent(eventName)
```
You can specify additional parameters for an event. Build them with the type-safe `EventParams` builder — the SDK serializes the values to JSON itself (you no longer need to pass a JSON string manually):
```kotlin
Dashly.trackEvent("purchase", EventParams.builder()
    .put("item", "book")
    .put("price", 9.99)
    .put("gift", true)
    .build())
```
`put(...)` is overloaded for `String`/`Int`/`Long`/`Double`/`Boolean`.

The SDK offers the ability to track navigation within the application to launch various trigger messages on specific screens if needed. Use the following method for this:
```kotlin
Dashly.trackScreen(screenName)
```
To pass UTM tags from a link, use:
```kotlin
Dashly.trackUtm(url)
```
The method extracts UTM parameters from the query string of the provided URL, records them as events, and sets the corresponding user properties. The typical use case is calling this method when the application is opened via a deep link:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    intent?.data?.toString()?.let { Dashly.trackUtm(it) }
}
```
You can retrieve a list of identifiers for the conversations that are currently unread. This is the only SDK method that throws an exception (`CarrotException`) — call it inside a `try/catch`:
```kotlin
 try {
     val unread = Dashly.getUnreadConversations()
 } catch (e: CarrotException) {
     // the SDK is not initialized or the user is unavailable
 }
```
You can also subscribe to changes in the list of unread conversation identifiers:
```kotlin
 Dashly.setUnreadConversationsCallback(callback)
```

## Chat with Operator

You can give the mobile app user the ability to access the chat with an operator from anywhere. This can be accomplished in two ways—using a floating button or directly calling the chat opening method at any desired time.

### Floating Button

Essentially, this is a user interface element inheriting from `ConstraintLayout`. You can embed it into your layout:
```xml
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

This element has its own attributes:
- `app:cq_location_fab` dictates the placement of the floating button relative to its parent container. There are 4 possible positions: `TOP_LEFT`, `TOP_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_RIGHT`; default is `BOTTOM_RIGHT`.
- `app:cq_visibility_background` controls the visibility of the dimming effect when the floating button is pressed. Default is `true`.
- `app:cq_icon_fab` sets the icon for the floating button. Default is `@id/ic_cq_message`.
- `app:cq_margin_fab` specifies the margins of the floating button relative to its parent container. Default is `16dp`.
- `app:cq_show_social_labels` manages the visibility of labels next to social media icons. Default is `true`.
- `app:cq_auto_hide_fab` determines automatic hiding of the chat button when the internet is unavailable. Default is `false`.

#### Floating Button Interface

Available methods for configuring and managing the floating button's behavior:

```java
/**
 * Show the floating button
 */
public void showFab()
```

```java
/**
 * Hide the floating button
 */
public void hideFab()
```

```java
/**
 * Show integration buttons
 */
public void expandMenu()
```

```java
/**
 * Hide integration buttons
 */
public void collapseMenu()
```

```java
/**
 * Set chat icon
 * @param iconFAB Icon
 */
public void setIconFAB(Drawable iconFAB)
```

```java
/**
 * Set button margins from screen edges
 * @param margin Margin value
 */
public void setMarginFAB(int margin)
```

```java
/**
 * Set button location
 * @param location Button location
 */
public void setLocationFAB(LocationFAB location)
```

### Opening Chat from Any Location

You can also open the chat by executing the following code from any location (post initialization):
```kotlin
Dashly.openChat(context)
```

# Notifications

The SDK supports two push notification providers - Firebase Cloud Messaging and Huawei Push Kit.

## Firebase Cloud Messaging Setup

First, you need to obtain a key and send it to Dashly. You can find the field for entering the key in Settings > Developers > Push notifications for SDK. The setup process for Firebase Cloud Messaging is described here.

If you are already using Firebase Cloud Messaging for your push notifications, to ensure that push notifications work correctly in the SDK, you need to edit your FirebaseMessagingService. This is necessary to pass the token and our messages into the SDK. Example:

```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val pushData: Map<String, String> = message.data

        if (Dashly.isDashlyPush(pushData)) {
            Dashly.sendPushNotification(pushData, this)
        } else {
            // Your code
        }
    }

    override fun onNewToken(token: String) {
        Dashly.sendPushToken(token)
        super.onNewToken(token)
    }
}
```

## Huawei Push Kit Setup

To deliver notifications to users with devices without Google services, you can use Huawei's push notification delivery service. First, you need to integrate HPK into your application. Instructions can be found here. Then, in Settings > Developers > Push notifications for SDK, you need to provide the Client ID, Client Secret, and Webhook Secret. Next, modify the service inherited from HmsMessageService. Example:

```kotlin
class MyHuaweiPushKitService : HmsMessageService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val pushData: Map<String, String> = remoteMessage?.dataOfMap ?: HashMap()
        if (Dashly.isDashlyPush(pushData)) {
            Dashly.sendPushNotification(pushData, this)
        } else {
            // Your code
        }
    }

    override fun onNewToken(token: String?) {
        Dashly.sendPushToken(token)
        super.onNewToken(token)
    }

    override fun onNewToken(token: String?, p1: Bundle?) {
        Dashly.sendPushToken(token)
        super.onNewToken(token, p1)
    }
}
```

## General Notification Settings

You can change the icon and color of notifications for new messages. To set the icon for notifications, call the following method after initializing the SDK:

```kotlin
Dashly.setNotificationIcon(R.drawable.ic_notification_icon)
```

Alternatively, add an icon named `ic_cq_notification.xml` in the `res/drawable` directory. To set the notification color, specify a color named `colorCqNotify` with your desired value in the resource file:

```xml
<color name="colorCqNotify">#EF7F28</color>
```

If you want to receive information about new messages in the SDK from anywhere in your application, you can implement a `BroadcastReceiver`. Example implementation:

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
`IncomingMessage` is a class that describes the incoming message.

Next, you need to register it:

```java
MyNewMessageBroadcastReceiver messageReceiver = new MyNewMessageBroadcastReceiver();
IntentFilter filter = new IntentFilter();
filter.addAction(NotificationsConstants.CQ_SDK_NEW_MESSAGE_ACTION);
registerReceiver(messageReceiver, filter);
```

Important! If the application is closed and the user opens the chat by clicking a push, your start activity will not launch. The application will close along with the closure of the chat. To fix this, you can pass the full name of the activity that should launch when the chat is closed:

```java
Dashly.setParentActivityClassName("io.test.MainActivity");
```

## Notifications unsubscribe method

There are methods to unsubscribe a particular user from push notifications and from all campaigns in general.

A method for unsubscribing from push notifications:

```kotlin
Dashly.pushNotificationsUnsubscribe()
```

A method for unsubscribing from all push campaigns:

```kotlin
Dashly.pushCampaignsUnsubscribe()
```
