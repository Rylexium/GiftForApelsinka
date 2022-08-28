package com.example.gift_for_apelsinka.db

import android.content.Context
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.db.model.Statements
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

suspend fun saveStatementsToDB(list : List<Statements>) {
    for(item in list)
        statementRealization.insertStatement(item)
}
suspend fun deleteStatementsFromDB() {
    statementRealization.clearStatement()
}
suspend fun savePicturesToDB(list : List<FieldPhoto>) {
    for (item in list)
        pictureRealization.insertFieldPhoto(FieldPhoto(item.id, item.picture, if(item.title == null) "" else item.title, item.belongs))
}
suspend fun deletePicturesApelsinkaFromDB() {
    pictureRealization.deleteApelsinkaPicture()
}

suspend fun deletePicturesOscarFromDB() {
    pictureRealization.deleteOscarPicture()
}

suspend fun deletePicturesLeraFromDB() {
    pictureRealization.deleteLeraPicture()
}

suspend fun deletePicturesRylexiumFromDB() {
    pictureRealization.deleteRylexiumPicture()
}

suspend fun deletePicturesMainFromDB() {
    pictureRealization.deleteMainPicture()
}

suspend fun deletePicturesLogoFromDB() {
    pictureRealization.deleteLogoPicture()
}
suspend fun deleteAll() {
    pictureRealization.deleteAll()
}
suspend fun saveHandbookToDB(map : MutableMap<String, String>) {
    for(item in map)
        handbookRealization.insertHandbook(Handbook(item.key, item.value))
}