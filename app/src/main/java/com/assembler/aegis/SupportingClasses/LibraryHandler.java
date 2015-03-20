package com.assembler.aegis.SupportingClasses;

import android.content.SharedPreferences;
import android.util.Log;

import com.assembler.aegis.EncryptionProviders.AESEncryptionProvider;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Matthew on 12/28/2014.
 */
public class LibraryHandler {
    private JSONObject mJsonObject = new JSONObject();
    private SharedPreferences prefs;
    private AESEncryptionProvider aes;

    public void addAndSaveApplicationAndPassword(String applicationName, String applicationPassword){
        try {
            mJsonObject.put(applicationName, applicationPassword);
            prefs.edit().putString("AccountsLibrary", aes.encryptAsBase64(mJsonObject.toString())).commit();
        } catch (Exception ex){
            Log.e("AEGIS", ex.getMessage());
        }
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
}
