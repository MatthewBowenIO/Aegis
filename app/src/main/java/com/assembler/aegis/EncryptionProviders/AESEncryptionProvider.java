package com.assembler.aegis.EncryptionProviders;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.assembler.aegis.SupportingClasses.Base64;

import android.util.Log;

/**
 * Created by Matthew on 12/28/2014.
 */
public class AESEncryptionProvider extends IStringEncryptor {
    protected static byte[] rawSecretKey = { 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    public AESEncryptionProvider(String passphrase) throws EncryptionException {
        this.cipherTransformation = "AES/CBC/PKCS5Padding";
        this.cipherAlgorithm = "AES";
        this.messageDigestAlgorithm = "MD5";
        byte[] passwordKey = encodeDigest(passphrase);

        try {
            this.cipher = Cipher.getInstance(this.cipherTransformation);
        } catch (NoSuchAlgorithmException e) {
            Log.e("AEGIS", "No such algorithm " + this.cipherAlgorithm, e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        } catch (NoSuchPaddingException e) {
            Log.e("AEGIS", "No such padding PKCS5", e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        }

        secretKey = new SecretKeySpec(passwordKey, this.cipherAlgorithm);
        ivParameterSpec = new IvParameterSpec(rawSecretKey);
    }

    public String encryptAsBase64(String data) throws EncryptionException {
        byte[] encryptedData = encrypt(data.getBytes());
        return Base64.encodeBytes(encryptedData);
    }

    public String decryptAsBase64(String data) throws EncryptionException {
        try {
            byte[] decryptedData = decrypt(Base64.decode(data.getBytes()));
            return new String(decryptedData);
        } catch (Exception e) {
            throw new EncryptionException("Decryption Failed", e);
        }
    }

    private byte[] decrypt(byte[] cipherData) throws EncryptionException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        } catch (InvalidKeyException e) {
            Log.e("AEGIS", "Invalid key", e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("AEGIS", "Invalid algorithm " + cipherAlgorithm, e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        }

        byte[] decryptedData;
        try {
            decryptedData = cipher.doFinal(cipherData);
        } catch (IllegalBlockSizeException e) {
            Log.e("AEGIS", "Illegal block size", e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        } catch (BadPaddingException e) {
            Log.e("AEGIS", "Bad padding", e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        }
        return decryptedData;
    }

    private byte[] encrypt(byte[] clearData) throws EncryptionException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        } catch (InvalidKeyException e) {
            Log.e("AEGIS", "Invalid key", e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("AEGIS", "Invalid algorithm " + cipherAlgorithm, e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        }

        byte[] encryptedData;
        try {
            encryptedData = cipher.doFinal(clearData);
        } catch (IllegalBlockSizeException e) {
            Log.e("AEGIS", "Illegal block size", e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        } catch (BadPaddingException e) {
            Log.e("AEGIS", "Bad padding", e);
            throw new EncryptionException("AESEncrpyionProvider could not be created", e);
        }

        return encryptedData;
    }
}
