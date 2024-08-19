package com.example.diplom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.BadTokenException
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.HistoryAdapter
import com.example.diplom.JsonHandler
import com.example.diplom.ListItem
import com.example.diplom.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HistoryFragment : Fragment(), HistoryAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var history: RecyclerView
    private lateinit var deleteBtn: ImageButton
    private var adapter: HistoryAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        adapter?.updateAdapter(JsonHandler.loadData(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deleteBtn = view.findViewById(R.id.DeleteHistory)
        deleteBtn.setOnClickListener {
            JsonHandler.clearData(requireContext())
            adapter?.updateAdapter(emptyList())
            view.findViewById<TextView>(R.id.AboutHistoty)?.text = "В истории пока ничего нет"
        }

        if (JsonHandler.loadData(requireContext()).isEmpty()) {
            view.findViewById<TextView>(R.id.AboutHistoty)?.text = "В истории пока ничего нет"
        } else {
            view.findViewById<TextView>(R.id.AboutHistoty)?.text = ""
        }

        history = view.findViewById(R.id.History)
        val list = ArrayList<ListItem>()
        list.addAll(JsonHandler.loadData(requireContext()))

        history.setHasFixedSize(true)
        history.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(list, requireContext(), this)
        history.adapter = adapter
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
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}