package com.example.partyfinder.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.Party
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

        parentActivity.showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getParties(parentActivity, this)

        return root
    }

    override fun onResume() {
        super.onResume()

        parentActivity.tvTitle.text = resources.getString(R.string.title_home_fragment)
    }

    fun successPartiesList(partyList: ArrayList<Party>) {

        parentActivity.hideProgressDialog()

        if (partyList.size > 0) {

            Toast.makeText(
                requireActivity(),
                partyList.size.toString() + " parties found.",
                Toast.LENGTH_SHORT
            ).show()

            // TODO Step 8: Pass the list to the adapter class.
            // START
            // mPartiesList = partyList

            // TODO Step 9: Notify the adapter class for any data change.
            // START
            // mAdapter.notifyDataSetChanged()
            // END
            // END
        } else {

            Toast.makeText(
                requireActivity(),
                "No parties found.",
                Toast.LENGTH_SHORT
            ).show()

            // TODO Step 10: Show the text view if the party list is empty.
            // START
            // tv_no_parties_found.visibility = View.VISIBLE
            // END
        }
    }
}