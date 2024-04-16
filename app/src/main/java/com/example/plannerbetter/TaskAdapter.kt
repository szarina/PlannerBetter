package com.example.plannerbetter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plannerbetter.databinding.TaskItemBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class TaskAdapter :RecyclerView.Adapter<TaskAdapter.TaskHolder>() {
    var taskList = ArrayList <Task>()

    class  TaskHolder(item : View): RecyclerView.ViewHolder(item){
        val binding = TaskItemBinding.bind(item)
        fun bind(task: Task) = with(binding){
            taskText.text = task.text
            checkbox.isChecked = task.isDone
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item,parent,false)
        return TaskHolder(view)
    }

    override fun getItemCount(): Int {
        return  taskList.size
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bind(taskList[position])
        holder.binding.delete.setOnClickListener {

            val taskId = taskList[position].id

            val userId = Firebase.auth.currentUser?.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("tasks/$userId/$taskId")
            databaseRef.removeValue()

            taskList.removeAt(position)
            notifyItemRemoved(position)
        }

        holder.binding.checkbox.setOnClickListener {
            val updateMap = HashMap<String,Any>()
            updateMap["done"] = holder.binding.checkbox.isChecked
            val taskId = taskList[position].id

            val userId = Firebase.auth.currentUser?.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("tasks/$userId/$taskId")
            databaseRef.updateChildren(updateMap).addOnSuccessListener {  }.addOnFailureListener {  }
        }
    }
    fun  addTasks(newTaskList: ArrayList <Task>){
        taskList.clear()
        newTaskList.forEach{book-> taskList.add(book)}
        notifyDataSetChanged()
    }


}