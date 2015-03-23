package team_10.nourriture_android.utils;

import android.os.Environment;
import android.util.Log;

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

public class SerializeHelper {
    public static final String LOCAL_DATA_DIR = "/tssq/";
    public static final String COMPANY_PREFERENTIAL_FILE_NAME = "companyPreferential.txt",
            OTHRE_PREFERENTIAL = "OtherPreferential.txt",
            YE_WU_BAN_LI = "yewubanli.txt";
    public static final String XINWENGONGGAO = "xinwengonggao.txt";
    public static final String GOUTONGHUDONG = "goutonghudong.txt";

    private static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    private static String getSDPath() {
        String path = null;
        if (hasSdcard()) {
            File f = Environment.getExternalStorageDirectory();
            path = f.toString();
        }

        return path;
    }

    public static Object getObjectFromFile(String dir, String fileName) {
        Log.d("SerializeHelper", "fileName-->" + fileName);
        String sdPath = getSDPath();
        if (sdPath == null) return null;

        StringBuffer sb = new StringBuffer(sdPath);
        sb.append(dir).append(fileName);
        Object obj = getObjectFormFile(sb.toString());
        Log.d("SerializeHelper", "localdata-->" + obj);
        return obj;
    }

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

    public static void writeObjectToFile(Object obj, String path, boolean append) {
        if (obj == null || path == null || path.length() == 0)
            return;

        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
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

    public static boolean removeFile(String dir, String fileName) {
        String sdPath = getSDPath();
        if (sdPath == null) return false;

        StringBuilder sb = new StringBuilder(sdPath);
        sb.append(dir).append(fileName);
        File file = new File(sb.toString());
        if (file.exists())
            return file.delete();
        else
            return false;
    }

    public static void writeObjectToFile(Object obj, String dir, String fileName) {
        String sdPath = getSDPath();
        if (sdPath == null) return;

        StringBuffer sb = new StringBuffer(sdPath);
        sb.append(dir).append(fileName);
        writeObjectToFile(obj, sb.toString(), false);
    }

    public static void writeObjectToFile(Object obj, String filepath) {
        writeObjectToFile(obj, filepath, false);
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
}
