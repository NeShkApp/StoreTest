package org.bohdan.storetest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import org.bohdan.storetest.R
import org.bohdan.storetest.models.Banner

class BannerAdapter(private var banners: List<Banner>,private val viewPager2: ViewPager2):
    RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val runnable = Runnable {
        viewPager2.currentItem = banners.size / 2
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val imageView: ImageView = itemView.findViewById(R.id.imageSlider)
        fun setImage(banners: Banner, context: Context) {
            val requestOptions = RequestOptions().transform(CenterInside())
            Glide.with(context)
                .load(banners.url)
                .apply(requestOptions)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerAdapter.ViewHolder {
        context = parent.context
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.slider_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerAdapter.ViewHolder, position: Int) {
        holder.setImage(banners[position], context)

        if(position == banners.lastIndex - 1){
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return banners.size
    }

}