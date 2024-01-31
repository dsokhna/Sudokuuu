package com.example.sudoku.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.sudoku.R
import com.example.sudoku.view.SudokuGridView

class RestartwithNewGameDialog :DialogFragment(){

    private var restartGameListener: NoticeRestartDialogListener? = null

    interface NoticeRestartDialogListener {
        fun onDialogRestartGameClick(dialog: DialogFragment)
        fun onDialogCancelRestartClick(dialog: DialogFragment)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;


            builder.setView(inflater.inflate(R.layout.restart_with_new_game_dialog, null))
                 .setPositiveButton("Restart",
                    DialogInterface.OnClickListener { dialog, id ->
                        restartGameListener?.onDialogRestartGameClick(this)
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        restartGameListener?.onDialogCancelRestartClick(this)
                    })


            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")

    }

    override fun onAttach(context:Context) {
        super.onAttach(context)
        try {
            restartGameListener = context as NoticeRestartDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

}