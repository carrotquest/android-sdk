package io.carrotquest.sample.auth.presenter

import android.content.Context
import io.carrotquest.sample.auth.view.IAuthView
import io.carrotquest.sample.constants.USER_EMAIl
import io.carrotquest.sample.constants.USER_NAME
import io.carrotquest.sample.constants.USER_PHONE
import io.carrotquest.sample.utils.SharedPreferencesUtil
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.models.UserProperty

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
        saveName(name, context)
        saveEmail(email, context)
        savePhone(phone, context)

        view?.close()
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
}