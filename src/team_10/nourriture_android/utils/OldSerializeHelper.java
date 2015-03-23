package team_10.nourriture_android.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.Queue;

public class OldSerializeHelper {
    public static Object getObjectFormFile(String filePath) {
        if (filePath == null || filePath.length() == 0)
            return null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file));
                Object object = ois.readObject();
                return object;
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void writeObjectToFile(Object obj, String filePath, boolean append) {
        if (obj == null || filePath == null || filePath.length() == 0)
            return;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists()) parent.mkdirs();
                boolean isSuc = file.createNewFile();
                System.out.println("isSuc" + isSuc);
            }
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file, append));
            oos.writeObject(obj);
            oos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化对象保存至文件
     */
    public static void writeObjectToFile(Object obj, String filePath) {
        writeObjectToFile(obj, filePath, false);
    }


    public static Queue getListFormFile(String filePath) {
        if (filePath == null || filePath.length() == 0)
            return null;
        Queue list = new LinkedList();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file));
                Object object = ois.readObject();
                while (object != null) {
                    list.add(object);
                    object = ois.readObject();
                }
                return list;
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 序列化对象保存至文件
     */
    public static void writeQueueToFile(Queue list, String filePath) {
        if (list == null || filePath == null || filePath.length() == 0)
            return;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file, true));
            Object object = list.poll();
            while (object != null) {
                oos.writeObject(object);
                object = list.poll();
            }
            oos.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getSDPath() {
        String path = null;
        if (hasSdcard()) {
            File f = Environment.getExternalStorageDirectory();
            path = f.toString();
        }

        return path;
    }

    private static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
