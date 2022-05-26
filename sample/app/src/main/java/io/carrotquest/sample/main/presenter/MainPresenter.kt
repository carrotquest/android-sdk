package io.carrotquest.sample.main.presenter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import io.carrotquest.sample.R
import io.carrotquest.sample.cart.view.CartActivity
import io.carrotquest.sample.constants.*
import io.carrotquest.sample.data.getDemoData
import io.carrotquest.sample.main.view.IMainView
import io.carrotquest.sample.model.MainCartModel
import io.carrotquest.sample.utils.SharedPreferencesUtil
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.core.main.CarrotSDK
import java.util.*

class MainPresenter(private var view: IMainView?) {

    fun onCreate(userAuthKey: String?, userId: String) {
        if (!userAuthKey.isNullOrEmpty()) {
            Carrot.auth(userId, userAuthKey, object : CarrotSDK.Callback<Boolean> {
                override fun onFailure(p0: Throwable?) {
                    view?.showAuthError()
                }

                override fun onResponse(resAuth: Boolean) {
                    if (!resAuth) {
                        view?.showAuthError()
                    }
                }

            })
        }

        val products = getDemoData()
        view?.showProducts(products)
    }

    fun detachView() {
        this.view = null
    }

    fun onBack() {
        view?.close()
    }

    fun onTapCart(context: Context) {
        val selectedProducts = MainCartModel.getInstance().getProducts()
        if (selectedProducts.size > 0) {
            Carrot.trackEvent(
                "Переход в корзину",
                "{\"Количество товаров\":\"${selectedProducts.size}\"}"
            )
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        } else {
            view?.showEmptyCartError()
            Carrot.trackEvent("Попытка перейти в пустую корзину")
        }
    }

    fun openChat(context: Context) {
        Carrot.openChat(context)
    }

    fun openAuth() {
        view?.openAuthDialog()
    }

    fun onScrolled(dy: Int) {
        if (dy > 0) {
            view?.hideFab()
        } else {
            view?.showFab()
        }
    }

    fun onTapProfile(context: Context) {
        view?.hideNavigationDrawer()
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Сбросить пользователя?")
            .setPositiveButton("Да") { _, _ ->
                run {

                    val userAuthKey =
                        SharedPreferencesUtil.getString(context, USER_AUTH_KEY_SP)
                    val newUserId = UUID.randomUUID().toString()
                    SharedPreferencesUtil.saveString(context, USER_ID, newUserId)
                    onCreate(userAuthKey, newUserId)

                    SharedPreferencesUtil.saveString(context, USER_NAME, "")
                    SharedPreferencesUtil.saveString(context, USER_EMAIl, "")
                    SharedPreferencesUtil.saveString(context, USER_PHONE, "")
                }
            }
            .setNegativeButton("Нет") { _, _ ->
                run {

                }
            }

        builder.show()
    }

    fun drawerOpened(context: Context) {
        val unreadConversationsCount = Carrot.getUnreadConversations().size
        val title = if (unreadConversationsCount > 0) {
            context.getString(R.string.open_chat_str) + " (${unreadConversationsCount})"
        } else {
            context.getString(R.string.open_chat_str)
        }

        view?.updateSupportItemTitle(title)
    }

    fun onLogout(context: Context) {
        Carrot.deInit()
        view?.updateSupportItemTitle(context.getString(R.string.open_chat_str) )
    }
}