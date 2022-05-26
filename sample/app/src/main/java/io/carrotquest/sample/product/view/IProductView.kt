package io.carrotquest.sample.product.view

interface IProductView {
    fun updateProductName(name: String)
    fun updateProductDescription(description: String)
    fun updateProductImage(imageUri: String)
    fun updatePrice(price: String)
    fun showSuccessBuy()
    fun close()
}