package com.example.gift_for_apelsinka.db.repo.statement

import androidx.lifecycle.LiveData
import com.example.gift_for_apelsinka.db.dao.StatementDao
import com.example.gift_for_apelsinka.db.model.Statements

class StatementRealization(private val statementDao: StatementDao) : StatementRepository {
    override val allStatements: List<Statements>
        get() = statementDao.readAllStatements()

    override suspend fun insertStatement(statement: Statements) {
        statementDao.insertStatement(statement)
    }

    override suspend fun clearStatement() {
        statementDao.deleteAll()
    }
}