package com.example.sudoku.game

class Cell(val row:Int,val col:Int,var value:Int,var isStartingCell:Boolean=false,var notes:MutableSet<Int>) {
    private var correctValue:Int=0

    fun isGoodValue():Boolean{
        return (value==correctValue)
    }

    fun getCorrectValue():Int{
        return correctValue
    }
    fun setCellDefaultInfo(value: Int){
        this.value=value
        this.isStartingCell=true
        this.correctValue=value
    }

    //SetNotStartingCell
    fun setNotStartingCell(value:Int,isStartingCell: Boolean){
        this.value=value
        this.isStartingCell=isStartingCell
    }
}