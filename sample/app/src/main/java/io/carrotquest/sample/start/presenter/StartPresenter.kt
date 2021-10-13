package io.carrotquest.sample.start.presenter

import android.content.Context
import io.carrotquest.sample.R
import io.carrotquest.sample.constants.*
import io.carrotquest.sample.start.view.IStartView
import io.carrotquest.sample.utils.SharedPreferencesUtil
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.core.main.CarrotSDK

class StartPresenter(private var view: IStartView?) {

    fun onStart(context: Context) {
        val apiKey = SharedPreferencesUtil.getString(context, API_KEY_SP)
        val appId = SharedPreferencesUtil.getString(context, APP_ID_SP)
        val userAuthKey = SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP)

        if (!apiKey.isNullOrEmpty() && !appId.isNullOrEmpty() && !userAuthKey.isNullOrEmpty()) {
            Carrot.setup(context, apiKey, appId, object : CarrotSDK.Callback<Boolean> {
                override fun onFailure(p0: Throwable?) {
                    view?.showConnectError()
                    view?.openSetApiKey()
                }

                override fun onResponse(resConnect: Boolean) {
                    if (resConnect) {
                        Carrot.setNotificationIcon(R.drawable.ic_notificatrion_icon)
                        view?.openMainActivity()
                    } else {
                        view?.showConnectError()
                    }
                }
            })
        } else if (API_KEY.isNotEmpty() && APP_ID.isNotEmpty() && USER_AUTH_KEY.isNotEmpty()) {
            Carrot.setup(context, API_KEY, APP_ID, object : CarrotSDK.Callback<Boolean> {
                override fun onFailure(p0: Throwable?) {
                    view?.showConnectError()
                }

                override fun onResponse(resConnect: Boolean) {
                    if (resConnect) {
                        Carrot.setNotificationIcon(R.drawable.ic_notificatrion_icon)
                        view?.openMainActivity()
                    } else {
                        view?.showConnectError()
                    }
                }
            })
        } else {
            view?.openSetApiKey()
        }
    }

    fun detachView() {
        this.view = null
    }
}