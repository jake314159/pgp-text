import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by jake on 12/08/14.
 */
public class EncryptIO {

    public static void main(String args[]) throws IOException, EncryptIOException {
        char[] key = new char[] {'p','a','s','s'};
        OutputStream os = EncryptIO.EncryptStream("testFile", key);
        os.write(toByteArray(key));
        os.flush();
        os.close();

        System.out.println("Write complete!");
        InputStream is = EncryptIO.DecryptStream("testFile", key);
        int c = (char)is.read();
        while(c>=0) {
            System.out.print((char) c);
            c = is.read();
        }
        is.close();
    }

    private static byte[] toByteArray(char[] key) {
        byte[] b = new byte[key.length];
        for(int i=0; i<key.length; i++) {
            b[i] = (byte)key[i];
        }
        return b;
    }

    public static InputStream DecryptStream(String file, char[] key) throws EncryptIOException {
        Cipher cipher1 = null;
        try {
            cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptIOException("Decrypt: No such cipher algorithm");
        } catch (NoSuchPaddingException e) {
            throw new EncryptIOException("Decrypt: No such padding");
        }

        //make the key
        byte[] keyByte = toByteArray(key);
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptIOException("Decrypt: No such hash algorithm");
        }
        keyByte = sha.digest(keyByte);
        keyByte = Arrays.copyOf(keyByte, 16); // use only first 128 bit

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, "AES");
        IvParameterSpec iv = new IvParameterSpec(keyByte);

        try {
            cipher1.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        } catch (InvalidKeyException e) {
            throw new EncryptIOException("Decrypt: Invalid key");
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptIOException("Decrypt: Invalid algorithm");
        }

        FileInputStream fis = null;
        CipherInputStream cis = null;

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new EncryptIOException("Encrypt: File not found");
        }
        cis = new CipherInputStream(fis, cipher1);
        return cis;
    }

    public static OutputStream EncryptStream(String file, char[] key) throws EncryptIOException {
        Cipher encryptCipher = null;
        try {
            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptIOException("Encrypt: No such cipher algorithm");
        } catch (NoSuchPaddingException e) {
            throw new EncryptIOException("Encrypt: Non such padding");
        }

        //make the key
        byte[] keyByte = toByteArray(key);
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptIOException("Encrypt: No such hash algorithm");
        }
        keyByte = sha.digest(keyByte);
        keyByte = Arrays.copyOf(keyByte, 16); // use only first 128 bit

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, "AES");
        IvParameterSpec iv = new IvParameterSpec(keyByte);

        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        } catch (InvalidKeyException e) {
            throw new EncryptIOException("Encrypt: Invalid key");
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptIOException("Encrypt: Invalid algorithm");
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new EncryptIOException("Encrypt: File not found");
        }
        CipherOutputStream cipherOutputStream = new CipherOutputStream(fos, encryptCipher);
        return cipherOutputStream;
    }
}
