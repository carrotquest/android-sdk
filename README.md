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
    implementation 'io.carrotquest:android-sdk:3.3.0-commonRelease'
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

На устройствах Huawei без Google-сервисов (большинство моделей, выпущенных после 2019 года) FCM не работает. Чтобы доставлять пуши и на них, в дополнение к FCM используется Huawei Push Kit (HPK). SDK сам определит, что устройство Huawei, и подменит канал доставки — со стороны интеграции достаточно пробросить HPK-токен и входящие сообщения теми же методами `Carrot.sendPushToken` / `Carrot.isCarrotPush` / `Carrot.sendPushNotification`.

> **HPK настраивается в дополнение к FCM, а не вместо него.** Сначала настройте FCM по шагам выше — он остаётся каналом доставки для всех остальных Android-устройств. Оба сервиса (`FirebaseMessagingService` и `HmsMessageService`) спокойно живут в одном приложении.

Настройка состоит из четырёх частей: консоль Huawei → ключи в личном кабинете Carrot quest → подключение HMS в проект → код приложения.

### Шаг 1. Создайте проект в консоли Huawei

1. Зарегистрируйте аккаунт разработчика на [developer.huawei.com](https://developer.huawei.com/) и пройдите верификацию (подтверждение личности или организации). Без верификации Push Kit включить нельзя; проверка документов может занять несколько дней.
2. В консоли [AppGallery Connect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html) создайте проект и добавьте в него Android-приложение. Имя пакета (package name) должно совпадать с `applicationId` вашего приложения.
3. В настройках приложения укажите SHA-256 отпечаток сертификата подписи — без него HMS не выдаст пуш-токен. Как получить отпечаток: [инструкция Huawei](https://developer.huawei.com/consumer/en/doc/HMSCore-Guides/config-agc-0000001050170137).
4. Включите Push Kit: в меню проекта слева откройте Рост (Grow) > Push Kit и нажмите «Включить».

### Шаг 2. Скопируйте Client ID и Client Secret

В AppGallery Connect откройте Настройки проекта > Данные приложения (App information) и найдите блок «ID клиента OAuth 2.0». Скопируйте оба значения — `Client ID` и `Client Secret`. Они понадобятся на шаге 4.

### Шаг 3. Настройте уведомление о получении сообщений (вебхук)

> **Этот шаг обязателен.** В отличие от FCM, Huawei сообщает о статусе доставки пуша не в ответе на отправку, а асинхронно — вебхуком. Без настроенного вебхука Carrot quest не сможет отслеживать доставку пушей на Huawei-устройства.

1. В AppGallery Connect откройте Рост (Grow) > Push Kit > Настройки.
2. Включите «Уведомление о получении сообщений» на уровне проекта и нажмите «Создать» в появившемся окне.
3. Заполните поля:

    | Поле | Значение |
    |---|---|
    | Имя | `carrotquest` |
    | Адрес подтверждения получения (callback URL) | `https://api.carrotquest.io/messages/webhooks/huawei/status?app=APP_ID` |
    | Имя пользователя подтверждения получения | строго `carrotquest` |
    | Ключ подтверждения получения | нажмите «Сгенерировать» и **сохраните значение** — это ваш `Webhook Secret` для шага 4 |
    | Версия | `v2` |

    `APP_ID` в адресе замените на идентификатор вашего аппа в Carrot quest. Он лежит там же, где ключи для SDK: Настройки > Разработчикам.

4. Нажмите «Тест» — Huawei отправит проверочный запрос на указанный адрес. Если проверка прошла успешно, нажмите «Отправить».

### Шаг 4. Загрузите ключи в Carrot quest

В личном кабинете Carrot quest откройте Настройки > Разработчикам > Push-уведомления для SDK и заполните три поля:

* `Client ID` и `Client Secret` — из шага 2;
* `Webhook Secret` — ключ подтверждения получения, сгенерированный на шаге 3.

![Подключение Huawei Push Kit](https://github.com/carrotquest/android-sdk/blob/carrotquest/img/hpk.png?raw=true)

Не забудьте нажать «Сохранить».

### Шаг 5. Подключите HMS в проект

1. В AppGallery Connect на странице Настройки проекта > Данные приложения скачайте файл `agconnect-services.json` и положите его в каталог модуля приложения (рядом с `build.gradle` модуля `app`).
2. Добавьте репозиторий Huawei и плагин AppGallery Connect. В `build.gradle` уровня проекта:

    ```groovy
    buildscript {
        repositories {
            google()
            mavenCentral()
            maven { url 'https://developer.huawei.com/repo/' }
        }
        dependencies {
            classpath 'com.huawei.agconnect:agcp:latestVersion'
        }
    }
    ```

    Репозиторий `https://developer.huawei.com/repo/` также нужно добавить туда, где ваш проект объявляет репозитории зависимостей: в `allprojects { repositories { ... } }` или, для новых проектов, в `dependencyResolutionManagement { repositories { ... } }` файла `settings.gradle[.kts]`.

3. В `build.gradle` модуля приложения примените плагин и добавьте зависимость Push Kit:

    ```groovy
    apply plugin: 'com.huawei.agconnect'

    dependencies {
        implementation 'com.huawei.hms:push:latestVersion'
    }
    ```

Вместо `latestVersion` подставьте актуальные версии плагина и Push Kit — их можно посмотреть в [официальной инструкции по интеграции HMS](https://developer.huawei.com/consumer/en/doc/HMSCore-Guides/service-introduction-0000001050040060).

### Шаг 6. Передача токена и обработка push

Создайте сервис, унаследованный от `HmsMessageService`, и пробросьте в SDK токен и входящие сообщения:

```kotlin
class MyHuaweiPushKitService : HmsMessageService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        val pushData = message?.dataOfMap.orEmpty()
        if (Carrot.isCarrotPush(pushData)) {
            Carrot.sendPushNotification(pushData, this)
        } else {
            // Логика показа собственных push-уведомлений
        }
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Carrot.sendPushToken(token)
    }

    override fun onNewToken(token: String?, bundle: Bundle?) {
        super.onNewToken(token, bundle)
        Carrot.sendPushToken(token)
    }
}
```

Зарегистрируйте сервис в `AndroidManifest.xml` внутри тега `<application>`:

```xml
<service
    android:name=".MyHuaweiPushKitService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.huawei.push.action.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

### Как проверить

1. Соберите приложение и запустите его на реальном Huawei-устройстве (на нём должен быть установлен HMS Core — на устройствах без Google-сервисов он есть из коробки).
2. Убедитесь, что в `onNewToken` вашего `HmsMessageService` пришёл токен и он передан в `Carrot.sendPushToken` (например, добавьте лог).
3. Сверните приложение и отправьте пользователю ручное сообщение из кабинета Carrot quest с включённым чекбоксом «Отправить push-уведомление» — на устройство должен прийти пуш.
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