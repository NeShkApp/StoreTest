package org.bohdan.storetest.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bohdan.storetest.DetailsActivity
import org.bohdan.storetest.models.CategoryModel
import org.bohdan.storetest.databinding.ItemCategoryBinding

class CategoryAdapter(var items: List<CategoryModel>):
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: ItemCategoryBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleCat.text = item.title

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.binding.imageCat)

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java).apply {
                putExtra("id", item.id)
                putExtra("title", item.title)
                putExtra("picUrl", item.picUrl)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    fun updateItems(newItems: List<CategoryModel>){
        items = newItems
        notifyDataSetChanged()
    }

}