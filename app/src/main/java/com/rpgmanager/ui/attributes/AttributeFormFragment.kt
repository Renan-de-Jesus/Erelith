package com.rpgmanager.ui.attributes

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rpgmanager.R
import com.rpgmanager.data.model.Attribute
import com.rpgmanager.databinding.FragmentAttributeFormBinding
import com.rpgmanager.viewmodel.AttributeViewModel
import kotlinx.coroutines.launch

class AttributeFormFragment : Fragment() {

    private var _binding: FragmentAttributeFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AttributeViewModel by activityViewModels()
    private val args: AttributeFormFragmentArgs by navArgs()

    private var existingAttribute: Attribute? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttributeFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.attributeId != 0L) {
            loadExistingAttribute(args.attributeId)
        }

        // Show/hide value fields based on selected type
        binding.radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            updateValueVisibility(checkedId)
        }

        // Default: NUMBER selected
        updateValueVisibility(R.id.radioNumber)

        binding.btnSave.setOnClickListener { saveAttribute() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun updateValueVisibility(checkedId: Int) {
        binding.layoutNumberValue.visibility =
            if (checkedId == R.id.radioNumber) View.VISIBLE else View.GONE
        binding.layoutTextValue.visibility =
            if (checkedId == R.id.radioText) View.VISIBLE else View.GONE
        binding.layoutBooleanValue.visibility =
            if (checkedId == R.id.radioBoolean) View.VISIBLE else View.GONE
    }

    private fun loadExistingAttribute(id: Long) {
        lifecycleScope.launch {
            viewModel.getAttributeById(id)?.let { attribute ->
                existingAttribute = attribute
                binding.etAttrName.setText(attribute.name)
                binding.etAttrNote.setText(attribute.note)

                when (attribute.attributeType) {
                    "NUMBER" -> {
                        binding.radioNumber.isChecked = true
                        binding.etNumberValue.setText(attribute.value)
                        updateValueVisibility(R.id.radioNumber)
                    }
                    "TEXT" -> {
                        binding.radioText.isChecked = true
                        binding.etTextValue.setText(attribute.value)
                        updateValueVisibility(R.id.radioText)
                    }
                    "BOOLEAN" -> {
                        binding.radioBoolean.isChecked = true
                        binding.checkboxBoolValue.isChecked = attribute.value == "true"
                        updateValueVisibility(R.id.radioBoolean)
                    }
                }
            }
        }
    }

    private fun saveAttribute() {
        val name = binding.etAttrName.text.toString().trim()
        val note = binding.etAttrNote.text.toString().trim()

        if (name.isEmpty()) {
            binding.etAttrName.error = getString(R.string.error_name_required)
            return
        }

        val (type, value) = when (binding.radioGroupType.checkedRadioButtonId) {
            R.id.radioText -> Pair(
                "TEXT",
                binding.etTextValue.text.toString().trim().also {
                    if (it.isEmpty()) {
                        binding.etTextValue.error = getString(R.string.error_value_required)
                        return
                    }
                }
            )
            R.id.radioBoolean -> Pair(
                "BOOLEAN",
                binding.checkboxBoolValue.isChecked.toString()
            )
            else -> {
                val numStr = binding.etNumberValue.text.toString().trim()
                if (numStr.isEmpty()) {
                    binding.etNumberValue.error = getString(R.string.error_value_required)
                    return
                }
                Pair("NUMBER", numStr)
            }
        }

        val attribute = existingAttribute?.copy(
            name = name,
            value = value,
            attributeType = type,
            note = note,
            updatedAt = System.currentTimeMillis()
        ) ?: Attribute(
            characterId = args.characterId,
            name = name,
            value = value,
            attributeType = type,
            note = note
        )

        if (existingAttribute != null) {
            viewModel.update(attribute)
            Toast.makeText(requireContext(), R.string.attribute_updated, Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(attribute)
            Toast.makeText(requireContext(), R.string.attribute_added, Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
