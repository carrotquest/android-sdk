package io.carrotquest.sample.main.view

import io.carrotquest.sample.model.ProductEntity

interface IMainView {
    fun showAuthError()
    fun close()
    fun showProducts(products: ArrayList<ProductEntity>)
    fun showEmptyCartError()

    fun showFab()
    fun hideFab()

    fun openAuthDialog()

    fun hideNavigationDrawer()
}