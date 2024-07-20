package org.bohdan.storetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private var groceries: List<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groceries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val grocery = groceries[position]
        holder.productName.text = grocery.name
        holder.productPrice.text = grocery.price.toString()
        Glide
            .with(holder.itemView.context)
            .load(grocery.imageUrl)
            .into(holder.productImage)
    }

    fun updateProducts(newProducts: List<Product>) {
        groceries = newProducts
        notifyDataSetChanged()
    }

}