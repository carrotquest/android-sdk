package io.carrotquest.sample.product.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.carrotquest.sample.R
import io.carrotquest.sample.model.ProductEntity
import io.carrotquest.sample.main.view.MainActivity
import io.carrotquest.sample.product.presenter.ProductPresenter
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity: AppCompatActivity(), IProductView {

    private val presenter = ProductPresenter(this)
    private lateinit var product: ProductEntity

    companion object {
        const val PRODUCT_ARG = "product_arg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        product = intent.getParcelableExtra<ProductEntity>(PRODUCT_ARG)
        presenter.onStart(product)

        buy_button.setOnClickListener {
            presenter.onClickBuyButton(product)
        }
    }


    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun updateProductName(name: String) {
        product_n_tv.text = name
    }

    override fun updateProductDescription(description: String) {
        product_d_tv.text = description
    }

    override fun updateProductImage(imageUri: String) {
        Glide
            .with(this)
            .load(imageUri)
            .into(product_iv as ImageView)
    }

    override fun updatePrice(price: String) {
        product_price_in_card_tv.text = price
    }

    override fun showSuccessBuy() {
        Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
    }

    override fun close() {
        val intent = Intent()
        intent.putExtra(MainActivity.PR_IN_CART, product)
        setResult(RESULT_OK, intent)
        finish()
    }
}