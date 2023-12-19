package com.example.file;

import org.apache.tomcat.util.buf.HexUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {

    // 计算文件的 MD5 值 根据MD5值 判断文件是否是同一个文件
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest =MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 提取文件 checksum
     *
     * @param path      文件全路径
     * @param algorithm  算法名 例如 MD5、SHA-1、SHA-256等
     * @return  checksum
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws IOException              the io exception
     */
    public static String extractChecksum(String path, String algorithm) throws NoSuchAlgorithmException, IOException {
        // 根据算法名称初始化摘要算法
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        // 读取文件的所有比特
        byte[] fileBytes = Files.readAllBytes(Paths.get(path));
        // 摘要更新
        digest.update(fileBytes);
        //完成哈希摘要计算并返回特征值
        byte[] digested = digest.digest();
        // 进行十六进制的输出
        return HexUtils.toHexString(digested);
    }
}
