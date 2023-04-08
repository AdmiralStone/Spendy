package com.example.spendy

import androidx.browser.browseractions.BrowserActionsIntent.BrowserActionsItemId

interface Comunicator {
    fun editTransaction(amount: String, type:String, date: String,itemId: String)
    fun addTransaction()
    fun dbAddTransaction(amount: String,type:String,date:String)
    fun deleteTransaction(itemId: String)
}