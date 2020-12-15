package top.xiaohuashifu.filesystem.test;

import top.xiaohuashifu.filesystem.file.allocation.FileAllocationTable;

import java.io.*;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-29 17:03
 */
public class DiskPathTest {

    public static void main(String[] args) {
        DiskPathTest diskPathTest = new DiskPathTest();
        diskPathTest.initFat();
    }

    /**
     * 读取磁盘初始化文件分配表
     */
    public void initFat() {
        String systemDiskPath = System.getProperty("user.dir");
        File file = new File(systemDiskPath, "system.disk");
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[128];
            inputStream.read(bytes);
            FileAllocationTable fileAllocationTable = new FileAllocationTable(bytes);
            System.out.println(fileAllocationTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
