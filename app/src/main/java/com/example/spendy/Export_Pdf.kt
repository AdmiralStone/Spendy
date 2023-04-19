package com.example.spendy


import android.app.DatePickerDialog
import android.content.pm.PackageManager

import android.os.Bundle
import android.os.Environment
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.spendy.databinding.FragmentExportPdfBinding
import com.google.common.collect.Table
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.fonts.otf.TableHeader
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [Export_Pdf.newInstance] factory method to
 * create an instance of this fragment.
 */
class Export_Pdf : Fragment() {
    private lateinit var _binding: FragmentExportPdfBinding
    private val binding get() = _binding
    private lateinit var firebaseStore: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private  lateinit var firebaseAuth:FirebaseAuth
    private lateinit var expenseArrayList: ArrayList<Expense>
    private val STORAGE_CODE = 100
    var startDate:String?=null
    var endDate:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportPdfBinding.inflate(inflater, container, false)
        firebaseStore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.getCurrentUser()!!
        expenseArrayList = arrayListOf()



        // Inflate the layout for this fragment
        val exportStartDate = Calendar.getInstance()

        val startDatePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            exportStartDate.set(Calendar.YEAR,year)
            exportStartDate.set(Calendar.MONTH,month)
            exportStartDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateStartDateLable(exportStartDate)

        }
        binding.exportStartDate.setOnClickListener{
            DatePickerDialog(it.context,startDatePicker, exportStartDate.get(Calendar.YEAR),exportStartDate.get(Calendar.MONTH),exportStartDate.get(Calendar.DAY_OF_MONTH)).show()
        }


        val exportEndDate = Calendar.getInstance()

        val endDatePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            exportEndDate.set(Calendar.YEAR,year)
            exportEndDate.set(Calendar.MONTH,month)
            exportEndDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateEndDateLable(exportEndDate)

        }
        binding.exportEndDate.setOnClickListener{
            DatePickerDialog(it.context,endDatePicker, exportEndDate.get(Calendar.YEAR),exportEndDate.get(Calendar.MONTH),exportEndDate.get(Calendar.DAY_OF_MONTH)).show()
        }


        binding.generateBtn.setOnClickListener {
            val data = getDataFromDB()

            savePDF(data)
        }


        return binding.root
    }

    private fun updateEndDateLable(exportEndDate: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        endDate = sdf.format(exportEndDate.time)
        binding.exportEndDate.setText(sdf.format(exportEndDate.time))
    }

    private fun updateStartDateLable(exportStartDate: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        startDate = sdf.format(exportStartDate.time)
        binding.exportStartDate.setText(sdf.format(exportStartDate.time))
    }

    private fun getDataFromDB(): List<List<String>> {
        firebaseStore = FirebaseFirestore.getInstance()
        var queryRef = firebaseStore.collection("Expenses").document(firebaseUser.uid).collection("ExpenseLog").orderBy("Date")
        val typetext = binding.category.getText().toString().trim()

        if(typetext.isNotEmpty()){
            queryRef = firebaseStore.collection("Expenses").document(firebaseUser.uid).collection("ExpenseLog").orderBy("Date").whereEqualTo("Category",typetext)
        }
        val data = mutableListOf<List<String>>()
        data.add(0, listOf("SrNo.","Category","Date","Amount"))
        if(startDate == null && endDate == null){
            queryRef
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ){
                        if(error != null){
                            Log.e("Error fetching data from DB",error.message.toString())
                            return
                        }

                        for(dc: DocumentChange in value?.documentChanges!!){
                            if(dc.type == DocumentChange.Type.ADDED){
                                expenseArrayList.add(dc.document.toObject(Expense::class.java))
                            }
                        }
                    }
                })

        }else if(startDate != null && endDate == null){
                queryRef.whereGreaterThan("Date",
                    startDate!!
                )
                    .orderBy("Date")
                    .addSnapshotListener(object : EventListener<QuerySnapshot> {
                        override fun onEvent(
                            value: QuerySnapshot?,
                            error: FirebaseFirestoreException?
                        ){
                            if(error != null){
                                Log.e("Error fetching data from DB",error.message.toString())
                                return
                            }

                            for(dc: DocumentChange in value?.documentChanges!!){
                                if(dc.type == DocumentChange.Type.ADDED){
                                    expenseArrayList.add(dc.document.toObject(Expense::class.java))
                                }
                            }
                        }
                    })
            }else if(startDate == null && endDate!=null){
                queryRef
                    .whereLessThanOrEqualTo("Date", endDate!!)
                    .orderBy("Date")
                    .addSnapshotListener(object : EventListener<QuerySnapshot> {
                        override fun onEvent(
                            value: QuerySnapshot?,
                            error: FirebaseFirestoreException?
                        ){
                            if(error != null){
                                Log.e("Error fetching data from DB",error.message.toString())
                                return
                            }

                            for(dc: DocumentChange in value?.documentChanges!!){
                                if(dc.type == DocumentChange.Type.ADDED){
                                    expenseArrayList.add(dc.document.toObject(Expense::class.java))
                                }
                            }
                        }
                    })
            }else{
                queryRef
                    .whereGreaterThan("Date", startDate.toString())
                    .whereLessThanOrEqualTo("Date", endDate.toString())
                    .orderBy("Date")
                    .addSnapshotListener(object : EventListener<QuerySnapshot> {
                        override fun onEvent(
                            value: QuerySnapshot?,
                            error: FirebaseFirestoreException?
                        ){
                            if(error != null){
                                Log.e("Error fetching data from DB",error.message.toString())
                                return
                            }

                            for(dc: DocumentChange in value?.documentChanges!!){
                                if(dc.type == DocumentChange.Type.ADDED){
                                    expenseArrayList.add(dc.document.toObject(Expense::class.java))
                                }
                            }
                        }
                    })
            }

        var srNo = 0
        for(record in expenseArrayList){
            val temp = mutableListOf<String>()
            temp.add(srNo.toString())
            record.Category?.let { temp.add(it) }
            record.Date?.let { temp.add(it) }
            record.Amount?.let { temp.add(it) }
            data.add(temp)
            srNo+=1
        }

        return data
    }

    private fun savePDF(data: List<List<String>> ) {
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyMMdd_HHmm", Locale.getDefault())
            .format(System.currentTimeMillis())

        val mFilePath = this.context?.getExternalFilesDir(String.toString()).toString() + "/" + mFileName + ".pdf"
        try{
            PdfWriter.getInstance(mDoc,FileOutputStream(mFilePath))
            mDoc.open()
            mDoc.addAuthor("SPENDY")
            val table = PdfPTable(data[0].size)
            for (row in data) {
                for (cell in row) {
                    table.addCell(cell)
                }
            }
            mDoc.add(table)
            mDoc.close()
            Toast.makeText(this.context,"$mFileName.pdf saved.",Toast.LENGTH_SHORT).show()
        }catch(e:java.lang.Exception){
            println(e.message)
            Toast.makeText(this.context,"${e.message}",Toast.LENGTH_SHORT).show()
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when(requestCode){
//            STORAGE_CODE -> {
//                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                }else{
//                    Toast.makeText(this.context,"Permission Denied",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

}