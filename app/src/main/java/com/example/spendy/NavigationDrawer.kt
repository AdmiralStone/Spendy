package com.example.spendy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class NavigationDrawer : AppCompatActivity(),Comunicator {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseStore:FirebaseFirestore
    lateinit var firebaseUser:FirebaseUser

    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseArrayList: ArrayList<Expense>
    private lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)

        firebaseStore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.getCurrentUser()!!

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setCheckedItem(R.id.nav_dashboard)

        replaceFragment(DashBoard(),navView.checkedItem?.title.toString())


        navView.setNavigationItemSelectedListener {
            it.isChecked = true
            when(it.itemId){
                R.id.nav_dashboard -> replaceFragment(DashBoard(),it.title.toString())
                R.id.nav_export -> Toast.makeText(applicationContext,"Clicked Export", Toast.LENGTH_SHORT).show()
                R.id.nav_accounts -> Toast.makeText(applicationContext,"Clicked Account", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> logOut()
            }
            true
        }
    }
    private fun replaceFragment(fragment:Fragment,title:String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)

    }

    private fun logOut(){
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun addTransaction() {
        setTitle("Add Transaction")
        val transaction = this.supportFragmentManager.beginTransaction()
        val addFragment = AddTransaction()
        transaction.replace(R.id.frameLayout,addFragment)
        transaction.commit()
    }

    override fun editTransaction(amount: String, type: String, date: String, itemId: String) {
        setTitle("Edit Transaction")
        val bundle = Bundle()
        bundle.putString("expenseId",itemId)
        bundle.putString("amount",amount)
        bundle.putString("category",type)
        bundle.putString("date",date)
        val transaction = this.supportFragmentManager.beginTransaction()
        val editFragment = EditTransaction()
        editFragment.arguments = bundle
        transaction.replace(R.id.frameLayout,editFragment)
        transaction.commit()
    }

    override fun dbAddTransaction(amount: String,type:String,date:String){
        val  id = UUID.randomUUID().toString()
        val data = HashMap<String,Any>()
        data.put("id",id)
        data.put("Amount",amount)
        data.put("Category",type)
        data.put("Date",date)

        firebaseStore.collection("Expenses").document(firebaseUser.uid).collection("Notes").document(id)
            .set(data)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Expense Logged", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            }

    }

    override fun deleteTransaction(itemId: String) {
        Toast.makeText(this,"Delete Item: " + itemId, Toast.LENGTH_SHORT).show()
    }
}