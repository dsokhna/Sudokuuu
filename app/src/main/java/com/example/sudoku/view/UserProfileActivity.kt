package com.example.sudoku.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sudoku.R
import com.example.sudoku.databinding.ActivityProfileBinding
import com.example.sudoku.databinding.ActivityUserProfileBinding
import com.example.sudoku.viewModel.MyAdapter
import com.example.sudoku.viewModel.User
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserProfileActivity : AppCompatActivity() {
    private lateinit var nom: TextView
    private lateinit var nomdutilisateur: TextView
    private lateinit var score: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter

    private lateinit var binding:ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding and set content view
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nom = binding.nom
        nomdutilisateur = binding.nomdutilisateur
        score = binding.score
        recyclerView = findViewById(R.id.recview)
        binding.Retour.setOnClickListener{finish()}
        // Set up RecyclerView with adapter
        val layoutManager = LinearLayoutManager(this)
        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(FirebaseDatabase.getInstance().getReference("Users"), User::class.java)
            .build()
        myAdapter = MyAdapter(options)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter

        // Get the user ID from the intent
        val userId = intent.getStringExtra("userId")

        // Read from the database
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nom = dataSnapshot.child("nom").getValue(String::class.java)
                val prenom = dataSnapshot.child("prenom").getValue(String::class.java)
                val score = dataSnapshot.child("score").getValue(Int::class.java)
                val nomdutilisateur =
                    dataSnapshot.child("nomdutilisateur").getValue(String::class.java)

                binding.nom.text = "$nom $prenom"
                binding.nomdutilisateur.text = nomdutilisateur
                binding.score.text = score.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}