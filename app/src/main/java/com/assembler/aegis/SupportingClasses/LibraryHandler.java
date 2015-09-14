package com.assembler.aegis.SupportingClasses;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.assembler.aegis.SQLiteHandler.PasswordContract;
import com.assembler.aegis.EncryptionProviders.AESEncryptionProvider;

import org.json.JSONObject;

/**
 * Created by Matthew on 12/28/2014.
 */
public class LibraryHandler {
    private JSONObject mJsonObject = new JSONObject();
    private SharedPreferences prefs;
    private AESEncryptionProvider aes;
    private SQLiteDatabase mDb = null;
    private Context context = null;

    public void addAndSaveApplicationAndPassword(String applicationName, String applicationPassword){
        try {
            mJsonObject.put(applicationName, applicationPassword);
            prefs.edit().putString("AccountsLibrary", aes.encryptAsBase64(mJsonObject.toString())).apply();
        } catch (Exception ex){
            Log.e("AEGIS", ex.getMessage());
        }

        new Runnable() {
            public void run() {
                PasswordContract.PasswordDbHelper db = new PasswordContract.PasswordDbHelper(context);
                mDb = db.getWritableDatabase();
            }
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(PasswordContract.PasswordEntry.COLUMN_NAME_APPLICATION, applicationName);
        contentValues.put(PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORDHASH, applicationPassword);
        mDb.insert(PasswordContract.PasswordEntry.TABLE_NAME, PasswordContract.PasswordEntry.COLUMN_NAME_APPLICATION, contentValues);
    }

    public JSONObject getApplicationAndPasswordJSONObject(){
        try{
            mJsonObject = new JSONObject(aes.decryptAsBase64(prefs.getString("AccountsLibrary", "")));
        } catch (Exception ex){
            Log.e("AEGIS", ex.getMessage());
        }

        return mJsonObject;
    }

    public void saveNewPasswordLibrary(String passwordLibrary) {
        try {
            mJsonObject = new JSONObject(passwordLibrary);
            prefs.edit().putString("AccountsLibrary", aes.encryptAsBase64(mJsonObject.toString())).commit();
        } catch (Exception ex){
            Log.e("AEGIS", ex.getMessage());
        }
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences){
        prefs = sharedPreferences;
    }

    public void setAESProvider(AESEncryptionProvider aesProvider){
        aes = aesProvider;
    }

    public void setApplicationContext(Context c) {context = c;}
}
