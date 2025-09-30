package com.xiaoyv.bangumi.shared.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.xiaoyv.bangumi.shared.native.AppDatabase

actual object DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "bgm.db")
    }
}