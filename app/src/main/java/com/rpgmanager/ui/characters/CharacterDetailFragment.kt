package com.rpgmanager.ui.characters

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rpgmanager.R
import com.rpgmanager.databinding.FragmentCharacterDetailBinding
import com.rpgmanager.ui.attributes.AttributeAdapter
import com.rpgmanager.ui.groups.DeleteConfirmDialog
import com.rpgmanager.viewmodel.AttributeViewModel
import com.rpgmanager.viewmodel.CharacterViewModel
import kotlinx.coroutines.launch
import java.io.File

class CharacterDetailFragment : Fragment() {

    private var _binding: FragmentCharacterDetailBinding? = null
    private val binding get() = _binding!!
    private val characterViewModel: CharacterViewModel by activityViewModels()
    private val attributeViewModel: AttributeViewModel by activityViewModels()
    private val args: CharacterDetailFragmentArgs by navArgs()
    private lateinit var attributeAdapter: AttributeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attributeViewModel.setCharacterId(args.characterId)

        loadCharacterInfo()
        setupAttributeList()
        setupSearch()

        binding.fabAddAttribute.setOnClickListener {
            findNavController().navigate(
                CharacterDetailFragmentDirections
                    .actionCharacterDetailFragmentToAttributeFormFragment(
                        attributeId = 0L,
                        characterId = args.characterId
                    )
            )
        }
    }

    private fun loadCharacterInfo() {
        lifecycleScope.launch {
            characterViewModel.getCharacterById(args.characterId)?.let { character ->
                binding.tvDetailName.text = character.name
                binding.tvDetailPlayer.text = getString(R.string.player_label, character.playerName)
                binding.tvDetailClassRace.text = getString(R.string.class_race_label, character.characterClass, character.race)
                binding.tvDetailLevel.text = getString(R.string.level_label, character.level)
                binding.tvDetailBackstory.text = character.backstory.ifEmpty { getString(R.string.no_backstory) }

                binding.chipDetailAlive.text = if (character.isAlive)
                    getString(R.string.status_alive) else getString(R.string.status_dead)

                if (!character.imagePath.isNullOrEmpty()) {
                    Glide.with(this@CharacterDetailFragment)
                        .load(File(character.imagePath))
                        .placeholder(R.drawable.ic_character_placeholder)
                        .into(binding.ivDetailCharacterImage)
                } else {
                    binding.ivDetailCharacterImage.setImageResource(R.drawable.ic_character_placeholder)
                }
            }
        }
    }

    private fun setupAttributeList() {
        attributeAdapter = AttributeAdapter(
            onEditClick = { attribute ->
                findNavController().navigate(
                    CharacterDetailFragmentDirections
                        .actionCharacterDetailFragmentToAttributeFormFragment(
                            attributeId = attribute.id,
                            characterId = args.characterId
                        )
                )
            },
            onDeleteClick = { attribute ->
                DeleteConfirmDialog(
                    title = getString(R.string.delete_attribute_title),
                    message = getString(R.string.delete_attribute_message, attribute.name)
                ) {
                    attributeViewModel.delete(attribute)
                    Toast.makeText(requireContext(), R.string.attribute_deleted, Toast.LENGTH_SHORT).show()
                }.show(childFragmentManager, "DeleteAttribute")
            }
        )

        binding.recyclerAttributes.adapter = attributeAdapter

        attributeViewModel.attributes.observe(viewLifecycleOwner) { attributes ->
            attributeAdapter.submitList(attributes)
            binding.tvEmptyAttributes.visibility =
                if (attributes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupSearch() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)
                val searchView = menu.findItem(R.id.action_search).actionView as SearchView
                searchView.queryHint = getString(R.string.search_attributes_hint)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        attributeViewModel.setSearchQuery(newText.orEmpty())
                        return true
                    }
                })
            }
            override fun onMenuItemSelected(item: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
