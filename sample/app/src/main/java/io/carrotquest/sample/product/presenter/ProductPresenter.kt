package io.carrotquest.sample.product.presenter

import io.carrotquest.sample.model.ProductEntity
import io.carrotquest.sample.product.view.IProductView
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.models.UserProperty

class ProductPresenter(var view: IProductView?) {


    fun detachView() {
        this.view = null
    }

    fun onStart(product: ProductEntity?) {
        if(product != null) {
            view?.updateProductName(product.name)
            view?.updateProductDescription(product.description)
            view?.updateProductImage(product.imageUri)
            view?.updatePrice("\u20BD %.2f".format(product.price))

            Carrot.setUserProperty(UserProperty(UserProperty.Operation.UNION, "\$viewed_products", product.name))
        }
    }

    fun onClickBuyButton(product: ProductEntity?) {
        Carrot.trackEvent("Товар был добавлен в корзину", "{\"Название\":\"${product?.name}\"}")

        view?.showSuccessBuy()
        view?.close()
    }
}