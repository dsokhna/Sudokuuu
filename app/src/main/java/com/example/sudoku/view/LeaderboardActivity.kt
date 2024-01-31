package com.example.sudoku.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.R
import com.example.sudoku.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getIntent = intent
        val listLeaderboard = findViewById<ListView>(R.id.ListViewLeaderboard)

        val getName = intent.getStringArrayListExtra("dataName")
        val getTime = intent.getStringArrayListExtra("dataTime")
        val getErrors = intent.getStringArrayListExtra("dataErrors")
        val getAllData = ArrayList<String>()
        var index = 0

        while ((index < getName!!.size) and (index < getTime!!.size) and (index < getErrors!!.size)) {
            getAllData.add("${index+1} " + getName[index] + " " + getTime[index] + " " + getErrors[index])
            index++
        }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, getAllData)
        listLeaderboard.adapter = arrayAdapter
        
        val getOnlineData = ArrayList<String>()
        /*
        val userRef = FirebaseDatabase.getInstance().getReference("Users").orderByKey()
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (postSnapshot in dataSnapshot.children) {

                    var x = 1

                    val Username = dataSnapshot.child("nomdutilisateur").getValue(String::class.java)
                    val Nom = dataSnapshot.child("nom").getValue(String::class.java)
                    val Prenom = dataSnapshot.child("prenom").getValue(String::class.java)
                    val Score = dataSnapshot.child("score").getValue(Int::class.java)
                    getOnlineData.add("$x " + "$Username " + "$Score")
                    x++
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        val arrayAdapterOnline = ArrayAdapter(this, android.R.layout.simple_list_item_1, getOnlineData)

         */
        
        binding.BackButton.setOnClickListener {
            super.finish()
        }

        binding.OnlineButton.setOnClickListener{
            
            if(binding.OnlineButton.text == "Online Leaderboard") {



                listLeaderboard.adapter = arrayAdapterOnline
                binding.OnlineButton.text = "Personnal Leaderboard"
                binding.TextViewLeaderboard.text = "Online Leaderboard"
            }
            else {
                listLeaderboard.adapter = arrayAdapterLocal
                binding.OnlineButton.text = "Online Leaderboard"
                binding.TextViewLeaderboard.text = "Personnal Leaderboard"
            }
        }

    }

}
