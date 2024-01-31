package com.example.sudoku.game

import android.os.SystemClock
import android.widget.Chronometer
import androidx.lifecycle.MutableLiveData

class sudokuGame {
    var selectedCellLiveData=MutableLiveData<Pair<Int,Int>>()

    var gridLiveData=MutableLiveData<Grid>()
    private  var selectedRow=-1
    private var selectedCol=-1

    //TO SAVE
    var grid=Grid(9)

    // TO SAVE
    var nbMistake=0

    //Timer TO SAVE we have to make play
    var timer: Chronometer? = null

    init {
        grid.generateGrid()
        gridLiveData.postValue(grid)
    }

    fun handleInput(number:Int,isTakingNotes:Boolean) {
        var gameOver:Boolean
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = grid.getCells(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number))
                cell.notes.remove(number)
            else
                cell.notes.add(number)
        } else {
            cell.value = number
            if(!cell.isGoodValue())
                nbMistake++;
            else{
                //Check if grid completed
                gameOver=grid.gridCompleted()
            }

        }
        gridLiveData.postValue(grid)
        //cellsLiveData.postValue(grid.cells)
    }

    fun updateCell(row:Int,col:Int){
        //if(!grid.getCells(row,col).isStartingCell){
        selectedRow=row
        selectedCol=col
        selectedCellLiveData.postValue(Pair(row,col))//}
    }



    fun delete(isTakingNotes: Boolean){
        if (selectedRow == -1 || selectedCol == -1) return
        val cell=grid.getCells(selectedRow,selectedCol)
        if(isTakingNotes)
            cell.notes.clear()
        else{
           cell.value=0
        }
        gridLiveData.postValue(grid)
    }
    
        fun startTimer() {
        timer!!.setBase(SystemClock.elapsedRealtime())
        timer?.start()
    }

    fun stopTimer() {
        timer?.stop()
    }

    fun StartNewGame(){
        grid.generateGrid()
        gridLiveData.postValue(grid)
    }
}
