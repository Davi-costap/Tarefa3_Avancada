package com.example.mylibraryCrypto;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {
    private static final String KEY = "chave secreta aqui"; // Chave secreta
    private static final String ALGORITHM = "AES"; // Algoritmo de criptografia

    public static String encrypt(String data) {

        long startTime = System.nanoTime(); // Registrar o tempo de início

        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes());

            long endTime = System.nanoTime(); // Registrar o tempo de término
            long executionTime = (endTime - startTime) / 1000000; // Tempo em milissegundos
            Log.d("Criptografia", "Tempo de Criptografia: " + executionTime + "ms");

            return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(android.util.Base64.decode(encryptedData, Base64.DEFAULT));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar", e);
        }
    }
}
