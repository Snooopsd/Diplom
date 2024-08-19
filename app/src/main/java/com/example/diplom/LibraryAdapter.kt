package com.example.diplom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class LibraryAdapter (libraryArray: ArrayList<ListItem>, context: Context, private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
    private var listArray = libraryArray
    private var newContext = context

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        private val itemImage: ImageView = view.findViewById(R.id.itemImage)
        private val itemName: TextView = view.findViewById(R.id.itemName)
        private val itemFlora: TextView = view.findViewById(R.id.itemFlora)

        fun bind(listItem: ListItem, clickListener: OnItemClickListener) {
            if (listItem.image!!.isEmpty()) {
                itemImage.setImageResource(R.drawable.placeholder_image)
            } else {
                Picasso.get().load(listItem.image).into(itemImage)
            }
            itemName.text = listItem.sort
            itemFlora.text = listItem.flora

            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(newContext)

        return ViewHolder(inflater.inflate(R.layout.item_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = listArray[position]
        holder.bind(listItem, itemClickListener)
    }

    fun updateAdapter(newListArray: List<ListItem>) {
        listArray.clear()
        listArray.addAll(newListArray)
        notifyDataSetChanged()
    }

    fun getType(position: Int): String? {
        return listArray[position].flora
    }

    fun getItem(position: Int): ListItem? {
        return listArray[position]
    }
}