package com.xiaoyv.bangumi.shared.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.xiaoyv.bangumi.shared.native.AppDatabase
import java.util.Properties

actual object DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver("jdbc:sqlite:bgm.db", Properties(), AppDatabase.Schema)
    }
}