package com.example.sudoku.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.sudoku.game.Cell
import com.example.sudoku.game.Grid

class SudokuGridView(context:Context, attributeSet:AttributeSet) :View(context,attributeSet) {

    private var sqrtSize = 3;
    private var size = 9;

    private var cellSize = 0F;
    private var noteSize = 0F;

    private var selectedRow = -1
    private var selectedCol = -1

   // var cells: List<Cell>? = null
    var grid:Grid?=null

    private var listener: SudokuGridView.OnTouchListener? = null

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 6F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#d3eaf2")

    }
    private val conflictCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#e9f5f9")

    }

    private val StartCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#69e8ab")

    }

    private val TextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private val TextPaintGoodAnswer = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
    }

    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private val startCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    private fun updateMesurements(width:Int){

        cellSize = (width / size).toFloat()
        noteSize = cellSize/sqrtSize.toFloat()
        noteTextPaint.textSize=cellSize/sqrtSize.toFloat()
        TextPaint.textSize=cellSize/1.5F
        startCellTextPaint.textSize=cellSize/1.5F

    }

    override fun onDraw(canvas: Canvas) {
        updateMesurements(width)
        fillCells(canvas)
        drawLine(canvas)
        drawText(canvas)
    }

    private fun fillCells(canvas: Canvas) {

        grid?.cells?.forEach {

                val r = it.row
                val c = it.col

                if (it.isStartingCell)
                    fillCell(canvas, r, c, StartCellPaint)
                else if (selectedRow != -1 && selectedCol != -1) {
                    if (r == selectedRow && c == selectedCol) {
                        fillCell(canvas, r, c, selectedCellPaint)
                    } else if (r == selectedRow || c == selectedCol) {
                        fillCell(canvas, r, c, conflictCellPaint)
                    } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                        fillCell(canvas, r, c, conflictCellPaint)
                    }
                }

        }
    }


    private fun fillCell(canvas: Canvas,r: Int,c:Int,paint:Paint){
    canvas.drawRect(c*cellSize,r*cellSize,(c+1)*cellSize,(r+1)*cellSize,paint)
    }


    private fun drawLine(canvas:Canvas){

        canvas.drawRect(0F,0F,width.toFloat(),width.toFloat(),thickLinePaint)
        for(i in 1 until size){
           val paintToUse=when(i%sqrtSize){
               0->thickLinePaint
               else->thinLinePaint
           }
            canvas.drawLine(i*cellSize,0F,i*cellSize,width.toFloat(),paintToUse)
            canvas.drawLine(0F,i*cellSize,width.toFloat(),i*cellSize,paintToUse)
        }
    }
    private fun drawText(canvas:Canvas){
        grid?.cells?.forEach {
            val textBound=Rect()


            if (it.value == 0) {
                it.notes.forEach{note->
                    val rowInCell=(note-1)/sqrtSize
                    val colInCell=(note-1)%sqrtSize

                    val valueString=note.toString()
                    noteTextPaint.getTextBounds(valueString,0,note.toString().length,textBound)
                    val textWidth=noteTextPaint.measureText(valueString)
                    val textHeight=textBound.height()

                    val posX=(it.col*cellSize)+(colInCell*noteSize)+noteSize/2-textWidth/2
                    val posY=(it.row*cellSize)+(rowInCell*noteSize)+noteSize/2F+textHeight/2F

                    canvas.drawText(valueString,posX,posY,noteTextPaint)
                }

            } else {
                val valueString=it.value.toString()
                val paint=if(it.isStartingCell) startCellTextPaint else TextPaint
                paint.getTextBounds(valueString,0,valueString.length,textBound)
                val textWidth=paint.measureText(valueString)
                val textHeight=textBound.height()
                //ONLY FOR DEBUG
                if(it.value==it.getCorrectValue())
                {
                    paint.color=Color.BLUE
                }
                canvas.drawText(valueString,(it.col*cellSize)+cellSize/2-textWidth/2,
                (it.row*cellSize)+cellSize/2F+textHeight/2F,paint)
            }
        }
    }

    private fun drawNotes(canvas:Canvas){
        grid?.cells?.forEach {
            val valueString=it.value.toString()
            val textBound=Rect()
            val paint=if(it.isStartingCell) startCellTextPaint else TextPaint
            TextPaint.getTextBounds(valueString,0,valueString.length,textBound)
            val textWitdh=TextPaint.measureText(valueString)
            val textHeight=textBound.height()

            canvas.drawText(valueString,(it.col*cellSize)+cellSize/2-textWitdh/2,
                (it.row*cellSize)+cellSize/2-textHeight/2,TextPaint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when(event.action){
            MotionEvent.ACTION_DOWN->{
                handleTouch(event.x,event.y)
                true
            }
            else->false
        }
    }




    private fun handleTouch(x:Float,y:Float){
        if((y/cellSize).toInt()>=9||(y/cellSize).toInt()<0) return
        if((x/cellSize).toInt()>=9||(x/cellSize).toInt()<0) return
        var  possibleSelectedRow =(y/cellSize).toInt();
        var possibleSelectedCol=(x/cellSize).toInt();

        listener?.onCellTouch(possibleSelectedRow,possibleSelectedCol);
    }

    fun updateSelectedCell(row:Int,col:Int){
        if((y/cellSize).toInt()>=9||(y/cellSize).toInt()<0) return
        if((x/cellSize).toInt()>=9||(x/cellSize).toInt()<0) return
        selectedRow=row
        selectedCol=col
        invalidate()
    }

    fun updateCell(grid:Grid?){
        this.grid=grid
        invalidate()
    }

    fun registerListener(listener:SudokuGridView.OnTouchListener){
        this.listener=listener
    }

    interface  OnTouchListener{
       fun  onCellTouch(row:Int,col:Int)
    }
}