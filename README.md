<<<<<<< HEAD
## Dashly for Android (Beta)

`Attention!` Library is a work in progress. Malfunction is possible.

Dashly for Android supports API 16 and later.

## Install
At the moment Dashly for Android can be installed via gradle.
Add `build.gradle` repository into project file:
=======
## Carrot quest для Android (Beta)

`Внимание!` Библиотека находится в стадии активной разработки. Возможны сбои в работе.

Carrot quest для Android поддерживает API 16 и выше.

## Установка
На данный момент Carrot quest для Android можно установить с помощью gradle.
Для этого добавьте репозиторий в `build.gradle` файле проекта:
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
```groovy
allprojects {
    ...
    repositories {
        ...
<<<<<<< HEAD
        maven { url "https://raw.github.com/carrotquest/android-sdk/beta_us" }
=======
        maven { url "https://raw.github.com/carrotquest/android-sdk/beta" }
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
        maven { url "https://jitpack.io" }
    }
}
```
<<<<<<< HEAD
Configure dependencies in your application's `build.gradle` file:
=======
Укажите зависимости в `build.gradle` файле вашего приложения:
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
```groovy
android {
    ...
    defaultConfig {
        ...
        multiDexEnabled true
    }
}

dependencies {
    ...
    implementation 'com.android.support:multidex:1.0.3'
<<<<<<< HEAD
    implementation 'io.carrotquest:android-sdk:1.0.15-usRelease'
}
```

