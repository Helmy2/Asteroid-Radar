package com.example.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.databinding.AsteroidItemBinding

class AsteroidListAdapter(
  private val listener: AsteroidListener
) :
    ListAdapter<Asteroid, AsteroidViewHolder>(AsteroidDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(listener,item)
    }
}

class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}

class AsteroidViewHolder(private val binding: AsteroidItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(listener: AsteroidListener, item: Asteroid) {
        binding.asteroid = item
        binding.listener = listener
    }

    companion object {
        fun from(parent: ViewGroup): AsteroidViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AsteroidItemBinding.inflate(layoutInflater, parent, false)
            return AsteroidViewHolder(binding)
        }
    }
}

class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}