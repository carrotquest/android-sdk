package io.carrotquest.sample.main.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.navigation.NavigationView
import io.carrotquest.sample.R
import io.carrotquest.sample.auth.view.AuthDialog
import io.carrotquest.sample.constants.USER_ID
import io.carrotquest.sample.databinding.ActivityMainBinding
import io.carrotquest.sample.databinding.NavHeaderMainBinding
import io.carrotquest.sample.main.presenter.MainPresenter
import io.carrotquest.sample.main.view.rv.ProductsAdapter
import io.carrotquest.sample.model.MainCartModel
import io.carrotquest.sample.model.ProductEntity
import io.carrotquest.sample.utils.SharedPreferencesUtil
import java.util.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, IMainView {

    private lateinit var binding: ActivityMainBinding
    private val presenter = MainPresenter(this)
    private val adapter = ProductsAdapter(this)

    private lateinit var countProductsInCart: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setNavigationItemSelectedListener(this)

        val layoutManager = GridLayoutManager(this, 2)
        binding.productsRv.layoutManager = layoutManager
        binding.productsRv.adapter = adapter

        setSupportActionBar(binding.mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        MainCartModel.getInstance().addAddProductObserver { _, arg ->
            run {
                if (arg is ProductEntity) {
                    incrementProductsCountInCart()
                }
            }
        }

        MainCartModel.getInstance().addRemoveProductObserver { _, arg ->
            run {
                if (arg is ProductEntity) {
                    decrementProductsCountInCart()
                }
            }
        }

        val userAuthKey = intent.getStringExtra(USER_AUTH_KEY_ARG)
        var userId = SharedPreferencesUtil.getString(this, USER_ID)
        if (userId.isEmpty()) {
            userId = UUID.randomUUID().toString()
            SharedPreferencesUtil.saveString(this, USER_ID, userId)
        }
        presenter.onCreate(userAuthKey, userId)

        binding.openNavViewBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                presenter.drawerOpened(this@MainActivity)
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

        binding.productsRv.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                presenter.onScrolled(dy)
            }
        })

        val headerBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        headerBinding.imageProfileView.setOnClickListener {
            presenter.onTapProfile(this)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    hideNavigationDrawer()
                } else {
                    presenter.onBack()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item: MenuItem = menu!!.findItem(R.id.cart)
        item.setActionView(R.layout.actionbar_badge_layout)
        val view = item.actionView as FrameLayout
        countProductsInCart = view.findViewById<View>(R.id.cart_badge) as TextView

        view.setOnClickListener {
            presenter.onTapCart(this)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            val product = data.getParcelableExtra<ProductEntity>(PR_IN_CART)
            MainCartModel.getInstance().addProduct(product)
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showProducts(products: ArrayList<ProductEntity>) {
        adapter.setData(products)
    }

    override fun hideNavigationDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun close() {
        presenter.detachView()
        finishAffinity()
        exitProcess(0)
    }

    override fun showAuthError() {
        Toast.makeText(this, "Произошла ошибка при авторизации пользователя", Toast.LENGTH_SHORT)
            .show()
    }

    override fun showEmptyCartError() {
        Toast.makeText(this, "В корзине нет товаров", Toast.LENGTH_SHORT).show()
    }

    companion object Constants {
        const val USER_AUTH_KEY_ARG = "user_auth_key_arg"
        const val REQ_CODE = 123
        const val PR_IN_CART = "product_in_cart"
    }

    private fun incrementProductsCountInCart() {
        val oldValue = try {
            countProductsInCart.text.toString().toInt()
        } catch (t: Throwable) {
            0
        }
        val nValue = oldValue + 1

        countProductsInCart.text = (nValue).toString()
        countProductsInCart.visibility = if (nValue <= 0) View.GONE else View.VISIBLE
    }

    private fun decrementProductsCountInCart() {
        val oldValue = try {
            countProductsInCart.text.toString().toInt()
        } catch (t: Throwable) {
            0
        }
        val nValue = if (oldValue > 0) oldValue - 1 else 0

        countProductsInCart.text = (nValue).toString()
        countProductsInCart.visibility = if (nValue <= 0) View.GONE else View.VISIBLE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.open_support -> presenter.openChat(this)
            R.id.auth -> presenter.openAuth()
            R.id.logout -> presenter.onLogout(this)
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun hideFab() {
        binding.cqFab.hide()
    }

    override fun showFab() {
        binding.cqFab.show()
    }

    override fun updateSupportItemTitle(title: String) {
        val menuSupportItem = binding.navView.menu.findItem(R.id.open_support)
        if (menuSupportItem != null) {
            menuSupportItem.title = title
        }
    }


    override fun openAuthDialog() {
        val authDialog = AuthDialog()
        authDialog.show(supportFragmentManager, "auth_dialog")
    }
}
