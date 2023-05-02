package com.smarternote.core.ui.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.smarternote.core.R

class BannerAdapter(
    private val imageList: List<Int>,
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.core_item_banner, parent, false)
        return BannerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.imageView.setImageResource(imageList[position % imageList.size])
        holder.imageView.setOnClickListener {
            onItemClickListener(position % imageList.size)
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}
