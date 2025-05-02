package com;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHandling {

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
    }

    public static String encryptServicePassword(String servicePassword, String masterPassword) throws Exception {
        byte[] salt = generateRandomBytes(SALT_LENGTH);
        SecretKey key = deriveKey(masterPassword, salt);

        byte[] iv = generateRandomBytes(IV_LENGTH);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        byte[] encryptedBytes = cipher.doFinal(servicePassword.getBytes());

        byte[] combined = new byte[salt.length + iv.length + encryptedBytes.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, salt.length + iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decryptServicePassword(String encryptedData, String masterPassword) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        byte[] ciphertext = new byte[combined.length - SALT_LENGTH - IV_LENGTH];

        System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
        System.arraycopy(combined, SALT_LENGTH, iv, 0, IV_LENGTH);
        System.arraycopy(combined, SALT_LENGTH + IV_LENGTH, ciphertext, 0, ciphertext.length);

        SecretKey key = deriveKey(masterPassword, salt);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes);
    }

    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}
