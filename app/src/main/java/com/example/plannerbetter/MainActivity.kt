package com.example.plannerbetter

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.plannerbetter.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        var isAdmin = false;
        var imgUrl = "";

        val userId = auth.currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().getReference("users/${userId}")

        //Text
        binding.textEmail.text = auth.currentUser?.email

        //Loading the  image
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if(user!=null){
                    imgUrl = user.photo
                    if(imgUrl.isNotEmpty()){
                        Glide.with(this@MainActivity).load(imgUrl).into(binding.imageView)}
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        //Changing the photot
        binding.uploadPictureButton.setOnClickListener {
            imgUrl = binding.inputImgUrl.text.toString()
            if (imgUrl.isNotEmpty()){
                Glide.with(this@MainActivity).load(imgUrl).into(binding.imageView)

                val updateMap = HashMap<String,Any>()
                updateMap["photo"] = imgUrl

                if( userId!= null){
                    databaseRef.child(userId).updateChildren(updateMap).addOnSuccessListener {  }.addOnFailureListener {  }
                }

            }

        }



        //Admin button add user
        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if(user!=null){
                    isAdmin = user.isAdmin
                    if(isAdmin){
                        binding.addUsersButton.visibility = View.VISIBLE
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.addUsersButton.setOnClickListener {
            val intent = Intent(this,AdminPanelActivity::class.java)
            startActivity(intent)
        }

        //Sign out
        binding.signOutbutton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,LogInActivity::class.java)
            startActivity(intent)
        }

        //To the notes
        binding.toNotes.setOnClickListener {
            val intent = Intent(this,TaskActivity::class.java)
            startActivity(intent)
        }

        //Pop up window
        showGreetingPopUp()


    }

    fun showGreetingPopUp(){

        val userId = auth.currentUser?.uid

        val dialogView = LayoutInflater.from(this).inflate(R.layout.pop_up_window,null)

        val greeting :TextView = dialogView.findViewById(R.id.welcomeMessage)

        if(userId!=null){
            databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")
            databaseRef.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    greeting.text = "Welcome ${user?.firstName}  ${user?.lastName}!"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            )
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setCancelable(true)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()


        Handler().postDelayed({
            alertDialog.dismiss()
        },3000)


    }

}