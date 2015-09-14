package com.assembler.aegis.SQLiteHandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by matthewbowen on 8/31/15.
 */
public class PasswordContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PasswordEntry.TABLE_NAME + " (" +
                    PasswordEntry._ID + " INTEGER PRIMARY KEY," +
                    PasswordEntry.COLUMN_NAME_APPLICATION + TEXT_TYPE + COMMA_SEP +
                    PasswordEntry.COLUMN_NAME_PASSWORDHASH + TEXT_TYPE + COMMA_SEP + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PasswordEntry.TABLE_NAME;

    public static abstract class PasswordEntry implements BaseColumns {
        public static final String TABLE_NAME = "Library";
        public static final String COLUMN_NAME_APPLICATION = "application";
        public static final String COLUMN_NAME_PASSWORDHASH = "passwordhash";
    }

    public static class PasswordDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "PLib.db";

        public PasswordDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
