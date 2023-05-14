package io.github.MGDCl.TheBridge.utils;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.HashMap;

public class ConvertBase64 {

    private static HashMap<String, String> data = new HashMap<String, String>();

    public static HashMap<String, String> getDataVoid() {
        data.clear();
        data.put("fly", "0");
        data.put("jump", "0");
        data.put("chat", "0");
        data.put("visible", "0");
        data.put("ride", "0");
        data.put("speed", "0");
        data.put("row", "0");
        return data;
    }

    public static String convertToBase64(HashMap<String, String> data) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(data);
        out.close();
        return Base64Coder.encodeLines(byteOut.toByteArray());
    }

    public static HashMap<String, String> getDataFromBase64(String data) throws ClassNotFoundException, IOException{
        ByteArrayInputStream byteIn = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        HashMap<String, String> data2 = (HashMap<String, String>) in.readObject();
        in.close();
        return data2;
    }

    public static String toBase64(Object object) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(object);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static Object fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Object object = dataInput.readObject();
            dataInput.close();
            return object;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
