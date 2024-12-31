package com.example.madcampweek1.help

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class HelpFragment : Fragment() {

    private lateinit var helpAdapter: HelpAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_help, container, false)

        val rvPeople: RecyclerView = view.findViewById(R.id.rvPeople)
        rvPeople.layoutManager = LinearLayoutManager(context)

        val editText: EditText = view.findViewById(R.id.etSearch)
        val bottomNavigationView: View? = activity?.findViewById(R.id.bottomNavigationView)

        val json = loadJSONFromRaw(R.raw.help)
        val type = object : TypeToken<List<Help>>() {}.type
        val helpList: List<Help> = Gson().fromJson(json, type)

        helpAdapter = HelpAdapter(helpList)
        rvPeople.adapter = helpAdapter

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                helpAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // 키보드가 나타났을 때
                bottomNavigationView?.visibility = View.GONE
            } else {
                // 키보드가 사라졌을 때
                bottomNavigationView?.visibility = View.VISIBLE
            }
        }

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