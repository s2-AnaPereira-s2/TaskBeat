package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    private var categories = listOf<CategoryUiData>()
    private var tasks = listOf<TaskUiData>()

    private val taskAdapter by lazy {
        TaskListAdapter()
    }
    private val categoryAdapter = CategoryListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            CUDtask()
        }

        categoryAdapter.setOnClickListener { selected ->

            if (selected.name == "edit") {
                CDcategory()
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
        getCategoriesfromDatabase()

        rvTask.adapter = taskAdapter
        getTasksfromDatabase()


        taskAdapter.setOnClickListener { task ->
            CUDtask(task)
        }

    }


    private fun getCategoriesfromDatabase() {
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
                    "edit",
                    false
                )
            )

            GlobalScope.launch(Dispatchers.Main) {
                categories = categoriesUiData
                categoryAdapter.submitList(categoriesUiData)
            }
        }
    }

    private fun getTasksfromDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            val tasksFromDB: List<TaskEntity> = taskDao.getAll()
            val tasksUiData: List<TaskUiData> = tasksFromDB.map {
                TaskUiData(
                    id = it.id,
                    category = it.category,
                    task = it.task,
                )
            }

            GlobalScope.launch(Dispatchers.Main) {
                tasks = tasksUiData
                taskAdapter.submitList(tasksUiData)
            }
        }
    }

    private fun insertCategory(category: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(category)
            getCategoriesfromDatabase()
        }
    }

    private fun deleteCategory(category: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.delete(category)
            getCategoriesfromDatabase()
        }
    }

    private fun insertORupdateTask(task: TaskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.insertORupdate(task)
            getTasksfromDatabase()
        }
    }

    private fun deleteTask(task: TaskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.deletetask(task)
            getTasksfromDatabase()
        }
    }

    private fun CUDtask(taskUiData: TaskUiData? = null){
        val create_update_delete_Task = CreateUpdateDeleteTask(
            task = taskUiData,
            categoryList = categories,
            onCreateClicked = {
                tasktoCreate ->
                val taskEntitytoCreate = TaskEntity(
                    task = tasktoCreate.task,
                    category = tasktoCreate.category
                )
                insertORupdateTask(taskEntitytoCreate)
            },
            onUpdateClicked = {
                tasktoUpdate ->
                val taskEntitytoUpdate = TaskEntity(
                    id = tasktoUpdate.id,
                    task = tasktoUpdate.task,
                    category = tasktoUpdate.category
                )
                insertORupdateTask(taskEntitytoUpdate)
            },
            onDeleteClicked = {
                taskToDelete ->
                val taskEntitytoDelete = TaskEntity(
                    id = taskToDelete.id,
                    task = taskToDelete.task,
                    category = taskToDelete.category
                )
                deleteTask(taskEntitytoDelete)
            },
        )
        create_update_delete_Task.show(supportFragmentManager, "create_update_delete_Task")
    }

    private fun CDcategory(categoryUiData: CategoryUiData? = null){
        val create_delete_Category = CreateDeleteCategory(
            categoryList = categories,
            onCreateClicked = {
                    categorytoCreate ->
                    val categoryEntity = CategoryEntity(
                        name = categorytoCreate.name,
                        isSelected = false,
                    )
                    insertCategory(categoryEntity)
            },
            onDeleteClicked = {
                categoryToDelete ->
                val categoryEntitytoDelete = CategoryEntity(
                    name = categoryToDelete.name,
                    isSelected = false,
                )
                deleteCategory(categoryEntitytoDelete)
            },
        )
        create_delete_Category.show(supportFragmentManager, "create_delete_Category")
    }
}
