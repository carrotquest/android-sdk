package io.carrotquest.sample.auth.presenter

import android.content.Context
import io.carrotquest.sample.auth.view.IAuthView
import io.carrotquest.sample.constants.*
import io.carrotquest.sample.utils.SharedPreferencesUtil
import io.carrotquest_sdk.android.Dashly
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
        if(Dashly.isInit()) {
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
            Dashly.setUserProperty(UserProperty("\$name", name))
        }
    }

    private fun saveEmail(email: String, context: Context) {
        val oldEmail = SharedPreferencesUtil.getString(context, USER_EMAIl)
        if(oldEmail != email) {
            SharedPreferencesUtil.saveString(context, USER_EMAIl, email)
            Dashly.setUserProperty(UserProperty("\$email", email))
        }
    }

    private fun savePhone(phone: String, context: Context) {
        val oldPhone = SharedPreferencesUtil.getString(context, USER_PHONE)
        if(oldPhone != phone) {
            SharedPreferencesUtil.saveString(context, USER_PHONE, phone)
            Dashly.setUserProperty(UserProperty("\$phone", phone))
        }
    }

    private fun initSdk(context: Context) {
        val apiKey = SharedPreferencesUtil.getString(context, API_KEY_SP).ifEmpty { API_KEY }
        val userAuthKey = SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP).ifEmpty { USER_AUTH_KEY }

        if(apiKey.isNotEmpty() && userAuthKey.isNotEmpty()) {
            Dashly.setup(context, apiKey, object : CarrotSDK.Callback<Boolean>{
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
        val userAuthKey = SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP).ifEmpty { USER_AUTH_KEY }
        if(userAuthKey.isNotEmpty()) {
            var userId = SharedPreferencesUtil.getString(context, USER_ID)
            if (userId.isEmpty()) {
                userId = UUID.randomUUID().toString()
                SharedPreferencesUtil.saveString(context, USER_ID, userId)
            }
            Dashly.auth(userId, userAuthKey, object : CarrotSDK.Callback<String>{
                override fun onResponse(userId: String) {
                    if(userId.isNotEmpty()) {
                        view?.close()
                    }
                }

                override fun onFailure(p0: Throwable?) {

                }
            })
        }
    }
}