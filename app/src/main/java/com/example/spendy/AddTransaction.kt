package com.example.spendy

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.spendy.databinding.FragmentAddTransactionBinding
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddTransaction.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTransaction : Fragment() {
    private lateinit var _binding: FragmentAddTransactionBinding
    private val binding get() = _binding
    private lateinit var firebaseStore: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private  lateinit var firebaseAuth:FirebaseAuth
    private  lateinit var comunicator: Comunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        firebaseStore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.getCurrentUser()!!

        comunicator = activity as Comunicator

        val myCalender = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateLable(myCalender)

        }

        binding.date.setOnClickListener{
            DatePickerDialog(it.context,datePicker, myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.submitBtn.setOnClickListener{

            val amount = binding.addAmount.getText().toString().trim()
            val category = binding.addCategory.getText().toString().trim()

            val  id = UUID.randomUUID().toString()
            val data = HashMap<String,Any>()
            data.put("id",id)
            data.put("Amount",amount)
            data.put("Category",category)
            data.put("Date",SimpleDateFormat("MM-dd-yyyy", Locale.US).format(myCalender.time))
            var context = it.context

            firebaseStore.collection("Expenses").document(firebaseUser.uid).collection("ExpenseLog").document(id)
                .set(data)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(context,"Expense Logged", Toast.LENGTH_SHORT).show()
                        comunicator.replaceFragment(DashBoard(),"Dashboard")

                    }else{
                        Toast.makeText(context,"Something Went Wrong", Toast.LENGTH_SHORT).show()
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
