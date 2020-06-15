package com.gengy.control.Untils;

import android.os.Environment;

import com.gengy.control.Untils.Screen.ScreenShotUntils;

import java.io.File;

/**
 * @date on 2020/3/24
 * 描述       14:14
 * com.gengy.control.Untils
 */
public class FileUntils {

    public static FileUntils getInstance() {
        return FileUntils.FileUntilsHolder.instance;
    }
    static class FileUntilsHolder {
        private static FileUntils instance = new FileUntils();
    }
    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    private static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }


    public  String createFile() {
//        String  path =
//                Environment.getExternalStorageDirectory() + "/ASceenUtil/" + System.currentTimeMillis();
      String  path =
                Environment.getExternalStorageDirectory() + "/ASceenUtil/" + System.currentTimeMillis();

//        File mVideoFile = new File(path);
//        if (!mVideoFile.exists()) {
//            mVideoFile.mkdir();
//        }
        return path;
    }



}
