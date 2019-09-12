package com.plantplanet.android.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.plantplanet.android.R
import com.plantplanet.android.models.History

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "History"
        private const val TABLE_NAME = "History"
        private const val ID = "ID"
        private const val DISEASE = "Disease"
        private const val PERCENTAGE = "Percentage"
        private const val IMAGE = "Image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,$DISEASE TEXT,$PERCENTAGE TEXT,$IMAGE BLOB);"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS $TABLE_NAME"
        db!!.execSQL(dropTable)
        onCreate(db)
    }

    fun getHistory(): ArrayList<History> {
        val historyList = ArrayList<History>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val history = History(
                    historyId = cursor.getInt(cursor.getColumnIndex(ID)),
                    historyDisease = cursor.getString(cursor.getColumnIndex(DISEASE)),
                    historyPercentage = cursor.getString(cursor.getColumnIndex(PERCENTAGE)),
                    historyImage = cursor.getString(cursor.getColumnIndex(IMAGE))
                )
                historyList.add(history)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return historyList
    }

    fun addHistory(history: History) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DISEASE, history.historyDisease)
        values.put(PERCENTAGE, history.historyPercentage)
        values.put(IMAGE, history.historyImage)
        db.insert(TABLE_NAME, null, values)
        db.close()
        Toast.makeText(context, context.getString(R.string.saved_successfully), Toast.LENGTH_LONG).show()
    }

    fun updateHistory(history: History): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DISEASE, history.historyDisease)
        values.put(PERCENTAGE, history.historyPercentage)
        return db.update(TABLE_NAME, values, "$PERCENTAGE=?", arrayOf(history.historyPercentage))
    }

    fun deleteHistory(history: History) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$ID=?", arrayOf(history.historyId.toString()))
        db.close()
        Toast.makeText(context, context.getString(R.string.deleted_successfully), Toast.LENGTH_LONG).show()
    }

    fun clearHistory() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
        Toast.makeText(context, context.getString(R.string.deleted_successfully), Toast.LENGTH_LONG).show()
    }
}