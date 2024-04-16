package com.example.plannerbetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.plannerbetter.databinding.ActivityTasksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class TaskActivity : AppCompatActivity() {
    lateinit var binding : ActivityTasksBinding
    lateinit var auth:FirebaseAuth
    lateinit var databaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var taskList = ArrayList <Task>()
        auth = Firebase.auth
        val userId = auth.currentUser?.uid
        var taskAdapter = TaskAdapter()
        binding.rView.layoutManager = LinearLayoutManager(this@TaskActivity)
        binding.rView.adapter = taskAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("tasks/${userId}")

        databaseRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null) {
                        taskList.add(task)
                    }

                }

                taskAdapter.addTasks(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })



            binding.addTaskButton.setOnClickListener {
                var text = binding.inputTask.text.toString()
                if (text.isNotEmpty()) {
                    val taskKey = databaseRef.push().key
                    val task = Task(taskKey.toString(),userId.toString(), text, false)

                    databaseRef =
                        FirebaseDatabase.getInstance().getReference("tasks/${userId}/${taskKey}")
                    databaseRef.setValue(task).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@TaskActivity, "Data Stored", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this@TaskActivity, "Error on Server", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }

        }



}