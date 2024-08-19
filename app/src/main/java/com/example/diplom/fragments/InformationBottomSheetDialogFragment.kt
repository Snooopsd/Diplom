package com.example.diplom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.diplom.Database
import com.example.diplom.ListItem
import com.example.diplom.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class InformationBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var item: ListItem
    private var check: Boolean = false

    companion object {
        private const val ARG_LAYOUT = "layout"

        @JvmStatic
        fun newInstance(layout: Int) = InformationBottomSheetDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_LAYOUT, layout)
            }
        }
    }

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = arguments?.getInt(ARG_LAYOUT)
        return inflater.inflate(layout ?: R.layout.sort_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Database(requireContext())

        if (item.flora == "Яблоня" || item.flora == "Груша") {
            if (check) {
                val sort = view.findViewById<TextView>(R.id.Recognition_sort)
                sort?.let {
                    it.textSize = 25f
                    it.setPadding(20, 5, 20, 5)
                }
                sort.text = "Распознан сорт"
            } else {
                view.findViewById<TextView>(R.id.Recognition_sort)?.text = ""
            }
            view.findViewById<TextView>(R.id.Name_sort)?.text = item.sort
            view.findViewById<TextView>(R.id.Type_sort)?.text = item.flora
            lifecycleScope.launch {
                Picasso.get().load(db.fetchDataFromFirestore(item.sort.toString(), "Sorts", "Image")).into(view.findViewById<ImageView>(R.id.imageView2))
                view.findViewById<TextView>(R.id.Full_information_tree)?.text = db.fetchDataFromFirestore(item.sort.toString(), "Sorts", "Information_tree")
                view.findViewById<TextView>(R.id.Full_information_fruit)?.text = db.fetchDataFromFirestore(item.sort.toString(), "Sorts", "Information_fruit")
                view.findViewById<TextView>(R.id.Information_fruiting)?.text = db.fetchDataFromFirestore(item.sort.toString(), "Sorts", "Information_fruiting")
                view.findViewById<TextView>(R.id.Information_winter)?.text = db.fetchDataFromFirestore(item.sort.toString(), "Sorts", "Winter_Diseases")
                view.findViewById<TextView>(R.id.Information_advantages)?.text = db.fetchDataFromFirestore(item.sort.toString(), "Sorts", "Advantages")
                view.findViewById<TextView>(R.id.Information_disadvantages)?.text = db.fetchDataFromFirestore(item.sort.toString(), "Sorts", "Disadvantages")
            }
        } else {
            if (check) {
                val diseases = view.findViewById<TextView>(R.id.Recognition_disease)
                diseases?.let {
                    it.textSize = 25f
                    it.setPadding(20, 5, 20, 5)
                }
                diseases.text = "Распознана болезнь"
            } else {
                view.findViewById<TextView>(R.id.Recognition_disease)?.text = ""
            }
            view.findViewById<TextView>(R.id.Name_diseases)?.text = item.sort
            view.findViewById<TextView>(R.id.Type_diseases)?.text = item.flora
            lifecycleScope.launch {
                Picasso.get().load(db.fetchDataFromFirestore(item.sort.toString(), "Diseases", "Image"))
                    .into(view.findViewById<ImageView>(R.id.imageView3))
                view.findViewById<TextView>(R.id.Full_information)?.text =
                    db.fetchDataFromFirestore(item.sort.toString(), "Diseases", "Information")
                view.findViewById<TextView>(R.id.Information_symptoms)?.text =
                    db.fetchDataFromFirestore(item.sort.toString(), "Diseases", "Symptoms")
                view.findViewById<TextView>(R.id.Information_treatment)?.text =
                    db.fetchDataFromFirestore(item.sort.toString(), "Diseases", "Treatment")
            }
        }
    }

    fun setData(data: ListItem, type: Boolean) {
        item = data
        check = type
    }
}