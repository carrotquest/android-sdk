package io.carrotquest.sample.main.view.rv

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.carrotquest.sample.R
import io.carrotquest.sample.model.ProductEntity
import io.carrotquest.sample.main.view.MainActivity
import io.carrotquest.sample.product.view.ProductActivity
import io.carrotquest_sdk.android.Carrot
import kotlinx.android.synthetic.main.product_view_holder.view.*

class ProductsAdapter(private val activity: AppCompatActivity): RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    private val items = ArrayList<ProductEntity>()

    fun setData(data: ArrayList<ProductEntity>) {
        this.items.clear()
        this.items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_view_holder, parent, false)
        return ProductViewHolder(v, activity)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        if(items.size > position) {
            holder.bind(items[position])
        }
    }

    inner class ProductViewHolder(private val v: View, private val activity: AppCompatActivity): RecyclerView.ViewHolder(v), View.OnClickListener {
        private var product: ProductEntity? = null
        fun bind(product: ProductEntity) {
            this.product = product
            v.setOnClickListener(this)
            v.product_name_tv.text = product.name
            v.product_price_tv.text = "\u20BD %.2f".format(product.price)
            Glide
                .with(v)
                .load(product.imageUri)
                .into(v.product_image_iv as ImageView)
        }

        override fun onClick(v: View?) {
            Carrot.trackEvent("Переход на карточку товара", "{\"Название\":\"${this.product?.name}\"}")
            val intent = Intent(activity, ProductActivity::class.java)
            intent.putExtra(ProductActivity.PRODUCT_ARG, product)
            activity.startActivityForResult(intent, MainActivity.REQ_CODE)

        }
    }
}