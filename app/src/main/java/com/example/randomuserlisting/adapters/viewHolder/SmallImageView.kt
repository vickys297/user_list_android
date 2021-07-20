package com.example.randomuserlisting.adapters.viewHolder

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.randomuserlisting.R
import com.example.randomuserlisting.databinding.RecyclerUserListItemBinding
import com.example.randomuserlisting.databinding.RecyclerUserListItemSmallBinding
import com.example.randomuserlisting.model.UserModel

class SmallImageView(private val binding: RecyclerUserListItemSmallBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UserModel) {
        binding.apply {
            userData = item
            executePendingBindings()
        }


        val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide
            .with(binding.root.context)
            .load(item.picture.large)
            .placeholder(circularProgressDrawable)
            .fallback(R.drawable.image_placeholder)
            .centerCrop()
            .into(binding.imageViewProfile)


    }
}