package com.assembler.aegis.EncryptionProviders;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import android.util.Log;

/**
 * Created by Matthew on 12/28/2014.
 */
public abstract class IStringEncryptor {
    protected Cipher cipher;
    protected SecretKey secretKey;
    protected IvParameterSpec ivParameterSpec;
    protected String cipherTransformation;
    protected String cipherAlgorithm;
    protected String messageDigestAlgorithm;
    public abstract String encryptAsBase64(String data) throws EncryptionException;
    public abstract String decryptAsBase64(String data) throws EncryptionException;

    protected byte[] encodeDigest(String text) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(messageDigestAlgorithm);
            return digest.digest(text.getBytes());
        } catch (NoSuchAlgorithmException e) {
            Log.e("AEGIS", "No such algorithm " + messageDigestAlgorithm, e);
        }
        return null;
    }
}
