package io.carrotquest.sample.cart.presenter

import io.carrotquest.sample.cart.view.ICartView
import io.carrotquest.sample.model.MainCartModel
import io.carrotquest_sdk.android.Dashly

class CartPresenter(private var view: ICartView?) {

    fun onCreate() {
        val products = MainCartModel.getInstance().getProducts()
        view?.showProducts(products)
    }

    fun detachView() {
        this.view = null
    }

    fun onTabBuy() {
        Dashly.trackEvent("Товары были куплены", "{\"Count\":\"${MainCartModel.getInstance().getProducts().size}\"}")
        view?.showSuccess()
        MainCartModel.getInstance().removeAll()
        view?.close()
    }
}