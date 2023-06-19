package com.example.partyfinder.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.partyfinder.R
import com.example.partyfinder.ui.activities.DashboardActivity

class HomeFragment : Fragment() {

    private lateinit var parentActivity: DashboardActivity

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)

        textView.text = "Home fragment"

        parentActivity = activity as DashboardActivity

        return root
    }

    override fun onResume() {
        super.onResume()

        parentActivity.tvTitle.text = resources.getString(R.string.title_home_fragment)
    }
}