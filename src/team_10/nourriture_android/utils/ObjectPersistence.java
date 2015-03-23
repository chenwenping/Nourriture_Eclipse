package team_10.nourriture_android.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class ObjectPersistence {
    private static final String sTag = ObjectPersistence.class.getName();

    public synchronized static void writeObjectToFile(Context context, Object object, String fileName) {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Activity.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            fos.getFD().sync();
        } catch (FileNotFoundException e) {
            Log.e(ObjectPersistence.sTag, "ObjectPersistence-writeObjectToFile-FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(ObjectPersistence.sTag, "ObjectPersistence-writeObjectToFile-IOException");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(ObjectPersistence.sTag, "ObjectPersistence-writeObjectToFile-IOException");
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    Log.e(ObjectPersistence.sTag, "ObjectPersistence-writeObjectToFile-IOException");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param context
     * @param fileName
     * @return Object or null if FileNotFoundException / StreamCorruptedException / IOException / ClassNotFoundException
     */
    public synchronized static Object readObjectFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object object = null;
        try {
            fis = context.openFileInput(fileName);
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
        } catch (FileNotFoundException e) {
            Log.e(ObjectPersistence.sTag, "ObjectPersistence-readObjectFromFile-FileNotFoundException");
            e.printStackTrace();
            return object;
        } catch (StreamCorruptedException e) {
            Log.e(ObjectPersistence.sTag, "ObjectPersistence-readObjectFromFile-StreamCorruptedException");
            e.printStackTrace();
            return object;
        } catch (IOException e) {
            Log.e(ObjectPersistence.sTag, "ObjectPersistence-readObjectFromFile-IOException");
            e.printStackTrace();
            return object;
        } catch (ClassNotFoundException e) {
            Log.e(ObjectPersistence.sTag, "ObjectPersistence-readObjectFromFile-ClassNotFoundException");
            e.printStackTrace();
            return object;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e(ObjectPersistence.sTag, "ObjectPersistence-readObjectFromFile-IOException");
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    Log.e(ObjectPersistence.sTag, "ObjectPersistence-readObjectFromFile-IOException");
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    public synchronized static boolean deleteFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }
}