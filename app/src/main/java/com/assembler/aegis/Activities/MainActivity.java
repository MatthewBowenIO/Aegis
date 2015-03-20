package com.assembler.aegis.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assembler.aegis.EncryptionProviders.AESEncryptionProvider;
import com.assembler.aegis.SupportingClasses.LibraryHandler;
import com.assembler.aegis.SupportingClasses.PasswordGeneration;
import com.assembler.aegis.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Matthew on 12/28/2014.
 */
public class MainActivity extends ActionBarActivity {
    SharedPreferences prefs;
    AESEncryptionProvider aes;
    LibraryHandler libraryHandler;
    LayoutInflater layoutInflater;
    PasswordGeneration generator;

    Button generatePasswordButton;
    Button copyPasswordToClipboardButton;
    EditText applicationForPassword;
    EditText generatedPassword;

    LinearLayout accountInfoContainer;

    ImageView settingsButton;

    String applicationPassword;
    String[] keyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        applicationPassword = getIntent().getExtras().getString("Password");

        prefs = getSharedPreferences("AegisPrefs", MODE_PRIVATE);
        layoutInflater = (LayoutInflater)getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        libraryHandler = new LibraryHandler();
        generator = new PasswordGeneration();

        try{
            aes = new AESEncryptionProvider(applicationPassword);
        } catch (Exception ex) {
            Log.e("AEGIS", ex.getMessage());
        }

        libraryHandler.setAESProvider(aes);
        libraryHandler.setSharedPreferences(prefs);

        generatePasswordButton = (Button) findViewById(R.id.generatePasswordButton);
        copyPasswordToClipboardButton = (Button) findViewById(R.id.copyButton);

        applicationForPassword = (EditText) findViewById(R.id.applicationForPasswordEditText);
        generatedPassword = (EditText) findViewById(R.id.generatedPasswordEditText);

        accountInfoContainer = (LinearLayout) findViewById(R.id.accountContainerLinearLayout);

        settingsButton = (ImageView) findViewById(R.id.settingsButton);

        generatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!applicationForPassword.getText().toString().equalsIgnoreCase("")) {
                    if(!stringArrayContains()) {
                        try {
                            String password = generator.getRandomPassword();
                            generatedPassword.setText(password);

                            libraryHandler.addAndSaveApplicationAndPassword(applicationForPassword.getText().toString(), password);

                            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.password_row, null);
                            ((TextView) layout.findViewById(R.id.applicationPasswordNameTextView)).setText("Application: " + applicationForPassword.getText().toString());
                            ((TextView) layout.findViewById(R.id.applicationPasswordTextView)).setText("Password: " + password);
                            accountInfoContainer.addView(layout);
                            prefs.edit().putInt("NumberOfPasswords", prefs.getInt("NumberOfPasswords", 0) + 1).commit();
                        } catch (Exception ex) {
                            Log.e("AEGIS", ex.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Already generated password for that application.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter application for password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        copyPasswordToClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("generatedPassword", generatedPassword.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                settingsIntent.putExtra("Password", applicationPassword);
                startActivityForResult(settingsIntent, 0);
            }
        });

        if(!loadApplicationsAndPasswords()){
            Intent launchIntent = new Intent(MainActivity.this, LaunchActivity.class);
            startActivityForResult(launchIntent, 0);
        }
    }

    private boolean loadApplicationsAndPasswords(){
        JSONObject jsonObject = libraryHandler.getApplicationAndPasswordJSONObject();

        if(jsonObject.equals(null))
            return false;

        Iterator keysToCopyIterator = jsonObject.keys();
        List<String> keysList = new ArrayList<String>();
        while(keysToCopyIterator.hasNext()) {
            String key = (String) keysToCopyIterator.next();
            keysList.add(key);
        }

        keyArray = keysList.toArray(new String[keysList.size()]);

        for(int i = 0; i < jsonObject.length(); i++) {
            final LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.password_row, null);
            ((TextView) layout.findViewById(R.id.applicationPasswordNameTextView)).setText("Application: " + keyArray[i]);
            ((TextView) layout.findViewById(R.id.applicationPasswordTextView)).setText("Password: " + jsonObject.optString(keyArray[i]));
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applicationForPassword.setText(((TextView) v.findViewById(R.id.applicationPasswordNameTextView)).getText().toString().split(":")[1].trim());
                    generatedPassword.setText(((TextView) v.findViewById(R.id.applicationPasswordTextView)).getText().toString().split(":")[1].trim());
                }
            });
            accountInfoContainer.addView(layout);
        }
        return true;
    }

    private boolean stringArrayContains(){
        for (String string : keyArray) {
            if(string.equalsIgnoreCase(applicationForPassword.getText().toString())) return true;
        }
        return false;
    }
}
