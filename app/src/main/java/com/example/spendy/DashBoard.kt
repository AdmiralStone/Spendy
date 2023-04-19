package com.example.spendy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashBoard.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashBoard : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
    private lateinit var comunicator: Comunicator
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseArrayList: ArrayList<Expense>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private  lateinit var firebaseAuth:FirebaseAuth
    private lateinit var totalExp:TextView


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.getCurrentUser()!!
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dash_board, container, false)
        val btn:View = view.findViewById(R.id.add_tran_btn)

        totalExp= view.findViewById(R.id.totalExpenseAmount)

        recyclerView = view.findViewById(R.id.history_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(DashBoard().context)
        recyclerView.setHasFixedSize(true)

        expenseArrayList = arrayListOf()
        myAdapter = MyAdapter(expenseArrayList)
        recyclerView.adapter = myAdapter

        EventChangeListener()

        comunicator = activity as Comunicator

        btn.setOnClickListener {
          comunicator.addTransaction()
        }

        myAdapter.setOnItemClickListener(object :MyAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {

                val itemId = expenseArrayList[position].id.toString()
                val amount = expenseArrayList[position].Amount.toString()
                val category = expenseArrayList[position].Category.toString()
                val date = expenseArrayList[position].Date.toString()
                comunicator.editTransaction(amount,category,date,itemId)
            }
        })

        return view
    }

    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("Expenses").document(firebaseUser.uid).collection("ExpenseLog").orderBy("Date")
            .addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ){
                    if(error != null){
                        Log.e("Firestore Error",error.message.toString())
                        return
                    }

                    for(dc:DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            expenseArrayList.add(dc.document.toObject(Expense::class.java))
                        }
                    }
                    var totalExpense = 0
                    for(record in expenseArrayList){
                        totalExpense+= record.Amount?.toInt() ?: 0
                    }

                    totalExp.setText(totalExpense.toString())

                    myAdapter.notifyDataSetChanged()

                }
            })

    }

}