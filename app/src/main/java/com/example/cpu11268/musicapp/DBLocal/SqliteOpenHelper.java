package com.example.cpu11268.musicapp.DBLocal;

import android.content.Context;


import org.greenrobot.greendao.database.Database;

public class SqliteOpenHelper extends DaoMaster.DevOpenHelper {

    public SqliteOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
