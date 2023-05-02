package com.smarternote.core.ui.banner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.smarternote.core.R
import com.smarternote.core.databinding.CoreItemBannerBinding

class BannerAdapter(
    private var imageList: List<String> = emptyList(),
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<BannerAdapter.VH>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(imageList: List<String>) {
        this.imageList = imageList
        notifyDataSetChanged()
    }

    inner class VH(val binding: CoreItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<CoreItemBannerBinding>(inflater, R.layout.core_item_banner, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val realPosition = position % imageList.size
        if (imageList.size > realPosition) {
            val imageUrl = imageList[realPosition]
            holder.binding.imageUrl = imageUrl
            holder.binding.imageView.setOnClickListener {
                onItemClickListener(position % imageList.size)
            }
            holder.binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}
