package com.devspace.taskbeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateUpdateDeleteTask (
    private val categoryList: List<CategoryUiData>,
    private val task: TaskUiData? = null,
    private val onCreateClicked: (TaskUiData) -> Unit,
    private val onUpdateClicked: (TaskUiData) -> Unit,
    private val onDeleteClicked: (TaskUiData) -> Unit,
): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_update_task_sheet, container, false)

        val btnCreateorUpdate = view.findViewById<Button>(R.id.btn_create_update)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete)
        val taskInput = view.findViewById<TextInputEditText>(R.id.task_edit_text)
        val title = view.findViewById<TextView>(R.id.tv_title)
        val spinner: Spinner = view.findViewById(R.id.category_list)

        var taskCategory: String? =  null
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
                taskCategory = categoryStr[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        if(task == null){
            title.setText(R.string.create_title)
            btnCreateorUpdate.setText(R.string.create)
            btnDelete.isVisible = false
        }
        else {
            title.setText(R.string.update_title)
            btnCreateorUpdate.setText(R.string.update)
            taskInput.setText(task.task)

            val currentCategory = task.category
            val categoryIndex = categoryStr.indexOf(currentCategory)

            spinner.setSelection(categoryIndex)

        }

       btnCreateorUpdate.setOnClickListener {
           val newTask = taskInput.text.toString()
           if (taskCategory != null && newTask.trim().isNotEmpty()) {
               if (task == null)
                   onCreateClicked.invoke(
                       TaskUiData(
                           id = 0,
                           task = newTask,
                           category = taskCategory!!
                       )
                   )

               else{
                   onUpdateClicked.invoke(
                       TaskUiData(
                           id = task.id,
                           task = newTask,
                           category = taskCategory!!
                       )
                   )
               }
               dismiss()
           }
           else {
               Snackbar.make(
                   btnCreateorUpdate,
                   "Select a category and write your task",
                   Snackbar.LENGTH_SHORT
               ).show()
           }

       }

        btnDelete.setOnClickListener {
            val Task = taskInput.text.toString()
            if (task != null) {
                onDeleteClicked.invoke(
                    TaskUiData(
                        id = task.id,
                        task = Task,
                        category = taskCategory!!
                    )
                )
            }
            dismiss()
        }

        return view
    }
}