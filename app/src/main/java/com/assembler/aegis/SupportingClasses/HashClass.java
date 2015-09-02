package com.assembler.aegis.SupportingClasses;

import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/**
 * Created by Matthew on 2/7/2015.
 */
public class HashClass {
    private SharedPreferences prefs;


    public String getSecurePassword(String passwordToHash, String salt, boolean newPassword, int numberofItterations) throws NoSuchAlgorithmException {
        String generatedPassword = null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(salt.getBytes());
        byte[] bytes = md.digest(passwordToHash.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        generatedPassword = sb.toString();

        if (!prefs.getBoolean("PasswordCreated", false)) {
            prefs.edit().putString("PWHash", generatedPassword).commit();
            prefs.edit().putString("PWSalt", salt).commit();
        }

        if(numberofItterations > 0){
            numberofItterations--;
            getSecurePassword(generatedPassword, salt, false, numberofItterations);
        } else {
            prefs.edit().putString("PWHash", generatedPassword);
        }

        return generatedPassword;
    }

    public String getSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences){
        prefs = sharedPreferences;
    }
}
