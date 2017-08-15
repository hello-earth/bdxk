package org.huakai.bdxk.common;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2017/8/14.
 */

public class Dataloger implements  Runnable {

    String filePath = "/sdcard/bdxk/";
    String fileName;
    String content;

    public Dataloger(String fileName,String content){
        this.fileName = fileName+".txt";
        this.content = content;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        writeTxtToFile(content, filePath, fileName);
    }

    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        File file = makeFilePath(filePath, fileName);

        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
