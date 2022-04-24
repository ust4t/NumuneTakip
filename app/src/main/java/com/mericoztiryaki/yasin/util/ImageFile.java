package com.mericoztiryaki.yasin.util;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by as on 2019-05-11.
 */
public class ImageFile {

    private String imagePath;
    private String fileName;
    private byte[] bytes;

    public ImageFile(Context context, String path) throws IOException {
        this.imagePath = path;

        Uri uri = Uri.fromFile(new File(path));
        this.bytes = toByteArray(context, uri);
    }

    private byte[] toByteArray(Context context, Uri uri) throws IOException {

        InputStream is = context.getContentResolver().openInputStream(uri);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        for (int readNum; (readNum = is.read(buf)) != -1;)
            bos.write(buf, 0, readNum);

        return bos.toByteArray();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getImagePath() {
        return imagePath;
    }
}
