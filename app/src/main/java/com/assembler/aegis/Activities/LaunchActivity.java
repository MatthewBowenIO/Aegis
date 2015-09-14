package com.assembler.aegis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.assembler.aegis.SupportingClasses.HashClass;
import com.assembler.aegis.R;

/**
 * Created by Matthew on 12/28/2014.
 */
public class LaunchActivity extends ActionBarActivity {
    EditText applicationPassword;
    Button acceptPasswordAndApplication;

    SharedPreferences prefs;

    HashClass hashClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.launch_activity);

        hashClass = new HashClass();
        prefs = getSharedPreferences("AegisPrefs", MODE_PRIVATE);
        hashClass.setSharedPreferences(prefs);

        applicationPassword = (EditText) findViewById(R.id.passwordEditText);
        acceptPasswordAndApplication = (Button) findViewById(R.id.acceptPasswordButton);

        acceptPasswordAndApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!prefs.getBoolean("PasswordCreated", false) && (applicationPassword.getText().toString().equalsIgnoreCase("") || applicationPassword.getText().toString().length() < 16)){
                        createDialog("You must entire a 16 character password.", null);
                    } else {
                        if (!prefs.getBoolean("PasswordCreated", false)) {
                            createDialog("Please write this password down. If you forget it you will no longer be able to access your passwords.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try{
                                        hashClass.getSecurePassword(applicationPassword.getText().toString(), hashClass.getSalt(), false, 1000);
                                        prefs.edit().putBoolean("PasswordCreated", true).commit();
                                        sendToMainActivity();
                                    } catch (Exception ex) {
                                        Log.e("Aegis", ex.getMessage());
                                    }
                                }
                            });
                        } else {
                            if(hashClass.getSecurePassword(applicationPassword.getText().toString(), prefs.getString("PWSalt", ""), false, 1000).equalsIgnoreCase(prefs.getString("PWHash", ""))) {
                                sendToMainActivity();
                            } else {
                                createDialog("Nope", null);
                            }
                        }
                    }
                } catch(Exception ex) {
                    Log.e("AEGIS", ex.getMessage());
                }
            }
        });

        if(prefs.getBoolean("PasswordCreated", false)){
            acceptPasswordAndApplication.setText("Login");
        }
    }

    private void sendToMainActivity() {
        Intent settingsIntent = new Intent(LaunchActivity.this, MainActivity.class);
        settingsIntent.putExtra("Password", applicationPassword.getText().toString());
        startActivityForResult(settingsIntent, 0);
        applicationPassword.setText("");
    }

    private void createDialog(String dialogText, DialogInterface.OnClickListener listener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(LaunchActivity.this);
        dialog.setMessage(dialogText);
        dialog.setPositiveButton("OK", listener);

        dialog.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_launch_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
