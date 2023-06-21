package com.example.partyfinder.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.partyfinder.R
import com.example.partyfinder.ui.activities.DashboardActivity
import com.example.partyfinder.ui.activities.SettingsActivity
import com.example.partyfinder.utils.CustomButton

@Suppress("DEPRECATION")
class NewPartyFragment : Fragment() {

    private lateinit var parentActivity: DashboardActivity

    private lateinit var btnDatePicker: CustomButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_new_party, container, false)

        btnDatePicker = root.findViewById(R.id.btn_pick_date)
        btnDatePicker.setOnClickListener { showDatePickerDialog(it) }

        parentActivity = activity as DashboardActivity

        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_party_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {

            R.id.action_next -> {

                Toast.makeText(
                    activity,
                    "Next",
                    Toast.LENGTH_SHORT
                ).show()
                // startActivity(Intent(activity, LocationPickerActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        parentActivity.tvTitle.text = resources.getString(R.string.title_new_party)
    }

    private fun showDatePickerDialog(v: View) {

        val newFragment = DatePickerFragment()
        newFragment.show(parentActivity.supportFragmentManager, "datePicker")
    }
}