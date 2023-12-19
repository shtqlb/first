package com.example.file;


import java.io.*;
import java.util.*;

public class FileCompaire {
    public static void main(String[] args)  {
        long start = System.currentTimeMillis();
        System.out.println(start);
        String basePath = "D:\\个人项目\\fileTest";
        Map<Long, FileMap> files = new HashMap<>();
        List<Long> keys = new ArrayList<>();
        File dir = new File(basePath);
        // 判断文件夹是否存在
        if (!dir.exists()) {
            System.out.println("目录不存在");
            return;
        }
        // 获取文件列表,将文件字节大小一致的过滤出来
        File[] fileList = dir.listFiles();
        for (File fi : fileList) {
            if (fi.isFile()) {
                System.out.println(fi.getName() + "---------" + fi.length());
                FileMap fileMap = new FileMap();
                List<File> fileLi = new ArrayList<>();
                fileLi.add(fi);
                int count = 1;
                long fileSize = fi.length();
                if (files.containsKey(fileSize)) {
                    count = files.get(fileSize).getCount() + count;
                    fileLi.addAll(files.get(fileSize).getFiles());
                }
                fileMap.setCount(count);
                fileMap.setFiles(fileLi);
                files.put(fileSize, fileMap);
                if (count > 1 && !keys.contains(fileSize)) {
                    keys.add(fileSize);
                }
            }
        }
        //遍历比对文件字节相同的文件
        for (Long key : keys) {
            List<File> fileLi = files.get(key).getFiles();
            for(int i=0;i<fileLi.size();i++){
                for(int j=i+1;j<fileLi.size() ;j++){
                    compareFile(fileLi.get(i),fileLi.get(j));
                }
            }
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    private static void compareFile(File file1, File file2) {
        FileInputStream f1 = null;
        FileInputStream f2 = null;
        try {
            f1 = new FileInputStream(file1);
            f2 = new FileInputStream(file2);
            int c;
            boolean flag = true;
            byte[] a = new byte[1024*100];
            byte[] b = new byte[1024*100];
            while ((c = f1.read(a)) != -1) {
                if (f2.read(b) != c) {
                    flag = false;
                    return;
                }
            }
            if (flag) {
                System.out.println(file1.getName() + "和" + file2.getName() + "内容一致");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (f1 != null) {
                try {
                    f1.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (f2 != null) {
                try {
                    f2.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

class FileMap {
    private int count;
    private List<File> files;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
