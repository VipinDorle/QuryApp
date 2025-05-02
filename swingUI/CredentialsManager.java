import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

public class CredentialManager {

    private static final String KEY_FILE = "key.secret";
    private static final String CREDS_FILE = "password.enc";
    private static final String ALGORITHM = "AES";

    // Generate or load AES key
    private static SecretKeySpec getOrCreateKey() throws Exception {
        File keyFile = new File(KEY_FILE);
        if (!keyFile.exists()) {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(128, new SecureRandom());
            SecretKey secretKey = keyGen.generateKey();
            byte[] encodedKey = secretKey.getEncoded();
            try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                fos.write(Base64.getEncoder().encode(encodedKey));
            }
            return new SecretKeySpec(encodedKey, ALGORITHM);
        } else {
            return loadKey();
        }
    }

    private static SecretKeySpec loadKey() throws Exception {
        byte[] encodedKey;
        try (FileInputStream fis = new FileInputStream(KEY_FILE)) {
            encodedKey = Base64.getDecoder().decode(fis.readAllBytes());
        }
        return new SecretKeySpec(encodedKey, ALGORITHM);
    }

    public static void saveEncryptedPassword(String password) throws Exception {
        SecretKeySpec key = getOrCreateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(password.getBytes());
        try (FileOutputStream fos = new FileOutputStream(CREDS_FILE)) {
            fos.write(Base64.getEncoder().encode(encrypted));
        }
    }

    public static String loadDecryptedPassword() throws Exception {
        SecretKeySpec key = loadKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] encrypted;
        try (FileInputStream fis = new FileInputStream(CREDS_FILE)) {
            encrypted = Base64.getDecoder().decode(fis.readAllBytes());
        }
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }

    public static void deleteStoredCredentials() {
        new File(CREDS_FILE).delete();
    }
}
