package com.xiaoyv.bangumi.shared.libnative.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.xiaoyv.bangumi.shared.libnative.application
import com.xiaoyv.bangumi.shared.native.AppDatabase

actual object DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, application, "bgm.db")
    }
}