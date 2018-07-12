package com.guoguang.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by allen on 2018/7/11.
 */

public class FileUtil {
    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath,String fileName) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
            //throw new FileNotFoundException(filePath);
        }
        String name=file.toString()+File.separator+fileName;
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = null;

        try {

            in = new BufferedInputStream(new FileInputStream(name));
            short bufsize = 1024;
            byte[] buffer = new byte[bufsize];
            int len1;
            while (-1 != (len1 = in.read(buffer, 0, bufsize))) {
                bos.write(buffer, 0, len1);
            }
            byte[] var7 = bos.toByteArray();
            return var7;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException var14) {
                var14.printStackTrace();
            }
            bos.close();
        }

    }
}