package com.assembler.aegis.EncryptionProviders;

/**
 * Created by Matthew on 12/28/2014.
 */
public class EncryptionException extends Exception {
    private static final long serialVersionUID = 1L;

    public EncryptionException() {
        super("Encryption Exception");
    }

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionException(Throwable cause) {
        super(cause);
    }
}
