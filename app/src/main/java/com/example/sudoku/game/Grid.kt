package com.example.sudoku.game

import kotlin.random.Random

class Grid(val size:Int) {

    var cells :List<Cell> = emptyList()
    var availableInt= intArrayOf(1,2,3,4,5,6,7,8,9)

    var path:ArrayList<Pair<Int,Pair<Int,Int>>> = ArrayList()

    fun generateGrid(){
         cells=List(9*9){i->Cell(i/9,i%9,  0, false,mutableSetOf<Int>())}
        generateCompleteSoluce()
        RemoveCell()
    }

    fun getCells(row:Int,col:Int)= cells[row*9+col]




    fun generateCompleteSoluce():Boolean{
       var isValid:Boolean=false
        if(gridNotEmpty()){
            isValid=true
        }
        else {
             for (r in 0 until size) {
                for (c in 0 until size) {
                    if (getCells(r, c).value == 0) {
                        availableInt.shuffle()
                        for (i: Int in availableInt) {
                            if (validatePosition(r, c, i)) {
                                getCells(r, c).setCellDefaultInfo(i)
                                //No empty cell

                                    if (generateCompleteSoluce())
                                        return true
                                }
                            }
                        getCells(r, c).setCellDefaultInfo(0)
                        return false
                        }

                    }
                }
            }
        return isValid

    }

    fun validatePosition(row:Int,col: Int,value:Int):Boolean {
        cells.forEach {

            val r = it.row
            val c = it.col


            if (r == row || c == col) {
                if (getCells(r, c).value == value)
                     return false
            } else if (r / 3 == row / 3 && c / 3 == col / 3) {
                if (getCells(r, c).value == value)
                    return false
            }

        }
        return true
    }

    fun gridNotEmpty():Boolean{
        cells.forEach {
            if(it.value==0)
                return false
        }
        return true
    }

    fun countNotEmptyCell():Int{
        var nbCellNotEmpty=0;
        cells.forEach {
            if(it.value!=0)
                nbCellNotEmpty++
        }
        return nbCellNotEmpty
    }

   
    fun RemoveCell(){
        var nbFilledCell=countNotEmptyCell()
        var rounds=3
        while(rounds>0&&nbFilledCell>=80){
            //getRandomCell

            var cellToEmpty:Cell?=null
            var isNotEmptyCell=false
            while(!isNotEmptyCell){

                cellToEmpty=cells.get(Random.nextInt(0,81))
                if(cellToEmpty.value!=0)
                    isNotEmptyCell=true
            }
            cellToEmpty?.setNotStartingCell(0,false)
            nbFilledCell--
            var lstCopyGrid=Array(9*9){i->cells[i].value}
          var nbSoluce= solveGrid(lstCopyGrid)

            if(nbSoluce!=1){
                cellToEmpty?.setCellDefaultInfo(cellToEmpty.getCorrectValue())
                nbFilledCell++;
                rounds--
            }
        }
    }

    fun solveGrid(lstCopyGrid:Array<Int>):Int {
        var nbSoluce = 0
        if (falseGridNotEmpty(lstCopyGrid)) {
            return 1
        } else {
            //For lstGridUntil Empty
            var indexEmptyCell = getIndexEmptyCell(lstCopyGrid)
            val row: Int = indexEmptyCell / size
            val col: Int = indexEmptyCell % size
            for (i in 1 until 10) {
                //isValidPlace
                if(validateFalsePosition(lstCopyGrid,row,col,i))
                {
                    lstCopyGrid[indexEmptyCell]=i
                    nbSoluce+=solveGrid(lstCopyGrid)
                    lstCopyGrid[indexEmptyCell]=0
                }
            }
        }
        return nbSoluce
    }

    fun getIndexEmptyCell(lstCopyGrid: Array<Int>):Int{
        var foundEmpty=false
        var cpt=0
        while (!foundEmpty&&cpt<lstCopyGrid.size)
        {
            if(lstCopyGrid[cpt]==0)
                return cpt
            cpt++
        }
        return 80;
    }

    fun falseGridNotEmpty(lstCopyGrid:Array<Int>):Boolean{
        lstCopyGrid.forEach {
            if(it==0)
                return false
        }
        return true
    }

    fun validateFalsePosition(lstCopyGrid:Array<Int>,row:Int,col:Int,value:Int):Boolean {
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (r == row || c == col) {
                    if (lstCopyGrid[r*size+c] == value)
                        return false
                } else if (r / 3 == row / 3 && c / 3 == col / 3) {
                    if (lstCopyGrid[r*size+c] == value)
                        return false
                }
            }
        }
        return true
    }

    fun gridCompleted():Boolean{
        cells.forEach{
            if(!it.isGoodValue())
                return false
        }
        return true
    }

}