package com.example.randomuserlisting.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.randomuserlisting.databinding.RecyclerLoadStateBinding
import com.example.randomuserlisting.utils.AppInterface
import java.net.UnknownHostException

class UserListLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<UserListLoadStateAdapter.UserListLoadStateViewHolder>() {


    class UserListLoadStateViewHolder(val binding: RecyclerLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val TAG = UserListLoadStateViewHolder::class.java.canonicalName
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
            Log.i(TAG, "bind: loadState $loadState")
            return binding
        }

    }

    override fun onBindViewHolder(holder: UserListLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState).apply {
            holder.binding.buttonRetry.setOnClickListener {
                retry.invoke()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): UserListLoadStateViewHolder {
        return UserListLoadStateViewHolder(
            RecyclerLoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}

