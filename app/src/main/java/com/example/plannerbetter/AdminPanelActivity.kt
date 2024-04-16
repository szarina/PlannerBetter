package com.example.plannerbetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.example.plannerbetter.databinding.ActivityAdminPanelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class AdminPanelActivity : AppCompatActivity() {
    lateinit var  binding: ActivityAdminPanelBinding
    lateinit var auth :FirebaseAuth
    lateinit var  databaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth




        binding.createUserButton.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val userPassword = binding.userPassword.text.toString()
            val userConfirmPassword = binding.confirmPassword.text.toString()
            val userFirstName = binding.userFirstName.text.toString()
            val userLastName = binding.userLastName.text.toString()
            val userIIN = binding.userIIN.text.toString()
            val selectedGender:Int = binding.radioGroup.checkedRadioButtonId



            if(userEmail.isEmpty() || userPassword.isEmpty() || userConfirmPassword.isEmpty()
                || userFirstName.isEmpty() ||  userLastName.isEmpty() || userIIN.isEmpty() ||
                selectedGender == -1 ){
                Toast.makeText(this,"Invalid credentials", Toast.LENGTH_SHORT).show()

            } else if (userConfirmPassword!=userPassword){
                Toast.makeText(this,"Passwords are not the same", Toast.LENGTH_SHORT).show()
            } else if (userIIN.length!=12){
                Toast.makeText(this,"IIN must be 12 numbers long", Toast.LENGTH_SHORT).show()
            }else{

                auth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){

                        Toast.makeText(this,"Authentication Success", Toast.LENGTH_SHORT).show()

                        val userId = auth.currentUser?.uid
                        val selectedRadioButton: RadioButton? =findViewById(selectedGender)
                        val selectedGenderText = selectedRadioButton?.text.toString()

                        val user = User(userFirstName,userLastName,false,selectedGenderText,userIIN,"")
                        databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")

                        databaseRef.setValue(user).addOnCompleteListener {
                                task ->
                            if(task.isSuccessful){
                                Toast.makeText(this,"Data Stored", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this,LogInActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this,"Error on Server", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    else{
                        Toast.makeText(this,"Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }



        binding.toMainPage.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}