package com.example.randomuserlisting.adapters.viewHolder

import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.randomuserlisting.databinding.RecyclerLoadStateBinding


class UserListLoadStateViewHolder(val binding: RecyclerLoadStateBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(loadState: LoadState): RecyclerLoadStateBinding {

        binding.apply {
            executePendingBindings()
        }

        when (loadState) {
            is LoadState.Error -> {
                Toast.makeText(
                    binding.root.context,
                    loadState.error.message,
                    Toast.LENGTH_SHORT
                ).show()

                binding.buttonRetry.isVisible = true
            }
            else -> {

                binding.loading.isVisible = loadState is LoadState.Loading
                binding.loading.isGone = loadState is LoadState.NotLoading
                binding.buttonRetry.isVisible = loadState !is LoadState.Loading
            }
        }
        return binding
    }

}
