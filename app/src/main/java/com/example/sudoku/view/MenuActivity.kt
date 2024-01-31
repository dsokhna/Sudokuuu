package com.example.sudoku.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.MainActivity
import com.example.sudoku.database.Database
import com.example.sudoku.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    lateinit var dbSudoku: Database

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.StartGameButton.setOnClickListener{
            val intentStart = Intent(this, MainActivity::class.java)
            startActivity(intentStart)
        }
        binding.ProfileButton.setOnClickListener{
            val intentProfile = Intent(this, ProfileActivity::class.java)
            startActivity(intentProfile)
        }
        binding.LeaderboardButton.setOnClickListener {

            dbSudoku = Database(this, null)
            
            if (::dbSudoku.isInitialized) {
                val getData = dbSudoku.getTable()

                val intentLeaderboard = Intent(this, LeaderboardActivity::class.java)

                val nameList = ArrayList<String>()
                val timeList = ArrayList<String>()
                val errorsList = ArrayList<String>()

                while (getData!!.moveToNext()) {
                    nameList.add(getData.getString(1))
                    timeList.add(getData.getString(2))
                    errorsList.add(getData.getString(3))
                }
                intentLeaderboard.putExtra("dataName", nameList)
                intentLeaderboard.putExtra("dataTime", timeList)
                intentLeaderboard.putExtra("dataErrors", errorsList)

                startActivity(intentLeaderboard)
            }
            else {
                Toast.makeText(this, "The leaderboard is currently empty", Toast.LENGTH_LONG).show()
            }
        }

    }
}
