package com.example.diplom

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class HistoryAdapter (historyArray: ArrayList<ListItem>, context: Context, private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<HistoryAdapter.ViewHolder>()  {
    private var listArray = historyArray
    private var newContext = context

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        private val itemImage: ImageView = view.findViewById(R.id.historyImage)
        private val itemName: TextView = view.findViewById(R.id.historyName)
        private val itemFlora: TextView = view.findViewById(R.id.historyType)

        fun bind(listItem: ListItem, clickListener: OnItemClickListener) {
            if (listItem.image!!.isEmpty()) {
                itemImage.setImageResource(R.drawable.placeholder_image)
            } else {
                loadImageFromFile(itemImage, listItem.image.toString())
            }
            itemName.text = listItem.sort
            itemFlora.text = listItem.flora

            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }

        private fun loadImageFromFile(imageView: ImageView, imagePath: String) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imageView.setImageBitmap(bitmap)
            } else {
                Log.e("LoadImage", "File does not exist: $imagePath")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(newContext)

        return ViewHolder(inflater.inflate(R.layout.history_item_layout, parent, false))
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