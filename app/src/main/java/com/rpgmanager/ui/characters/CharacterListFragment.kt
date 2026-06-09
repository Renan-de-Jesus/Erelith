package com.rpgmanager.ui.characters

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rpgmanager.R
import com.rpgmanager.databinding.FragmentCharacterListBinding
import com.rpgmanager.ui.groups.DeleteConfirmDialog
import com.rpgmanager.viewmodel.CharacterViewModel

class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharacterViewModel by activityViewModels()
    private val args: CharacterListFragmentArgs by navArgs()
    private lateinit var adapter: CharacterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setGroupId(args.groupId)

        adapter = CharacterAdapter(
            onItemClick = { character ->
                findNavController().navigate(
                    CharacterListFragmentDirections
                        .actionCharacterListFragmentToCharacterDetailFragment(character.id)
                )
            },
            onEditClick = { character ->
                findNavController().navigate(
                    CharacterListFragmentDirections
                        .actionCharacterListFragmentToCharacterFormFragment(
                            characterId = character.id,
                            groupId = args.groupId
                        )
                )
            },
            onDeleteClick = { character ->
                DeleteConfirmDialog(
                    title = getString(R.string.delete_character_title),
                    message = getString(R.string.delete_character_message, character.name)
                ) {
                    viewModel.delete(character)
                }.show(childFragmentManager, "DeleteCharacter")
            },
            onLevelUpClick = { character ->
                viewModel.levelUp(character.id)
            }
        )

        binding.recyclerCharacters.adapter = adapter

        viewModel.characters.observe(viewLifecycleOwner) { characters ->
            adapter.submitList(characters)
            binding.emptyState.visibility =
                if (characters.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddCharacter.setOnClickListener {
            findNavController().navigate(
                CharacterListFragmentDirections
                    .actionCharacterListFragmentToCharacterFormFragment(
                        characterId = 0L,
                        groupId = args.groupId
                    )
            )
        }

        setupSearch()
    }

    private fun setupSearch() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)
                val searchView = menu.findItem(R.id.action_search).actionView as SearchView
                searchView.queryHint = getString(R.string.search_characters_hint)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setSearchQuery(newText.orEmpty())
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
