package com.devspace.taskbeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class CreateDeleteCategory (
    private val categoryList: List<CategoryUiData>,
    private val onCreateClicked: (CategoryUiData) -> Unit,
    private val onDeleteClicked: (CategoryUiData) -> Unit,
): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_delete_category_sheet, container, false)
        val btnCreate = view.findViewById<Button>(R.id.btn_create)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete_category)
        val categoryName = view.findViewById<TextInputEditText>(R.id.category_name_edit_text)
        val spinner: Spinner = view.findViewById(R.id.category_list)
        var deleteCategory: String? =  null
        val categoryStr = categoryList.filter { it.name != "edit" && it.name != "ALL" }.map { it.name }

        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            categoryStr
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                deleteCategory = categoryStr[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

       btnCreate.setOnClickListener {
           val newCategory = categoryName.text.toString()
           onCreateClicked.invoke(
               CategoryUiData(
               name = newCategory,
               isSelected = false)
           )
           dismiss()
       }

        btnDelete.setOnClickListener {
            val category = deleteCategory.toString()
            onDeleteClicked.invoke(
                CategoryUiData(
                    name = category,
                    isSelected = false)
            )
            dismiss()
        }


        return view
    }



}