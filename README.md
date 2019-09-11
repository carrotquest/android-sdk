## Dashly for Android (Beta)

`Attention!` Library is a work in progress. Malfunction is possible.

Dashly for Android supports API 16 and later.

## Install
At the moment Dashly for Android can be installed via gradle.
Add `build.gradle` repository into project file:
```groovy
allprojects {
    ...
    repositories {
        ...
        maven { url "https://raw.github.com/dashly/android-sdk/beta_us" }
        maven { url "https://jitpack.io" }
    }
}
```
Configure dependencies in your application's `build.gradle` file:
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
    implementation 'io.carrotquest:android-sdk:1.0.20-usRelease'
}
```

Java 8 is used by the library. Add the following settings in case your project is using an older version of Java:
```groovy
android {
    ...
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
}
```


## Initialization
You'll need API Key and User Auth Key to work with Dashly for Android. Those can be found on Settings - API Keys tab:
![Api keys](/img/dashly_api_keys.png)

You should run this code in your application's onCreate() method in order to initialize Dashly:

```java
Dasly.setup(this, apiKey);
```

Use this method to display additional info during debug process:
```java
Dasly.isDebug(true);
```

## User authorization

In case your application has user authorization, you might want to send user id to Dashly:

```java
Dasly.auth(userId, userAuthKey);
```
or
```java
Dasly.auth(userId, userAuthKey, callback)
```

## User properties and events

You can set user properties, using this method:
```java
Dasly.setUserProperty(userProperty);
Dasly.setUserProperty(userPropertyList);
```

`UserProperty` class should be used for user properties description
```java
public UserProperty(String key, String value)
public UserProperty(Operation operation, String key, String value)
```
More info on `Operations` can be found in [«User properties»](/props#_3) section.

`Important!`

`key` field value should not start with `$`.


`CarrotUserProperty` and `EcommerceUProperty` classes should be used to set [system properties](/props#_4)

Use the following method for events tracking:
```java
Dasly.trackEvent(eventName);
```
You can send additional event parameters as JSON string
```java
Dasly.trackEvent(eventName, eventParams);
```

## Live chat
You can give your users an opportunity to start a live chat (with your operator) from anywhere. This can be done two ways - either by adding a floating button or by directly calling a chat
openning method at desired moment.

### Floating Button
This is an interface element inherited from `ConstraintLayout`. You can embed it in your markup:
``` xml
<io.carrot_sdk.android.ui.fab.FloatingButton
        android:id="@+id/cq_sdk_float_button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:cq_location_fab="BOTTOM_RIGHT"
        app:cq_visibility_background="false"
        app:cq_icon_fab="@drawable/ic_send"
        app:cq_margin_fab="8dp"
/>
```
This element has it's own attributes: 
    `app:cq_location_fab` controls button location inside parent element. 4 options are available - `TOP_LEFT`, `TOP_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_RIGHT`. Default - `BOTTOM_RIGHT`.
    `app:cq_visibility_background` controlls fogging effect visibility on floating button tap. Default `true`. 
    `app:cq_icon_fab` floating button icon. Default `@id/ic_cq_message`.
    `app:cq_margin_fab` controls floating button margins (inside parent element). Default `16dp`.

#### Floating button interface
Available floating button behaviour configuration and control methods.

``` java
/**
 * Show floating button
 */
public void showFab()
```

``` java
/**
 * Hide floating button
 */
 public void hideFab()
```

``` java
/**
 * Show integrations buttons
 */
public void expandMenu()
```

``` java
/**
 * Hide integrations buttons
 */
public void collapseMenu()
```

``` java
/**
 * Set chat icon
 * @param iconFAB Icon
 */
public void setIconFAB(Drawable iconFAB)
```

``` java
/**
 * Set button margins (from screen borders)
 * @param margin Margin value
 */
public void setMarginFAB(int margin) 
```

``` java
/**
 * Set button location
 * @param location Button location
 */
public void setLocationFAB(LocationFAB location)
```


### Open chat from anywhere
After initialization you can open chat from any place using thix method:
```java
Dasly.openChat(context);
```

### Notofications
SDK uses Firebase Cloud Messaging for sending notifications. At the moment you are required to get a key and send it to our support. Cloud Messaging setup is described [here](https://firebase.google.com/docs/cloud-messaging?authuser=0)

You can setup notifications icon using this method
``` java
Dasly.setNotificationIcon(notificationIconId)
```
`notificationIconId` - icon source id.
