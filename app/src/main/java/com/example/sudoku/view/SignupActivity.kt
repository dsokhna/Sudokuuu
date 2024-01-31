package com.example.sudoku.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.databinding.ActivitySignupBinding
import com.example.sudoku.viewModel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

import com.google.firebase.database.FirebaseDatabase
class SignupActivity  : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance().getReference("Users")

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val nom =binding.nom.text.toString()
            val prenom = binding.prenom.text.toString()
            val email = binding.emailEt.text.toString()
            val nomdutilisateur=binding.nomdutilisateur.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()



            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            database=FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.currentUser!!.uid)
                            val user= User(nom,prenom,0,nomdutilisateur, firebaseAuth.currentUser!!.uid)
                            database.setValue(user).addOnSuccessListener {
                                binding.nom.text.clear()
                                binding.prenom.text.clear()
                                binding.nomdutilisateur.text.clear()

                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)

                            }

                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Les mot depasse sont different", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ce champ ne peut pas etre vide !!", Toast.LENGTH_SHORT).show()

            }
        }

    }

}