package com.example.sudoku.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.sudoku.R
import com.example.sudoku.databinding.EndGameDialogBinding

class EndGameDialog:DialogFragment() {
    private var EndGameListener: NoticeEndGameDialogListener? = null
    lateinit var endGameDialogBinding:EndGameDialogBinding

    interface NoticeEndGameDialogListener {
        fun onDialogEndGameClick(dialog: DialogFragment)
        fun onShareClick(dialog:DialogFragment)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {


            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            endGameDialogBinding=EndGameDialogBinding.inflate(this.layoutInflater)
            builder.setView(inflater.inflate(R.layout.end_game_dialog, null))
                .setPositiveButton("Restart",
                    DialogInterface.OnClickListener { dialog, id ->
                        EndGameListener?.onDialogEndGameClick(this)
                    })
            endGameDialogBinding.ShareBtn.setOnClickListener{EndGameListener?.onShareClick(this)}
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            EndGameListener = context as NoticeEndGameDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

}