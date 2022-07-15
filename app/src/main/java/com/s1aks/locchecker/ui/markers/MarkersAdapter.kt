package com.s1aks.locchecker.ui.markers

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.s1aks.locchecker.R
import com.s1aks.locchecker.databinding.MarkerItemBinding
import com.s1aks.locchecker.domain.entities.MapPosition

class MarkersAdapter(
    private val menuInflater: MenuInflater,
    private val itemClickListener: OnItemClickListener
) :
    ListAdapter<MapPosition, MarkersAdapter.MarkersViewHolder>(DiffCallback) {

    var clickedMarkerId = -1
    var itemPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MarkersViewHolder(
        MarkerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MarkersViewHolder, position: Int) {
        holder.bind(position, itemClickListener)
    }

    fun updateItem(marker: MapPosition) {
        val currentListMutable = currentList.toMutableList()
        currentListMutable[itemPosition] = marker
        submitList(currentListMutable)
        notifyItemChanged(itemPosition)
    }

    fun removeItem() {
        val currentListMutable = currentList.toMutableList()
        currentListMutable.removeAt(itemPosition)
        submitList(currentListMutable)
    }


    inner class MarkersViewHolder(private val binding: MarkerItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {
        init {
            binding.root.setOnCreateContextMenuListener(this)
        }

        fun bind(position: Int, clickListener: OnItemClickListener) = with(binding) {
            title.text = currentList[position].title
            information.text = currentList[position].information
            latitude.text = currentList[position].latitude.toString()
            longitude.text = currentList[position].longitude.toString()
            itemView.setOnClickListener { clickListener.onItemClicked(currentList[position].id) }
            itemView.setOnLongClickListener {
                itemPosition = adapterPosition
                clickedMarkerId = currentList[adapterPosition].id
                false
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menuInflater.inflate(R.menu.marker_items_context_menu, menu)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MapPosition>() {
        override fun areItemsTheSame(oldItem: MapPosition, newItem: MapPosition) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: MapPosition, newItem: MapPosition) =
            oldItem == newItem
    }
}

interface OnItemClickListener {
    fun onItemClicked(itemId: Int)
}