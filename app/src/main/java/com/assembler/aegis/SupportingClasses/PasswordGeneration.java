package com.assembler.aegis.SupportingClasses;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Matthew on 12/28/2014.
 */
public class PasswordGeneration {
    private SecureRandom random = new SecureRandom();

    public String getRandomPassword() {
        return new  BigInteger(80, random).toString(32);
    }
}
