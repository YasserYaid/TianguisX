package com.universidadveracruzana.tianguisx.ui.commonmessages

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.DialogReviewBinding
import com.universidadveracruzana.tianguisx.entities.Review

class ReviewDialog (

    private val onSubmitClickListener : (Review) -> Unit

) : DialogFragment() {

    private lateinit var binding : DialogReviewBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogReviewBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val review = Review()

        binding.rbRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            when (binding.rbRating.rating.toInt()){
                1 -> binding.tvRating.text = resources.getString(R.string.VeryBad)
                2 -> binding.tvRating.text = resources.getString(R.string.Bad)
                3 -> binding.tvRating.text = resources.getString(R.string.Regular)
                4 -> binding.tvRating.text = resources.getString(R.string.Good)
                5 -> binding.tvRating.text = resources.getString(R.string.VeryGood)
                else -> binding.tvRating.text = " "
            }
            review.reviewRating = rating
        }

        binding.addReviewButton.setOnClickListener{
            review.reviewDescription = binding.etComment.text.toString()
            onSubmitClickListener.invoke(review)
            dismiss()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}