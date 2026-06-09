package com.rpgmanager.ui.groups

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
import com.rpgmanager.data.model.Group
import com.rpgmanager.databinding.FragmentGroupFormBinding
import com.rpgmanager.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.io.File

class GroupFormFragment : Fragment() {

    private var _binding: FragmentGroupFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GroupViewModel by activityViewModels()
    private val args: GroupFormFragmentArgs by navArgs()

    private var existingGroup: Group? = null
    private var selectedImagePath: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImagePath = getRealPathFromUri(uri)
                Glide.with(this).load(uri).circleCrop().into(binding.ivGroupImage)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.groupId != 0L) {
            loadExistingGroup(args.groupId)
        }

        binding.ivGroupImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener { saveGroup() }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadExistingGroup(id: Long) {
        lifecycleScope.launch {
            viewModel.getGroupById(id)?.let { group ->
                existingGroup = group
                binding.etGroupName.setText(group.name)
                binding.etMasterName.setText(group.masterName)
                binding.etDescription.setText(group.description)
                binding.checkboxActive.isChecked = group.isActive
                selectedImagePath = group.imagePath
                if (!group.imagePath.isNullOrEmpty()) {
                    Glide.with(this@GroupFormFragment)
                        .load(File(group.imagePath))
                        .circleCrop()
                        .into(binding.ivGroupImage)
                }
            }
        }
    }

    private fun saveGroup() {
        val name = binding.etGroupName.text.toString().trim()
        val master = binding.etMasterName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val isActive = binding.checkboxActive.isChecked

        // Validation
        if (name.isEmpty()) {
            binding.etGroupName.error = getString(R.string.error_name_required)
            return
        }
        if (master.isEmpty()) {
            binding.etMasterName.error = getString(R.string.error_master_required)
            return
        }

        val group = existingGroup?.copy(
            name = name,
            masterName = master,
            description = description,
            isActive = isActive,
            imagePath = selectedImagePath
        ) ?: Group(
            name = name,
            masterName = master,
            description = description,
            isActive = isActive,
            imagePath = selectedImagePath
        )

        if (existingGroup != null) {
            viewModel.update(group)
            Toast.makeText(requireContext(), R.string.group_updated, Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(group)
            Toast.makeText(requireContext(), R.string.group_created, Toast.LENGTH_SHORT).show()
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
        } catch (e: Exception) {
            uri.path
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
