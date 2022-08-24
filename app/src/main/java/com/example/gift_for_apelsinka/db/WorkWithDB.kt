package com.example.gift_for_apelsinka.db

import android.content.Context
import com.example.gift_for_apelsinka.db.repo.fieldphoto.FieldPhotoRealization
import com.example.gift_for_apelsinka.db.repo.handbook.HandbookRealization
import com.example.gift_for_apelsinka.db.repo.statement.StatementRealization

lateinit var statementRealization: StatementRealization
lateinit var pictureRealization : FieldPhotoRealization
lateinit var handbookRealization: HandbookRealization

fun initDB(applicationContext: Context) {
    statementRealization = StatementRealization(MyDatabase.getInstance(applicationContext).statementDao())
    handbookRealization = HandbookRealization(MyDatabase.getInstance(applicationContext).handbookDao())
    pictureRealization = FieldPhotoRealization(MyDatabase.getInstance(applicationContext).fieldPhotoDao())
}