Java 8 is used by the library. Add the following settings in case your project is using an older version of Java:
=======
    implementation 'io.carrotquest:android-sdk:1.0.16-commonRelease'
}
```

Библиотека использует Java 8. Если ваш проект использует версию Java ниже 8, добавьте следующие настройки:
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
```groovy
android {
    ...
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
}
```


<<<<<<< HEAD
## Initialization
You'll need API Key and User Auth Key to work with Dashly for Android. Those can be found on Settings - API Keys tab:
![Api keys](/img/dashly_api_keys.png)

You should run this code in your application's onCreate() method in order to initialize Dashly:
=======
## Инициализация
Для работы с Carrot quest для Android вам понадобится API Key и User Auth Key. Вы можете найти эти ключи на вкладке Настройки > API Ключи:
![Api keys](img/carrot_api_keys.png)

Для инициализации Carrot quest вам нужно выполнить следующий код в методе onCreate() вашего приложения:
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30

```java
Carrot.setup(this, apiKey);
```
<<<<<<< HEAD

Use this method to display additional info during debug process:
```java
Carrot.isDebug(true);
```

## User authorization

In case your application has user authorization, you might want to send user id to Dashly:
=======
или
```java
Carrot.setup(this, apiKey, callback)
```

Для вывода дополнительной информации во время отладки используйте метод:
```java
Carrot.setDebug(true);
```

## Авторизация пользователей

Если в вашем приложении присутствует авторизация пользователей, вы можете передать id пользователя в Carrot:
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30

```java
Carrot.auth(userId, userAuthKey);
```
<<<<<<< HEAD
or
```java
Carrot.auth(userId, userAuthKey, callback)
```

## User properties and events

You can set user properties, using this method:
=======
или
```java
Carrot.auth(userId, userAuthKey, callback)
```
## Свойства пользователей и события

Вы можете установить необходимые свойства пользователя с помощью
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
```java
Carrot.setUserProperty(userProperty);
Carrot.setUserProperty(userPropertyList);
```

<<<<<<< HEAD
`UserProperty` class should be used for user properties description
=======
Для описания свойств пользователя используйте класс `UserProperty`
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
```java
public UserProperty(String key, String value)
public UserProperty(Operation operation, String key, String value)
```
<<<<<<< HEAD
More info on `Operations` can be found in [«User properties»](/props#_3) section.

`Important!`

`key` field value should not start with `$`.


`CarrotUserProperty` and `EcommerceUProperty` classes should be used to set [system properties](/props#_4)

Use the following method for events tracking:
```java
Carrot.trackEvent(eventName);
```
You can send additional event parameters as JSON string
```java
Carrot.trackEvent(eventName, eventParams);
```

## Live chat
You can give your users an opportunity to start a live chat (with your operator) from anywhere. This can be done two ways - either by adding a floating button or by directly calling a chat
openning method at desired moment.

### Floating Button
This is an interface element inherited from `ConstraintLayout`. You can embed it in your markup:
``` xml
<io.carrot_sdk.android.ui.fab.FloatingButton
=======
Более подробно про `Operations` можно прочитать в разделе [«Cвойства пользователя»](https://carrotquest.io/developers/props/#_3).

`Внимание!`

Поле `key` не может начинаться с символа `$`.


Для установки [системных свойств](https://carrotquest.io/developers/props#_4) реализовано 2 класса `CarrotUserProperty` и `EcommerceUProperty`.

Для отслеживания событий используйте
```java
Carrot.trackEvent(eventName);
```
Вы можете указать дополнительные параметры для события в виде JSON-строки и передать их в метод
```java
Carrot.trackEvent(eventName, eventParams);
```
Вы можете получить список идентификаторов непрочитанных на данный момент диалогов
```java
 Carrot.getUnreadConversations();
```

## Чат с оператором
Вы можете дать пользователю мобильного приложения возможность перейти в чат с оператором из любого места. Это можно реализовать двумя разными путями - через плавающую кнопку, либо напрямую вызвав метод открытия чата в любое нужное время.

### Плавающая кнопка (Floating Button)
По своей сути - это элемент интерфейса, наследующийся от `ConstraintLayout`. Вы можете встроить его в свою разметку:
``` xml
<io.carrotquest_sdk.android.ui.FloatingButton
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
        android:id="@+id/cq_sdk_float_button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:cq_location_fab="BOTTOM_RIGHT"
        app:cq_visibility_background="false"
        app:cq_icon_fab="@drawable/ic_send"
        app:cq_margin_fab="8dp"
/>
```
<<<<<<< HEAD
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
=======
У этого элемента есть свои атрибуты: 
* `app:cq_location_fab` отвечает за расположение плавающей кнопки относительно её родительского контейнера. Возможны 4 варианта - `TOP_LEFT`, `TOP_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_RIGHT`. По умолчанию `BOTTOM_RIGHT`.
* `app:cq_visibility_background` отвечает за видимость эффекта затемнения при нажатии на плавающую кнопку. По умолчанию `true`. 
* `app:cq_icon_fab` задаёт иконку плавающей кнопки. По умолчанию `@id/ic_cq_message`.
* `app:cq_margin_fab` задаёт отступы плавающей кнопки относительно своего родительского контейнера. По умолчанию `16dp`.

#### Интерфейс плавающей кнопки
Доступные методы для настройки и управления поведением плавающей кнопки.

``` java
/**
 * Показать плавающую кнопку
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
 */
public void showFab()
```

``` java
/**
<<<<<<< HEAD
 * Hide floating button
=======
 * Скрыть плавающую кнопку
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
 */
 public void hideFab()
```

``` java
/**
<<<<<<< HEAD
 * Show integrations buttons
=======
 * Показать кнопки интеграции
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
 */
public void expandMenu()
```

``` java
/**
<<<<<<< HEAD
 * Hide integrations buttons
=======
 * Скрыть кнопки интеграции
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
 */
public void collapseMenu()
```

``` java
/**
<<<<<<< HEAD
 * Set chat icon
 * @param iconFAB Icon
=======
 * Установить иконку чата
 * @param iconFAB Иконка
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
 */
public void setIconFAB(Drawable iconFAB)
```

``` java
/**
<<<<<<< HEAD
 * Set button margins (from screen borders)
 * @param margin Margin value
=======
 * Установить отступы кнопки от краев экрана
 * @param margin Значение отступа
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
 */
public void setMarginFAB(int margin) 
```

``` java
/**
<<<<<<< HEAD
 * Set button location
 * @param location Button location
=======
 * Установить расположение кнопки
 * @param location Расположение кнопки
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
 */
public void setLocationFAB(LocationFAB location)
```


<<<<<<< HEAD
### Open chat from anywhere
After initialization you can open chat from any place using thix method:
=======
### Открытие чата из произвольного места
Открыть чат можно также, вызвав из произвольного места (после инициализации) следующий код:
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
```java
Carrot.openChat(context);
```

<<<<<<< HEAD
### Notofications
SDK uses Firebase Cloud Messaging for sending notifications. At the moment you are required to get a key and send it to our support. Cloud Messaging setup is described [here](https://firebase.google.com/docs/cloud-messaging?authuser=0)

You can setup notifications icon using this method
``` java
Carrot.setNotificationIcon(notificationIconId)
```
`notificationIconId` - icon source id.
=======
### Уведомления
Для работы с уведомлениями SDK использует сервис Firebase Cloud Messaging. В связи с этим на данном этапе необходимо получить ключ и отправить его нам в поддержку. Процесс настройки сервиса Firebase Cloud Messaging описан [здесь](https://firebase.google.com/docs/cloud-messaging?authuser=0)

Если вы уже используете сервис Firebase Cloud Messaging для своих push-уведомлений, то для корректной работы push-уведомлений в SDK необходимо отредактировать вашу службу FirebaseMessagingService. Это необходимо для "прокидывания" наших сообщений внутрь SDK. Пример:
``` java
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived (RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey(NotificationsConstants.CQ_SDK_PUSH) && "true".equals(data.get(NotificationsConstants.CQ_SDK_PUSH))) {
            Carrot.sendFirebaseNotification(remoteMessage);
        } else {
            //Your code
        }
    }
}
```

Иконку уведомлений можно устанавливать используя метод:
``` java
Carrot.setNotificationIcon(notificationIconId)
```
где `notificationIconId` - это идентификатор ресурса иконки.

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
>>>>>>> 4f281b5dedb5c9f17d6bd618e56c838a577fbe30
