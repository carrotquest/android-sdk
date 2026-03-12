package io.carrotquest.sample.product.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.carrotquest.sample.databinding.ActivityProductBinding
import io.carrotquest.sample.model.ProductEntity
import io.carrotquest.sample.main.view.MainActivity
import io.carrotquest.sample.product.presenter.ProductPresenter

class ProductActivity: AppCompatActivity(), IProductView {

    private lateinit var binding: ActivityProductBinding
    private val presenter = ProductPresenter(this)
    private var product: ProductEntity? = null

    companion object {
        const val PRODUCT_ARG = "product_arg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        product = intent.getParcelableExtra<ProductEntity>(PRODUCT_ARG)
        presenter.onStart(product)

        binding.buyButton.setOnClickListener {
            presenter.onClickBuyButton(product)
        }
    }


    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun updateProductName(name: String) {
        binding.productNTv.text = name
    }

    override fun updateProductDescription(description: String) {
        binding.productDTv.text = description
    }

    override fun updateProductImage(imageUri: String) {
        Glide
            .with(this)
            .load(imageUri)
            .into(binding.productIv)
    }

    override fun updatePrice(price: String) {
        binding.productPriceInCardTv.text = price
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
