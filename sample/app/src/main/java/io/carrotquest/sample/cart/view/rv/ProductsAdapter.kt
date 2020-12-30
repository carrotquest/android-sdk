package io.carrotquest.sample.cart.view.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.carrotquest.sample.R
import io.carrotquest.sample.model.MainCartModel
import io.carrotquest.sample.model.ProductEntity
import io.carrotquest_sdk.android.Carrot
import kotlinx.android.synthetic.main.product_in_cart_view_holder.view.*

class ProductsInCartAdapter(private val activity: AppCompatActivity): RecyclerView.Adapter<ProductsInCartAdapter.ProductInCartViewHolder>() {

    private val items = ArrayList<ProductEntity>()

    fun setData(data: ArrayList<ProductEntity>) {
        this.items.clear()
        this.items.addAll(data)
        notifyDataSetChanged()
    }

    fun removeProduct(product: ProductEntity) {
        val index = this.items.indexOf(product)
        this.items.remove(product)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductInCartViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_in_cart_view_holder, parent, false)
        return ProductInCartViewHolder(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductInCartViewHolder, position: Int) {
        if(items.size > position) {
            holder.bind(items[position])
        }
    }

    inner class ProductInCartViewHolder(private val v: View): RecyclerView.ViewHolder(v) {
        private var product: ProductEntity? = null
        fun bind(product: ProductEntity) {
            this.product = product
            v.product_in_cart_name_tv.text = product.name
            Glide
                .with(v)
                .load(product.imageUri)
                .into(v.product_in_cart_iv as ImageView)

            v.delete_product_btn.setOnClickListener {
                Carrot.trackEvent(activity.getString(R.string.product_was_delete), "{\"${activity.getString(R.string.product_name)}\":\"${this.product?.name}\"}")
                MainCartModel.getInstance().removeProduct(product)
            }
        }

    }
}