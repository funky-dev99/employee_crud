package com.example.kotlintodopractice.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodopractice.databinding.FragmentHomeBinding
import com.example.kotlintodopractice.utils.adapter.EmployeeAdapter
import com.example.kotlintodopractice.utils.model.EmployeeData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), EmployeeDialogFragment.OnDialogNextBtnClickListener, EmployeeAdapter.EmployeeAdapterInterface {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var frag: EmployeeDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String

    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var employeeItemList: MutableList<EmployeeData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        // get data from firebase
        getEmployeesFromFirebase()

        binding.addTaskBtn.setOnClickListener {
            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = EmployeeDialogFragment()
            frag!!.setListener(this)
            frag!!.show(childFragmentManager, EmployeeDialogFragment.TAG)
        }
    }

    private fun getEmployeesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                employeeItemList.clear()
                for (employeeSnapshot in snapshot.children) {
                    val employee = employeeSnapshot.getValue(EmployeeData::class.java)
                    if (employee != null) {
                        employeeItemList.add(employee)
                    }
                }
                Log.d(TAG, "onDataChange: $employeeItemList")
                // Notify the adapter on the main thread
                activity?.runOnUiThread {
                    employeeAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance("https://kotlin-employee-a8c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("Employees").child(authId)

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        employeeItemList = mutableListOf()
        employeeAdapter = EmployeeAdapter(employeeItemList)
        employeeAdapter.setListener(this)
        binding.mainRecyclerView.adapter = employeeAdapter
    }

    override fun saveEmployee(employee: EmployeeData, todoEdit: TextInputEditText) {
        val employeeId = database.push().key ?: return
        employee.employeeId = employeeId
        database.child(employeeId).setValue(employee).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Employee Added Successfully", Toast.LENGTH_SHORT).show()
                todoEdit.text = null
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        frag!!.dismiss()
    }

    override fun updateEmployee(employee: EmployeeData, todoEdit: TextInputEditText) {
        database.child(employee.employeeId).setValue(employee).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(employeeData: EmployeeData, position: Int) {
        context?.let { context ->
            AlertDialog.Builder(context)
                .setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete this employee?")
                .setPositiveButton("Yes") { dialog, _ ->
                    database.child(employeeData.employeeId).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (position < employeeItemList.size) {
                                employeeItemList.removeAt(position)
                                employeeAdapter.notifyItemRemoved(position)
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error: Invalid position", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onEditItemClicked(employeeData: EmployeeData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()
        frag = EmployeeDialogFragment.newInstance(employeeData.employeeId, employeeData.firstName, employeeData.lastName, employeeData.designation)
        frag!!.setListener(this)
        frag!!.show(childFragmentManager, EmployeeDialogFragment.TAG)
    }
}
