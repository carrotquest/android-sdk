package io.carrotquest.sample.main.view.rv

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.carrotquest.sample.databinding.ProductViewHolderBinding
import io.carrotquest.sample.model.ProductEntity
import io.carrotquest.sample.main.view.MainActivity
import io.carrotquest.sample.product.view.ProductActivity
import io.carrotquest_sdk.android.Dashly

class ProductsAdapter(private val activity: AppCompatActivity): RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    private val items = ArrayList<ProductEntity>()

    fun setData(data: ArrayList<ProductEntity>) {
        this.items.clear()
        this.items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, activity)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        if(items.size > position) {
            holder.bind(items[position])
        }
    }

    inner class ProductViewHolder(private val binding: ProductViewHolderBinding, private val activity: AppCompatActivity): RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var product: ProductEntity? = null
        fun bind(product: ProductEntity) {
            this.product = product
            binding.root.setOnClickListener(this)
            binding.productNameTv.text = product.name
            binding.productPriceTv.text = "\u20BD %.2f".format(product.price)
            Glide
                .with(binding.root)
                .load(product.imageUri)
                .into(binding.productImageIv)
        }

        override fun onClick(v: View?) {
            Dashly.trackEvent("Переход на карточку товара", "{\"Название\":\"${this.product?.name}\"}")
            val intent = Intent(activity, ProductActivity::class.java)
            intent.putExtra(ProductActivity.PRODUCT_ARG, product)
            activity.startActivityForResult(intent, MainActivity.REQ_CODE)
        }
    }
}
