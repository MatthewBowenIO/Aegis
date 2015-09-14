package com.assembler.aegis.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.assembler.aegis.EncryptionProviders.AESEncryptionProvider;
import com.assembler.aegis.SupportingClasses.HashClass;
import com.assembler.aegis.SupportingClasses.LibraryHandler;
import com.assembler.aegis.R;

import java.io.FileOutputStream;

/**
 * Created by Matthew on 12/28/2014.
 */
public class SettingsActivity extends ActionBarActivity {
    SharedPreferences prefs;
    AESEncryptionProvider aes;
    LibraryHandler libraryHandler;
    HashClass hashClass;
    LayoutInflater layoutInflater;

    Button exportLibrary;
    Button importLibrary;
    Button clearLibrary;
    Button acceptPasswordChange;

    EditText oldPassword;
    EditText newPassword;

    String applicationPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        applicationPassword = getIntent().getExtras().getString("Password");

        prefs = getSharedPreferences("AegisPrefs", MODE_PRIVATE);
        layoutInflater = (LayoutInflater)getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        libraryHandler = new LibraryHandler();
        hashClass = new HashClass();

        try{
            aes = new AESEncryptionProvider(applicationPassword);
        } catch (Exception ex) {
            Log.e("AEGIS", ex.getMessage());
        }

        libraryHandler.setAESProvider(aes);
        libraryHandler.setSharedPreferences(prefs);
        hashClass.setSharedPreferences(prefs);

        exportLibrary = (Button) findViewById(R.id.exportLibrary);
        importLibrary = (Button) findViewById(R.id.importLibrary);
        clearLibrary = (Button) findViewById(R.id.clearLibrary);
        acceptPasswordChange = (Button) findViewById(R.id.acceptNewPassword);
        oldPassword = (EditText) findViewById(R.id.oldPasswordEditText);
        newPassword = (EditText) findViewById(R.id.newPasswordEditText);

        exportLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream outputStream = openFileOutput("AegisExport", Context.MODE_PRIVATE);
                    outputStream.write(libraryHandler.getApplicationAndPasswordJSONObject().toString().getBytes());
                    outputStream.close();
                    Toast.makeText(getApplicationContext(), "File: AegisExport saved to " + getApplicationInfo().dataDir, Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Log.e("AEGIS", ex.getMessage());
                }
            }
        });

        importLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 1);
            }
        });

        clearLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog("Are you sure? All of your password will be lost.");
            }
        });

        acceptPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(hashClass.getSecurePassword(oldPassword.getText().toString(), prefs.getString("PWSalt", ""), false, 1000).equalsIgnoreCase(prefs.getString("PWHash", ""))) {
                        String jsonObject = libraryHandler.getApplicationAndPasswordJSONObject().toString();
                        aes = new AESEncryptionProvider(newPassword.getText().toString());
                        libraryHandler.setAESProvider(aes);
                        libraryHandler.saveNewPasswordLibrary(jsonObject);
                        hashClass.getSecurePassword(newPassword.getText().toString(), prefs.getString("PWSalt", ""), true, 1000);
                        Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }

                    oldPassword.setText("");
                    newPassword.setText("");
                } catch (Exception ex) {
                    Log.e("AEGIS", ex.getMessage());
                }
            }
        });
    }

    private void createDialog(String dialogText){
        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
        dialog.setMessage(dialogText);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putString("AccountsLibrary", "").commit();
                Toast.makeText(getApplicationContext(), "Okay... It's done.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = data.getDataString();

        super.onActivityResult(requestCode, resultCode, data);
    }
}
