package fun.logcatcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static void write(String path, byte[] b){
        File mFile = new File(path);
        File parent = mFile.getParentFile();
        if(!parent.exists()){
            parent.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(b);
            fos.close();
        }catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
    }
    public static void write(String path, String s){
        write(path, s.getBytes());
    }
    public static String readSize(String path, int size){

        try {
            long max_size = new File(path).length();
            if (max_size < size) return new String(readBytes(path));
            FileInputStream ins = new FileInputStream(path);
            ins.skip(max_size - size);
            byte[] b = new byte[size];
            ins.read(b);
            ins.close();
            return new String(b);
        }catch (Exception e){
            return "";
        }
    }
    public static byte[] readBytes(String path){
        File mFile = new File(path);
        if(!mFile.exists()){
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(mFile);
            byte[] b = new byte[(int)mFile.length()];
            fis.read(b);
            fis.close();
            return b;
        }catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
    }
    public static String readString(String path){
        byte[] b = readBytes(path);
        if(b == null){
            return null;
        }
        return new String(b);
    }
    public static boolean isPathWriteable(String path){
        File file = new File(path);
        if (file.isFile()){
            return false;
        }else if (!file.exists()){
            return file.mkdirs();
        }
        File testFile = new File(file,".write.checker");
        if (testFile.exists()){
            return testFile.delete();
        }
        try {
            if (!testFile.createNewFile())return false;
            return testFile.delete();
        } catch (IOException e) {
            return false;
        }
    }
    public static void delete(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()){
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                delete(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }
    public static void copy(String from, String to){
        File fromFile = new File(from);
        File toFile = new File(to);
        if (!toFile.getParentFile().exists()){
            toFile.getParentFile().mkdirs();
        }
        if (fromFile.isDirectory()){
            if (!toFile.exists()){
                toFile.mkdirs();
            }
            File[] files = fromFile.listFiles();
            if (files == null) return;
            for (File f : files){
                copy(f.getAbsolutePath(),to + File.separator + f.getName());
            }
        }else {
            try {
                FileInputStream fis = new FileInputStream(fromFile);
                FileOutputStream fos = new FileOutputStream(toFile);
                byte[] b = new byte[1024];
                int len;
                while ((len = fis.read(b)) != -1){
                    fos.write(b,0,len);
                }
                fis.close();
                fos.close();
            }catch (IOException ignored){ }
        }
    }
}
