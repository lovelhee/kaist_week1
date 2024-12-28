package com.example.madcampweek1.contactTab

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcampweek1.R

class ContactsAdapter(private val contactList: List<Contact>) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivContact: ImageView = itemView.findViewById(R.id.ivContacts)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacts, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.tvName.text = contact.name
        holder.tvNumber.text = contact.phoneNumber
        holder.tvCategory.text = contact.category

        Log.d("ContactAdapter", "Name: ${contact.name}, Category: ${contact.category}, ImageUrl: ${contact.imageUrl}")

        Glide.with(holder.itemView.context)
            .load(contact.imageUrl)
            //.placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
            //.error(R.drawable.error_image) // 에러 시 표시할 이미지
            .circleCrop()
            .into(holder.ivContact)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}