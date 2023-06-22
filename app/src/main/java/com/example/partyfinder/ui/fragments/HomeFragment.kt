package com.example.partyfinder.ui.fragments

import PartyAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.Party
import com.example.partyfinder.ui.activities.DashboardActivity

class HomeFragment : Fragment() {

    private lateinit var parentActivity: DashboardActivity

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PartyAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        parentActivity = activity as DashboardActivity

        recyclerView = root.findViewById(R.id.rv_parties)

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

//            Toast.makeText(
//                requireActivity(),
//                partyList.size.toString() + " parties found.",
//                Toast.LENGTH_SHORT
//            ).show()

            adapter = PartyAdapter(partyList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            adapter.notifyDataSetChanged()
        } else {

            Toast.makeText(
                requireActivity(),
                "No parties found.",
                Toast.LENGTH_SHORT
            ).show()

        }
    }
}