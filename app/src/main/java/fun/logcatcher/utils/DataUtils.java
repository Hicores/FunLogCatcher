package fun.logcatcher.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DataUtils {
    public static byte[] readAllBytes(InputStream inp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inp.read(buffer)) != -1) out.write(buffer, 0, read);
        return out.toByteArray();
    }
    public static String decryptByAES_CBC(byte[] data, byte[] key, byte[] Iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec,ivParameterSpec);
            byte[] decryptedData = cipher.doFinal(data);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = (byte) Integer.parseInt(inHex.substring(i, i + 2), 16);
            j++;
        }
        return result;
    }
    public static void copyStream(InputStream ins, OutputStream out){
        try {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = ins.read(buffer)) != -1) out.write(buffer, 0, read);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String byteArrayToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    public static String convertBytesToString(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        double convertedValue = bytes / Math.pow(1024, exp);

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedValue = df.format(convertedValue);

        return formattedValue + " " + unit + "B";
    }
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        StringBuilder md5 = new StringBuilder(bigInt.toString(16));
        while (md5.length() < 32) {
            md5.insert(0, "0");
        }
        return md5.toString().toUpperCase();
    }
    public static String getStrMD5(String data){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        digest.update(data.getBytes(StandardCharsets.UTF_8));
        BigInteger bigInt = new BigInteger(1, digest.digest());
        StringBuilder md5 = new StringBuilder(bigInt.toString(16).toUpperCase());
        while (md5.length() < 32){
            md5.insert(0, "0");
        }
        return md5.toString();
    }
    public static String getStrMD5_little(String data){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        digest.update(data.getBytes(StandardCharsets.UTF_8));
        BigInteger bigInt = new BigInteger(1, digest.digest());
        StringBuilder md5 = new StringBuilder(bigInt.toString(16).toLowerCase());
        while (md5.length() < 32){
            md5.insert(0, "0");
        }
        return md5.toString();
    }
    public static byte[] decryptByAES_ECB(byte[] data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] encryptByAES_ECB(byte[] data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String convertMillisecondsToTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        long remainingMilliseconds = milliseconds % 1000;

        return String.format("%02d:%02d.%03d", minutes, seconds, remainingMilliseconds);
    }
    public static byte[] gzipCompress(byte[] b){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            java.util.zip.GZIPOutputStream gzip = new java.util.zip.GZIPOutputStream(out);
            gzip.write(b);
            gzip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
