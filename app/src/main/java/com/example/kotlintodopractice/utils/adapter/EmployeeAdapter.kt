package com.example.kotlintodopractice.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodopractice.databinding.EachEmployeeItemBinding
import com.example.kotlintodopractice.utils.model.EmployeeData

class EmployeeAdapter(private val list: MutableList<EmployeeData>) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    private var listener: EmployeeAdapterInterface? = null

    fun setListener(listener: EmployeeAdapterInterface) {
        this.listener = listener
    }

    class EmployeeViewHolder(val binding: EachEmployeeItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding =
            EachEmployeeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.employeeName.text = "${this.firstName} ${this.lastName}"
                binding.employeeDesignation.text = this.designation

                binding.editEmployee.setOnClickListener {
                    listener?.onEditItemClicked(this, position)
                }

                binding.deleteEmployee.setOnClickListener {
                    listener?.onDeleteItemClicked(this, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface EmployeeAdapterInterface {
        fun onDeleteItemClicked(employeeData: EmployeeData, position: Int)
        fun onEditItemClicked(employeeData: EmployeeData, position: Int)
    }
}
