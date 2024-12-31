package com.example.madcampweek1.help

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_help, container, false)

        val rvPeople: RecyclerView = view.findViewById(R.id.rvPeople)
        rvPeople.layoutManager = LinearLayoutManager(context)

        val json = loadJSONFromRaw(R.raw.help)
        val type = object : TypeToken<List<Help>>() {}.type
        val helpList: List<Help> = Gson().fromJson(json, type)

        rvPeople.adapter = HelpAdapter(helpList)

        return view
    }

    private fun loadJSONFromRaw(resourceId: Int): String {
        return try {
            val inputStream = resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            val jsonString = reader.use { it.readText() }
            jsonString
        } catch (ex: Exception) {
            Log.e("HelpFragment", "Error reading JSON file", ex)
            ""
        }
    }
}