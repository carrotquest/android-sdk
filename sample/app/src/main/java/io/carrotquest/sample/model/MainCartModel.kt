package io.carrotquest.sample.model

import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.models.UserProperty
import java.util.*
import kotlin.collections.ArrayList

class MainCartModel private constructor() {

    companion object {
        private var instance: MainCartModel? = null

        fun getInstance(): MainCartModel {
            if(instance == null) {
                instance = MainCartModel()
            }
            return instance!!
        }
    }

    private val products = ArrayList<ProductEntity>()
    private val addProductObservable: CartObservable = CartObservable()
    private val removeProductObservable: CartObservable = CartObservable()

    fun addProduct(product: ProductEntity) {
        products.add(product)
        addProductObservable.notifyObservers(product)

        Carrot.setUserProperty(UserProperty(UserProperty.Operation.UNION,"\$cart_items", product.name))
        Carrot.setUserProperty(UserProperty(UserProperty.Operation.ADD,"\$cart_amount", product.price.toInt().toString()))
    }

    fun addAddProductObserver(observer: Observer) {
        addProductObservable.addObserver(observer)
    }

    fun removeProduct(product: ProductEntity) {
        products.remove(product)
        removeProductObservable.notifyObservers(product)

        Carrot.setUserProperty(UserProperty(UserProperty.Operation.EXCLUDE,"\$cart_items", product.name))
        Carrot.setUserProperty(UserProperty(UserProperty.Operation.ADD,"\$cart_amount", (-product.price.toInt()).toString()))
    }

    fun addRemoveProductObserver(observer: Observer) {
        removeProductObservable.addObserver(observer)
    }

    fun getProducts() = products
    fun removeAll() {
        for(x in products) {
            removeProductObservable.notifyObservers(x)
        }
        products.clear()
    }

    private fun getCommonPrice() = products.sumByDouble { x->x.price.toDouble() }.toFloat()
}