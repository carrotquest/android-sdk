## Carrot quest для Android

Carrot quest для Android поддерживает API 16 и выше.

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
    implementation 'io.carrotquest:android-sdk:1.0.75-commonRelease'
}
```

Библиотека использует Java 8. Если ваш проект использует версию Java ниже 8, добавьте следующие настройки:
```groovy
android {
    ...
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
}
```


## Инициализация
Для работы с Carrot quest для Android вам понадобится API Key и User Auth Key. Вы можете найти эти ключи на вкладке Настройки > Разработчикам:
![Api keys](https://github.com/carrotquest/android-sdk/blob/carrotquest/img/carrot_api_keys.png?raw=true)

Для инициализации Carrot quest вам нужно выполнить следующий код в методе onCreate() вашего приложения:

```java
Carrot.setup(this, apiKey, appId);
```
или
```java
Carrot.setup(this, apiKey, appId, callback)
```

Для вывода дополнительной информации во время отладки используйте метод:
```java
Carrot.setDebug(true);
```

## Авторизация пользователей

Если в вашем приложении присутствует авторизация пользователей, вы можете передать id пользователя в Carrot:

```java
Carrot.auth(userId, userAuthKey);
```
или
```java
Carrot.auth(userId, userAuthKey, callback)
```

Чтобы сменить пользователя, нужно сначала вызвать метод деинициализации:
```java
Carrot.deInit()
```
а после завново вызвать методы ининциализации и (опционально) авторизации.


## Свойства пользователей и события

Вы можете установить необходимые свойства пользователя с помощью
```java
Carrot.setUserProperty(userProperty);
Carrot.setUserProperty(userPropertyList);
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
Также можно подписаться на изменения в списке идентификаторов непрочитанных диалогов
```java
 Carrot.setUnreadConversationsCallback(callback);
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
```java
Carrot.openChat(context);
```

### Уведомления
Для работы с уведомлениями SDK использует сервис Firebase Cloud Messaging. В связи с этим необходимо получить ключ и отправить его в Carrot. Вы можете найти поле для ввода ключа на вкладке Настройки > Разработчикам. Процесс настройки сервиса Firebase Cloud Messaging описан [здесь](https://firebase.google.com/docs/cloud-messaging?authuser=0)

Если вы уже используете сервис Firebase Cloud Messaging для своих push-уведомлений, то для корректной работы push-уведомлений в SDK необходимо отредактировать вашу службу FirebaseMessagingService. Это необходимо для "прокидывания" токена и наших сообщений внутрь SDK. Пример:
``` java
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Carrot.sendFcmToken(token);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived (RemoteMessage remoteMessage) {
        if (Carrot.isCarrotPush(remoteMessage)) {
            Carrot.sendFirebasePushNotification(remoteMessage, this)
        } else {
            //Your code
        }
    }
}
```

Иконку и цвет уведомлений о новых сообщениях можно изменить.
Для установки иконки на уведомления вызовете следующий метод после инициализации SDK:
```java
Carrot.setNotificationIcon(R.drawable.ic_notificatrion_icon);
```
Либо добавьте иконку с названием `ic_cq_notification.xml` в директорию `res/drawable`
Для установки цвета уведомлений в файл ресурсов пропишите цвет с названием `colorCqNotify` и нужным вам значением:
``` xml
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
