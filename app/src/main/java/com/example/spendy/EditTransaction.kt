package com.example.spendy

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.spendy.databinding.FragmentAddTransactionBinding
import com.example.spendy.databinding.FragmentEditTransactionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditTransaction.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditTransaction : Fragment() {
    private lateinit var _binding: FragmentEditTransactionBinding
    private val binding get() = _binding
    private lateinit var firebaseStore: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private  lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _binding = FragmentEditTransactionBinding.inflate(inflater, container, false)
        firebaseStore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.getCurrentUser()!!

        val args = this.arguments
        val amount = args?.get("amount")
        val category = args?.get("category")
        val date = args?.get("date")
        val expenseId = args?.get("expenseId")

        binding.amount.setText(amount.toString())
        binding.category.setText(category.toString())
        binding.date.setText(date.toString())

        val myCalender = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateLable(myCalender)

        }

        binding.date.setOnClickListener{
            DatePickerDialog(it.context,datePicker, myCalender.get(Calendar.YEAR),myCalender.get(
                Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.logUpdateBtn.setOnClickListener{

            val newAmount = binding.amount.getText().toString().trim()
            val newCategory = binding.category.getText().toString().trim()

            val data = HashMap<String,Any>()
            data.put("Amount",newAmount)
            data.put("Category",newCategory)
            data.put("Date", SimpleDateFormat("MM-dd-yyyy", Locale.US).format(myCalender.time))
            var context = it.context

            firebaseStore.collection("Expenses").document(firebaseUser.uid).collection("ExpenseLog").document(expenseId.toString())
                .update(data)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(context,"Expense Updated", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"Something Went Wrong While Updating Expense", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        binding.logDeleteBtn.setOnClickListener{
            firebaseStore.collection("Expenses").document(firebaseUser.uid).collection("ExpenseLog").document(expenseId.toString())
                .delete()
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(context,"Expense Deleted", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"Something Went Wrong Wile Deleting Expense", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return binding.root
    }

    private fun updateLable(myCalendar: Calendar){
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.date.setText(sdf.format(myCalendar.time))
    }



    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment EditTransaction.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            EditTransaction().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
