package io.carrotquest.sample.input_data.presenter

import android.content.Context
import io.carrotquest.sample.constants.API_KEY_SP
import io.carrotquest.sample.constants.APP_ID_SP
import io.carrotquest.sample.constants.USER_AUTH_KEY_SP
import io.carrotquest.sample.input_data.view.IInputDataView
import io.carrotquest.sample.utils.SharedPreferencesUtil
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.core.main.CarrotSDK

class InputDataPresenter(var view: IInputDataView?) {


    fun detachView() {
        this.view = null
    }

    fun onTryConnect(context: Context, appId: String, apiKey: String, userAuthKey: String) {
        view?.showProgress()
        if (appId.isNotEmpty() && apiKey.isNotEmpty() && userAuthKey.isNotEmpty()) {
            Carrot.setup(context, apiKey, appId, object : CarrotSDK.Callback<Boolean> {
                override fun onFailure(p0: Throwable?) {
                    view?.showConnectError()
                    view?.hideProgress()
                }

                override fun onResponse(resConnect: Boolean) {
                    if (resConnect) {
                        saveData(context, appId, apiKey, userAuthKey)
                        view?.openMainActivity()
                    } else {
                        view?.showConnectError()
                    }
                    view?.hideProgress()
                }
            })
        } else {
            view?.showFieldsIsEmptyError()
            view?.hideProgress()
        }
    }

    private fun saveData(context: Context, appId: String, apiKey: String, userAuthKey: String) {
        SharedPreferencesUtil.saveString(context, APP_ID_SP, appId)
        SharedPreferencesUtil.saveString(context, API_KEY_SP, apiKey)
        SharedPreferencesUtil.saveString(context, USER_AUTH_KEY_SP, userAuthKey)
    }

    fun onBack() {
        view?.close()
    }
}