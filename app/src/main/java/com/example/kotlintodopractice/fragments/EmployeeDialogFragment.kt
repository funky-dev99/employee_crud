package com.example.kotlintodopractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlintodopractice.databinding.FragmentEmployeeDialogBinding
import com.example.kotlintodopractice.utils.model.EmployeeData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class EmployeeDialogFragment : BottomSheetDialogFragment() {

    private var binding: FragmentEmployeeDialogBinding? = null
    private var listener: OnDialogNextBtnClickListener? = null

    private var employeeId: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var designation: String? = null

    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            employeeId = it.getString("employeeId")
            firstName = it.getString("firstName")
            lastName = it.getString("lastName")
            designation = it.getString("designation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            if (employeeId != null) {
                firstNameInput.setText(firstName)
                lastNameInput.setText(lastName)
                designationInput.setText(designation)
                dialogTitle.text = "Edit Employee"
                addBtn.text = "Update"
            }

            addBtn.setOnClickListener {
                val fName = firstNameInput.text.toString().trim()
                val lName = lastNameInput.text.toString().trim()
                val desig = designationInput.text.toString().trim()

                var isError = false

                if (fName.isEmpty()) {
                    firstNameInput.error = "First name required"
                    isError = true
                } else {
                    firstNameInput.error = null
                }

                if (lName.isEmpty()) {
                    lastNameInput.error = "Last name required"
                    isError = true
                } else {
                    lastNameInput.error = null
                }

                if (desig.isEmpty()) {
                    designationInput.error = "Designation required"
                    isError = true
                } else {
                    designationInput.error = null
                }

                if (containsNumbers(fName)) {
                    firstNameInput.error = "First name cannot contain numbers"
                    isError = true
                }

                if (containsNumbers(lName)) {
                    lastNameInput.error = "Last name cannot contain numbers"
                    isError = true
                }

                if (containsNumbers(desig)) {
                    designationInput.error = "Designation cannot contain numbers"
                    isError = true
                }

                if (!isError) {
                    if (employeeId == null) {
                        val employee = EmployeeData("", fName, lName, desig)
                        listener?.saveEmployee(employee, firstNameInput)
                    } else {
                        val employee = EmployeeData(employeeId!!, fName, lName, desig)
                        listener?.updateEmployee(employee, firstNameInput)
                    }
                }
            }

            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun containsNumbers(text: String): Boolean {
        return text.any { it.isDigit() }
    }


    interface OnDialogNextBtnClickListener {
        fun saveEmployee(employee: EmployeeData, todoEdit: TextInputEditText)
        fun updateEmployee(employee: EmployeeData, todoEdit: TextInputEditText)
    }

    companion object {
        const val TAG = "EmployeeDialogFragment"

        @JvmStatic
        fun newInstance(employeeId: String, firstName: String, lastName: String, designation: String) =
            EmployeeDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("employeeId", employeeId)
                    putString("firstName", firstName)
                    putString("lastName", lastName)
                    putString("designation", designation)
                }
            }
    }
}
