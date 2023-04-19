package com.example.spendy

import androidx.browser.browseractions.BrowserActionsIntent.BrowserActionsItemId
import androidx.fragment.app.Fragment

interface Comunicator {
    fun editTransaction(amount: String, type:String, date: String,itemId: String)
    fun addTransaction()
    fun dbAddTransaction(amount: String,type:String,date:String)
    fun deleteTransaction(itemId: String)
    fun replaceFragment(fragment: Fragment, title:String)

}