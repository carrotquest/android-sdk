package io.carrotquest.sample.cart.view

import io.carrotquest.sample.model.ProductEntity

interface ICartView {
    fun showProducts(products: ArrayList<ProductEntity>)
    fun showEmptyList()
    fun showError()
    fun showSuccess()
    fun close()
}