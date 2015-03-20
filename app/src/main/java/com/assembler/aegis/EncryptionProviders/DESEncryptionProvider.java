package com.assembler.aegis.EncryptionProviders;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import android.util.Log;
import com.assembler.aegis.SupportingClasses.Base64;

/**
 * Created by Matthew on 12/28/2014.
 */
public class DESEncryptionProvider extends IStringEncryptor {
    Cipher ecipher;
    Cipher dcipher;
    byte[] salt = { 72, 51, 82, 112, 28, 27, 87, 115 };
    int iterationCount = 10;

    public DESEncryptionProvider(String passPhrase) throws EncryptionException {
        try {
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            Log.e("AEGIS", "Failed to created DES encryptor.", e);
            throw new EncryptionException("DESEncrpyionProvider could not be created", e);
        }
    }

    public String encryptAsBase64(String str) throws EncryptionException {
        try {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            return Base64.encodeBytes(enc);
        } catch (Exception e) {
            Log.e("AEGIS", "Failed to created DES encryptor.", e);
            throw new EncryptionException("DESEncrpyionProvider could not be created", e);
        }
    }

    public String decryptAsBase64(String str) throws EncryptionException {
        try {
            byte[] dec = Base64.decode(str);
            byte[] utf8 = dcipher.doFinal(dec);

            return new String(utf8, "UTF8");
        } catch (Exception e) {
            Log.e("AEGIS", "Failed to created DES encryptor.", e);
            throw new EncryptionException("DESEncrpyionProvider could not be created", e);
        }
    }
}
