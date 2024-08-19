package com.example.diplom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.Database
import com.example.diplom.LibraryAdapter
import com.example.diplom.ListItem
import com.example.diplom.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LibraryFragment : Fragment(), LibraryAdapter.OnItemClickListener {
        private var param1: String? = null
    private var param2: String? = null

    private lateinit var library: RecyclerView
    private lateinit var sortButton: Button
    private lateinit var diseasesButton: Button
    private var adapter: LibraryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val db = Database(requireContext())
        db.fetchDataFromFirestoreArray("Sorts") {resultList ->
            adapter?.updateAdapter(resultList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sortButton = view.findViewById(R.id.ListSorts)
        sortButton.isEnabled = false
        diseasesButton = view.findViewById(R.id.ListDiseases)

        library = view.findViewById(R.id.library)
        val list = ArrayList<ListItem>()

        library.setHasFixedSize(true)
        library.layoutManager = LinearLayoutManager(requireContext())
        adapter = LibraryAdapter(list, requireContext(), this)
        library.adapter = adapter


        val db = Database(requireContext())
        sortButton.setOnClickListener {
            db.fetchDataFromFirestoreArray("Sorts") {resultList ->
                adapter?.updateAdapter(resultList)
            }
            sortButton.isEnabled = false
            diseasesButton.isEnabled = true
        }

        diseasesButton.setOnClickListener {
            db.fetchDataFromFirestoreArray("Diseases") {resultList ->
                adapter?.updateAdapter(resultList)
            }
            diseasesButton.isEnabled = false
            sortButton.isEnabled = true
        }
    }

    override fun onItemClick(position: Int) {
        if(adapter?.getType(position) == "Яблоня" || adapter?.getType(position) == "Груша") {
            val bottomSheetFragment = InformationBottomSheetDialogFragment.newInstance(R.layout.sort_bottom_sheet_layout)
            adapter?.getItem(position)?.let { bottomSheetFragment.setData(it, false) }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        } else {
            val bottomSheetFragment = InformationBottomSheetDialogFragment.newInstance(R.layout.diseases_bottom_sheet_layout)
            adapter?.getItem(position)?.let { bottomSheetFragment.setData(it, false) }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}