package com.example.madcampweek1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class ContactsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvContacts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val contacts = loadContactsFromJson()

        val adapter = ContactsAdapter(contacts)
        recyclerView.adapter = adapter

        return view

    }

    private fun loadContactsFromJson(): List<Contact> {
        val inputStream = resources.openRawResource(R.raw.contacts)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Contact>>() {}.type
        return Gson().fromJson(reader, type)
    }

}