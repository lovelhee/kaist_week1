package com.example.madcampweek1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcampweek1.GalleryDataBase.GalleryImageDao
import android.widget.Toast
import kotlinx.coroutines.launch


// 데이터 클래스
data class RecyclerViewItem(val imageUri: Uri, val text: String)

// 어댑터 클래스
class RecyclerViewAdapter(
    private val items: MutableList<RecyclerViewItem>,
    private val galleryImageDao: GalleryImageDao,
    private val lifecycleScope: LifecycleCoroutineScope
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

        // 클릭 리스너
        holder.imageView.setOnClickListener {
            showOptionDialog(holder.itemView.context, item, position)
        }
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = items.size

    // 아이템 추가 함수
    fun addItem(item: RecyclerViewItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    // 다이얼로그
    private fun showOptionDialog(context: Context, item: RecyclerViewItem, position: Int) {
        val options = arrayOf("사진 확대", "사진 삭제")
        AlertDialog.Builder(context).apply {
            setTitle("옵션 선택")
            setItems(options) { _, which ->
                when (which) {
                    0 -> showImageFullscreen(context, item.imageUri)
                    1 -> deleteItem(context, item, position)
                }
            }
            show()
        }
    }

    // 사진 전체 화면으로 띄우기
    private fun showImageFullscreen(context: Context, uri: Uri) {
        val intent = Intent(context, FullscreenImageActivity::class.java).apply {
            putExtra("imageUri", uri.toString())
        }
        context.startActivity(intent)
    }

    // 사진 삭제 (DB에서도 삭제)
    private fun deleteItem(context: Context, item: RecyclerViewItem, position: Int) {
        AlertDialog.Builder(context).apply {
            setTitle("삭제 확인")
            setMessage("정말로 삭제하시겠습니까?")
            setPositiveButton("예") { _, _ ->
                lifecycleScope.launch {
                    galleryImageDao.deleteByUri(item.imageUri.toString())
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("아니오", null)
            show()
        }
    }
}
