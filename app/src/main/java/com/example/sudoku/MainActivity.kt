package com.example.sudoku

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.databinding.ActivityMainBinding
import com.example.sudoku.dialogs.EndGameDialog
import com.example.sudoku.dialogs.RestartwithNewGameDialog
import com.example.sudoku.game.Cell
import com.example.sudoku.game.Grid
import com.example.sudoku.game.sudokuGame
import com.example.sudoku.view.SudokuGridView
import com.example.sudoku.viewModel.SudokuViewModel
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity(),SudokuGridView.OnTouchListener,
    RestartwithNewGameDialog.NoticeRestartDialogListener,
    EndGameDialog.NoticeEndGameDialogListener {

    private lateinit var viewModel:SudokuViewModel
    private lateinit var binding: ActivityMainBinding

  //  private  val buttons= listOf(binding.OneButton,binding.TwoButton,binding.ThreeButton,binding.FourButton,binding.FiveButton,binding.SixButton,binding.SevenButton,binding.EightButton,binding.NineButton)

    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // setContentView(R.layout.activity_main)
       binding.Board.registerListener(this)
       
       var Mistake = binding.TextViewMistake

        viewModel= ViewModelProvider(this).get(SudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { 
            updateSelectedCell(it)
            Mistake.setText("Mistakes: ${viewModel.sudokuGame.nbMistake}")
        })
        viewModel.sudokuGame.gridLiveData.observe(this,Observer{updateCell(it)})
        
        viewModel.sudokuGame.timer = binding.SimpleChronometer
        
        val buttons= listOf(binding.OneButton,binding.TwoButton,binding.ThreeButton,binding.FourButton,binding.FiveButton,binding.SixButton,binding.SevenButton,binding.EightButton,binding.NineButton)

        buttons.forEachIndexed{index,button->button.setOnClickListener{viewModel.sudokuGame.handleInput(index+1,binding.NoteSwitch.isChecked)
         GameOver()}}
        binding.Delete.setOnClickListener{viewModel.sudokuGame.delete(binding.NoteSwitch.isChecked)}
        binding.NewGame.setOnClickListener{AskStartNewGame()}
        binding.Retour.setOnClickListener{BackToMenu()}
        viewModel.sudokuGame.startTimer()
    }

    private fun updateCell(grid:Grid?)=grid?.let{
        binding.Board.updateCell(grid)
        //Check if game is over
    }


    private fun updateSelectedCell(cell:Pair<Int,Int>?)=cell?.let{
      binding.Board.updateSelectedCell(cell.first,cell.second)
    }

  override  fun onCellTouch(row:Int,col:Int){
        viewModel.sudokuGame.updateCell(row,col)
    }

    fun GameOver(){
        if(viewModel.sudokuGame.grid.gridCompleted()){
           //Implement popup and add data to database
            var dialog=EndGameDialog()
            dialog.show(supportFragmentManager,"customDialog")
        }
    }


    fun AskStartNewGame(){
        var dialog=RestartwithNewGameDialog()
        dialog.show(supportFragmentManager,"customDialog")
    }


    override fun onDialogRestartGameClick(dialog: DialogFragment) {
       viewModel.sudokuGame.StartNewGame()
    }

    override fun onDialogCancelRestartClick(dialog: DialogFragment) {
        // User touched the dialog's negative button
    }

    override fun onDialogEndGameClick(dialog: DialogFragment) {
        viewModel.sudokuGame.StartNewGame()
    }

    override fun onShareClick(dialog: DialogFragment) {
        //What require to share
    }

    fun BackToMenu(){
        finish()
    }

    }
