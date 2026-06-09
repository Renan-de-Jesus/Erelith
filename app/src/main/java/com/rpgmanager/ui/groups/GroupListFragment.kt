package com.rpgmanager.ui.groups

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.rpgmanager.R
import com.rpgmanager.databinding.FragmentGroupListBinding
import com.rpgmanager.viewmodel.GroupViewModel

class GroupListFragment : Fragment() {

    private var _binding: FragmentGroupListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GroupViewModel by activityViewModels()
    private lateinit var adapter: GroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupAdapter(
            onItemClick = { group ->
                val action = GroupListFragmentDirections
                    .actionGroupListFragmentToCharacterListFragment(group.id, group.name)
                findNavController().navigate(action)
            },
            onEditClick = { group ->
                val action = GroupListFragmentDirections
                    .actionGroupListFragmentToGroupFormFragment(group.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { group ->
                DeleteConfirmDialog(
                    title = getString(R.string.delete_group_title),
                    message = getString(R.string.delete_group_message, group.name)
                ) {
                    viewModel.delete(group)
                }.show(childFragmentManager, "DeleteGroup")
            }
        )

        binding.recyclerGroups.adapter = adapter

        viewModel.filteredGroups.observe(viewLifecycleOwner) { groups ->
            adapter.submitList(groups)
            binding.emptyState.visibility =
                if (groups.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddGroup.setOnClickListener {
            findNavController().navigate(
                GroupListFragmentDirections.actionGroupListFragmentToGroupFormFragment(0L)
            )
        }

        setupSearch()
    }

    private fun setupSearch() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = getString(R.string.search_groups_hint)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setSearchQuery(newText.orEmpty())
                        return true
                    }
                })
            }
            override fun onMenuItemSelected(menuItem: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
