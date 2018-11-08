package com.example.cpu11268.musicapp.DBLocal;

import android.content.Context;


import org.greenrobot.greendao.database.Database;

public class LocalDatabaseHelper {
    private static LocalDatabaseHelper localDatabaseHelper;

    private DaoSession daoSession;
    private Database database;


    private LocalDatabaseHelper(Context context) {
        DaoMaster.DevOpenHelper helper =
                new SqliteOpenHelper(context.getApplicationContext(), "lockscreen-db");
        database = helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();
    }

    public static LocalDatabaseHelper getInstance(Context context) {
        if (localDatabaseHelper == null) {
            init(context);
        }
        return localDatabaseHelper;
    }

    public static void init(Context context) {
        if (localDatabaseHelper == null) {
            localDatabaseHelper = new LocalDatabaseHelper(context);
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Database getDatabase() {
        return database;
    }
}
