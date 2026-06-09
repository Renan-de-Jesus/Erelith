package com.rpgmanager.ui.characters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rpgmanager.R
import com.rpgmanager.data.model.Character
import com.rpgmanager.databinding.FragmentCharacterFormBinding
import com.rpgmanager.viewmodel.CharacterViewModel
import kotlinx.coroutines.launch
import java.io.File

class CharacterFormFragment : Fragment() {

    private var _binding: FragmentCharacterFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharacterViewModel by activityViewModels()
    private val args: CharacterFormFragmentArgs by navArgs()

    private var existingCharacter: Character? = null
    private var selectedImagePath: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImagePath = getRealPathFromUri(uri)
                Glide.with(this).load(uri).circleCrop().into(binding.ivCharacterImage)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.characterId != 0L) {
            loadExistingCharacter(args.characterId)
        }

        binding.ivCharacterImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener { saveCharacter() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun loadExistingCharacter(id: Long) {
        lifecycleScope.launch {
            viewModel.getCharacterById(id)?.let { character ->
                existingCharacter = character
                binding.etCharacterName.setText(character.name)
                binding.etPlayerName.setText(character.playerName)
                binding.etRace.setText(character.race)
                binding.etClass.setText(character.characterClass)
                binding.etLevel.setText(character.level.toString())
                binding.etBackstory.setText(character.backstory)
                // RadioGroup — is alive
                if (character.isAlive) {
                    binding.radioAlive.isChecked = true
                } else {
                    binding.radioDead.isChecked = true
                }
                selectedImagePath = character.imagePath
                if (!character.imagePath.isNullOrEmpty()) {
                    Glide.with(this@CharacterFormFragment)
                        .load(File(character.imagePath))
                        .circleCrop()
                        .into(binding.ivCharacterImage)
                }
            }
        }
    }

    private fun saveCharacter() {
        val name = binding.etCharacterName.text.toString().trim()
        val player = binding.etPlayerName.text.toString().trim()
        val race = binding.etRace.text.toString().trim()
        val charClass = binding.etClass.text.toString().trim()
        val levelStr = binding.etLevel.text.toString().trim()
        val backstory = binding.etBackstory.text.toString().trim()
        val isAlive = binding.radioGroupAlive.checkedRadioButtonId == R.id.radioAlive

        if (name.isEmpty()) { binding.etCharacterName.error = getString(R.string.error_name_required); return }
        if (player.isEmpty()) { binding.etPlayerName.error = getString(R.string.error_player_required); return }
        if (race.isEmpty()) { binding.etRace.error = getString(R.string.error_race_required); return }
        if (charClass.isEmpty()) { binding.etClass.error = getString(R.string.error_class_required); return }

        val level = levelStr.toIntOrNull() ?: 1

        val character = existingCharacter?.copy(
            name = name,
            playerName = player,
            race = race,
            characterClass = charClass,
            level = level,
            backstory = backstory,
            isAlive = isAlive,
            imagePath = selectedImagePath
        ) ?: Character(
            groupId = args.groupId,
            name = name,
            playerName = player,
            race = race,
            characterClass = charClass,
            level = level,
            backstory = backstory,
            isAlive = isAlive,
            imagePath = selectedImagePath
        )

        if (existingCharacter != null) {
            viewModel.update(character)
            Toast.makeText(requireContext(), R.string.character_updated, Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(character)
            Toast.makeText(requireContext(), R.string.character_created, Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        return try {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val idx = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (it.moveToFirst() && idx >= 0) it.getString(idx) else uri.path
            } ?: uri.path
        } catch (e: Exception) { uri.path }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
