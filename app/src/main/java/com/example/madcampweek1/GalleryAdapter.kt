package com.example.madcampweek1

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// 데이터 클래스
data class RecyclerViewItem(val imageUri: Uri, val text: String)

// 어댑터 클래스
class RecyclerViewAdapter(
    private val items: MutableList<RecyclerViewItem>
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    // 뷰 홀더 클래스
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val textView: TextView = itemView.findViewById(R.id.itemTextView)
    }

    // 아이템 뷰 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery, parent, false)
        return ViewHolder(view)
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView.context)
            .load(item.imageUri)
            .into(holder.imageView)
        holder.textView.text = item.text
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = items.size

    // 아이템 추가 함수
    fun addItem(item: RecyclerViewItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}
