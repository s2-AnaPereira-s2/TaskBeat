package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskBeatDataBase::class.java, "database-task-beat"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }

    private val taskDao: TaskDao by lazy {
        db.getTaskDao()
    }

    private var categories: MutableList<CategoryUiData> = mutableListOf()
    private var tasks: MutableList<TaskUiData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()

        categoryAdapter.setOnClickListener { selected ->

            if (selected.name == "+") {
                Snackbar.make (rvCategory, "Add category", Snackbar.LENGTH_SHORT)
                    .show()
            }

            else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                val taskTemp =
                    if (selected.name == "ALL") {
                        tasks
                    } else {
                        tasks.filter { it.category == selected.name }
                    }

                taskAdapter.submitList(taskTemp)
                categoryAdapter.submitList(categoryTemp)
            }
        }


        rvCategory.adapter = categoryAdapter
        getCategoriesfromDatabase(categoryAdapter)

        rvTask.adapter = taskAdapter
        getTasksfromDatabase(taskAdapter)
    }


    private fun getCategoriesfromDatabase(adapter: CategoryListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categoriesFromDB: List<CategoryEntity> = categoryDao.getAll()
            val categoriesUiData = categoriesFromDB.map {
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            }.toMutableList()

            categoriesUiData.add(
                CategoryUiData(
                    "+",
                    false
                )
            )

            categories = categoriesUiData

            adapter.submitList(categoriesUiData)
        }
    }

    private fun getTasksfromDatabase(adapter: TaskListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val tasksFromDB: List<TaskEntity> = taskDao.getAll()
            val tasksUiData = tasksFromDB.map {
                TaskUiData(
                    category = it.category,
                    task = it.task,
                )
            }

            tasks = tasksUiData.toMutableList()
            adapter.submitList(tasksUiData)
        }
    }

}
