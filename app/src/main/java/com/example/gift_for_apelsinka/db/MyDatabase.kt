package com.example.gift_for_apelsinka.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gift_for_apelsinka.db.dao.FieldPhotoDao
import com.example.gift_for_apelsinka.db.dao.HandbookDao
import com.example.gift_for_apelsinka.db.dao.StatementDao
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.model.Statement

@Database(entities = [FieldPhoto::class, Statement::class, Statement::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun fieldPhotoDao() : FieldPhotoDao
    abstract fun handbookDao() : HandbookDao
    abstract fun statementDao() : StatementDao

    companion object {
        private var database : MyDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : MyDatabase {
            return if(database == null) {
                database = Room.databaseBuilder(context, MyDatabase::class.java, "db")
                    .build()
                database as MyDatabase
            }
            else {
                database as MyDatabase
            }
        }
    }
}