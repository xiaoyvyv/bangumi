package com.xiaoyv.bangumi.shared.database

import app.cash.sqldelight.db.SqlDriver

expect object DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}