package com.example.plannerbetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.plannerbetter.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogInBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val userPassword = binding.userPassword.text.toString()

            if(userEmail.isEmpty() || userPassword.isEmpty()){
                Toast.makeText(this,"Invalid credentials",Toast.LENGTH_SHORT).show()

            }else{
                auth.signInWithEmailAndPassword(userEmail,userPassword)
                    .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }


        binding.toSignUpPage.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}