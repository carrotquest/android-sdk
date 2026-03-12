package io.carrotquest.sample.cart.view.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.carrotquest.sample.databinding.ProductInCartViewHolderBinding
import io.carrotquest.sample.model.MainCartModel
import io.carrotquest.sample.model.ProductEntity
import io.carrotquest_sdk.android.Dashly

class ProductsInCartAdapter(private val activity: AppCompatActivity): RecyclerView.Adapter<ProductsInCartAdapter.ProductInCartViewHolder>() {

    private val items = ArrayList<ProductEntity>()

    fun setData(data: ArrayList<ProductEntity>) {
        this.items.clear()
        this.items.addAll(data)
        notifyDataSetChanged()
    }

    fun removeProduct(product: ProductEntity) {
        val index = this.items.indexOf(product)
        if (index != -1) {
            this.items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductInCartViewHolder {
        val binding = ProductInCartViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductInCartViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductInCartViewHolder, position: Int) {
        if(items.size > position) {
            holder.bind(items[position])
        }
    }

    inner class ProductInCartViewHolder(private val binding: ProductInCartViewHolderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductEntity) {
            binding.productInCartNameTv.text = product.name
            Glide
                .with(binding.root)
                .load(product.imageUri)
                .into(binding.productInCartIv)

            binding.deleteProductBtn.setOnClickListener {
                Dashly.trackEvent("Товар был удален из корзины", "{\"Название\":\"${product.name}\"}")
                MainCartModel.getInstance().removeProduct(product)
            }
        }
    }
}
