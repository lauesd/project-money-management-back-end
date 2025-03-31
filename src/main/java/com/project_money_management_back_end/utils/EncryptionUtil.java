package com.project_money_management_back_end.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    /*Prod
    @Value("${ENCRYPTION_SECRET}")
    private String encryptionSecret;
   */
    /*Dev*/
    private final String encryptionSecret = "my-secret-encryption-key";

    public String encrypt(String text) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(encryptionSecret.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encryptedBytes = cipher.doFinal(text.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        SecretKeySpec keySpec = new SecretKeySpec(encryptionSecret.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }
}
