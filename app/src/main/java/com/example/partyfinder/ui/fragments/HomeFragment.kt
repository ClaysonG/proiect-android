package com.example.partyfinder.ui.fragments

import PartyAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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

    private lateinit var searchView: SearchView
    private var partyList: ArrayList<Party> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        parentActivity = activity as DashboardActivity

        recyclerView = root.findViewById(R.id.rv_parties)
        searchView = root.findViewById(R.id.sv_parties)

        parentActivity.showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getParties(parentActivity, this)

        return root
    }

    override fun onResume() {
        super.onResume()

        parentActivity.tvTitle.text = resources.getString(R.string.title_home_fragment)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun successPartiesList(partyList: ArrayList<Party>) {

        parentActivity.hideProgressDialog()

        this.partyList = partyList

        if (partyList.size > 0) {

//            Toast.makeText(
//                requireActivity(),
//                partyList.size.toString() + " parties found.",
//                Toast.LENGTH_SHORT
//            ).show()

            adapter = PartyAdapter(partyList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // adapter.notifyDataSetChanged()

            setupSearchView()
        } else {

            Toast.makeText(
                requireActivity(),
                "No parties found.",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun setupSearchView() {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText != null) {

                    val filteredList = filterPartyList(newText)
                    adapter.filterList(filteredList)
                }

                return true
            }
        })
    }

    private fun filterPartyList(query: String): ArrayList<Party> {

        val filteredList = ArrayList<Party>()
        for (party in partyList) {

            if (party.name.contains(query, ignoreCase = true)) {

                filteredList.add(party)
            }
        }

//        Toast.makeText(
//            requireActivity(),
//            filteredList.size.toString() + " parties found.",
//            Toast.LENGTH_SHORT
//        ).show()

        return filteredList
    }
}