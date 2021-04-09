package io.carrotquest.sample.cart.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import io.carrotquest.sample.R
import io.carrotquest.sample.cart.presenter.CartPresenter
import io.carrotquest.sample.cart.view.rv.ProductsInCartAdapter
import io.carrotquest.sample.model.MainCartModel
import io.carrotquest.sample.model.ProductEntity
import kotlinx.android.synthetic.main.activity_cart.*
import java.util.Observer

class CartActivity: AppCompatActivity(), ICartView {

    private val adapter = ProductsInCartAdapter(this)
    private val presenter = CartPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar = findViewById<Toolbar>(R.id.cart_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val layoutManager = LinearLayoutManager(this)
        products_in_cart_rv.layoutManager = layoutManager
        products_in_cart_rv.adapter = adapter

        presenter.onCreate()

        MainCartModel.getInstance().addRemoveProductObserver(Observer { _, arg ->
            run {
                if (arg is ProductEntity) {
                    adapter.removeProduct(arg)
                    if (MainCartModel.getInstance().getProducts().isEmpty()) {
                        close()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        val item: MenuItem = menu!!.findItem(R.id.buy)
        item.setOnMenuItemClickListener {
            presenter.onTabBuy()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun showProducts(products: ArrayList<ProductEntity>) {
        adapter.setData(products)
    }

    override fun showEmptyList() {

    }

    override fun showError() {

    }

    override fun showSuccess() {
        Toast.makeText(this, "Товары приобретены!", Toast.LENGTH_SHORT).show()
    }

    override fun close() {
        finish()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}