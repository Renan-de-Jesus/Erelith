package com.rpgmanager.ui.characters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rpgmanager.R
import com.rpgmanager.data.model.Character
import com.rpgmanager.databinding.ItemCharacterBinding
import java.io.File

class CharacterAdapter(
    private val onItemClick: (Character) -> Unit,
    private val onEditClick: (Character) -> Unit,
    private val onDeleteClick: (Character) -> Unit,
    private val onLevelUpClick: (Character) -> Unit
) : ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CharacterViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            binding.tvCharacterName.text = character.name
            binding.tvPlayerName.text = binding.root.context.getString(
                R.string.player_label, character.playerName
            )
            binding.tvClassRace.text = binding.root.context.getString(
                R.string.class_race_label, character.characterClass, character.race
            )
            binding.tvLevel.text = binding.root.context.getString(
                R.string.level_label, character.level
            )

            // IsAlive shown as chip/status
            binding.chipAlive.text = if (character.isAlive)
                binding.root.context.getString(R.string.status_alive)
            else
                binding.root.context.getString(R.string.status_dead)
            binding.chipAlive.setChipBackgroundColorResource(
                if (character.isAlive) R.color.alive_green else R.color.dead_red
            )

            // ImageView with Glide
            if (!character.imagePath.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(File(character.imagePath))
                    .placeholder(R.drawable.ic_character_placeholder)
                    .circleCrop()
                    .into(binding.ivCharacterImage)
            } else {
                binding.ivCharacterImage.setImageResource(R.drawable.ic_character_placeholder)
            }

            binding.root.setOnClickListener { onItemClick(character) }
            binding.btnEdit.setOnClickListener { onEditClick(character) }
            binding.btnDelete.setOnClickListener { onDeleteClick(character) }
            binding.btnLevelUp.setOnClickListener { onLevelUpClick(character) }
        }
    }

    class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Character, newItem: Character) =
            oldItem == newItem
    }
}
