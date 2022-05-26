package io.carrotquest.sample.auth.presenter

import android.content.Context
import io.carrotquest.sample.auth.view.IAuthView
import io.carrotquest.sample.constants.*
import io.carrotquest.sample.utils.SharedPreferencesUtil
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.core.main.CarrotSDK
import io.carrotquest_sdk.android.models.UserProperty
import java.util.*

class AuthPresenter(private var view: IAuthView?) {

    fun onStart(context: Context) {
        val savedName = SharedPreferencesUtil.getString(context, USER_NAME)
        view?.showName(savedName)

        val savedEmail = SharedPreferencesUtil.getString(context, USER_EMAIl)
        view?.showEmail(savedEmail)

        val savedPhone = SharedPreferencesUtil.getString(context, USER_PHONE)
        view?.showPhone(savedPhone)
    }

    fun onTapClose() {
        view?.close()
    }

    fun onTapDone(context: Context, name: String, email: String, phone: String) {
        if(Carrot.isInit()) {
            saveName(name, context)
            saveEmail(email, context)
            savePhone(phone, context)

            authSdk(context)
        } else {
            initSdk(context)
        }
    }

    fun detachView() {
        this.view = null
    }

    fun onDestroy() {
        detachView()
    }

    fun onChangeName(name: String, context: Context) {
        saveName(name, context)
    }

    fun onChangeEmail(email: String, context: Context) {
      saveEmail(email, context)
    }

    fun onChangePhone(phone: String, context: Context) {
       savePhone(phone, context)
    }

    private fun saveName(name: String, context: Context) {
        val oldName = SharedPreferencesUtil.getString(context, USER_NAME)
        if(oldName != name) {
            SharedPreferencesUtil.saveString(context, USER_NAME, name)
            Carrot.setUserProperty(UserProperty("\$name", name))
        }
    }

    private fun saveEmail(email: String, context: Context) {
        val oldEmail = SharedPreferencesUtil.getString(context, USER_EMAIl)
        if(oldEmail != email) {
            SharedPreferencesUtil.saveString(context, USER_EMAIl, email)
            Carrot.setUserProperty(UserProperty("\$email", email))
        }
    }

    private fun savePhone(phone: String, context: Context) {
        val oldPhone = SharedPreferencesUtil.getString(context, USER_PHONE)
        if(oldPhone != phone) {
            SharedPreferencesUtil.saveString(context, USER_PHONE, phone)
            Carrot.setUserProperty(UserProperty("\$phone", phone))
        }
    }

    private fun initSdk(context: Context) {
        val apiKey = if(SharedPreferencesUtil.getString(context, API_KEY_SP).isNotEmpty()) SharedPreferencesUtil.getString(context, API_KEY_SP) else API_KEY
        val appId = if(SharedPreferencesUtil.getString(context, APP_ID_SP).isNotEmpty()) SharedPreferencesUtil.getString(context, APP_ID_SP) else APP_ID
        val userAuthKey = if(SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP).isNotEmpty()) SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP) else USER_AUTH_KEY

        if(apiKey.isNotEmpty() && appId.isNotEmpty() && userAuthKey.isNotEmpty()) {
            Carrot.setup(context, apiKey, appId, object : CarrotSDK.Callback<Boolean>{
                override fun onFailure(p0: Throwable?) {

                }

                override fun onResponse(resConnect: Boolean?) {
                    if(resConnect != null && resConnect) {
                        authSdk(context)
                    }
                }
            })
        }
    }

    private fun authSdk(context: Context) {
        val userAuthKey = if(SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP).isNotEmpty()) SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP) else USER_AUTH_KEY
        if(userAuthKey.isNotEmpty()) {
            var userId = SharedPreferencesUtil.getString(context, USER_ID)
            if (userId.isEmpty()) {
                userId = UUID.randomUUID().toString()
                SharedPreferencesUtil.saveString(context, USER_ID, userId)
            }
            Carrot.auth(userId, userAuthKey, object : CarrotSDK.Callback<Boolean>{
                override fun onResponse(resAuth: Boolean?) {
                    if(resAuth != null && resAuth) {
                        view?.close()
                    }
                }

                override fun onFailure(p0: Throwable?) {

                }
            })
        }
    }
}