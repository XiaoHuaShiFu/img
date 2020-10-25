package top.xiaohuashifu.filesystem.test;

import top.xiaohuashifu.filesystem.file.manager.SimpleFileManager;
import top.xiaohuashifu.filesystem.disk.DiskConstant;
import top.xiaohuashifu.filesystem.disk.DiskManager;

import java.io.IOException;
import java.util.Arrays;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-01 15:07
 */
public class FileManagerTest {

    public static void main(String[] args) throws IOException {
        DiskManager diskManager = new DiskManager(DiskConstant.DISK_FILE);
//        System.out.println(Arrays.toString(diskManager.fileAllocationTable.items));
        SimpleFileManager simpleFileManager = new SimpleFileManager(diskManager);
        simpleFileManager.getFile("/");
//        directoryTree.addNode("/", new File("use", "txt",
//                new FileAttribute(false, true, true, false),
//                3,1));
//        directoryTree.addNode("/", new File("sys", null,
//                new FileAttribute(false, false, true, true),
//                4,1));
//        directoryTree.addNode("/sys", new File("fi", "dat",
//                new FileAttribute(false, false, true, false),
//                5,3));
//        directoryTree.deleteNode("/sys/fi.dat");
//        System.out.println(simpleFileManager.createDirectory("/", "dat", true));
//        System.out.println(simpleFileManager.createDirectory("/usr", "xxx", true));
//        System.out.println(diskManager.getDiskBlock(5));
//        System.out.println(simpleFileManager.createDirectory("/usr", "dir", true));
//        System.out.println(simpleFileManager.createFile("/", "txt.t", true));
//        System.out.println(diskManager.getDiskBlock(5));
//        simpleFileManager.deleteFile("/usr/dir/de");
//        System.out.println(simpleFileManager.getFileList("/usr/dir"));
//        System.out.println(simpleFileManager.getFileList("/usr/dir"));
//        simpleFileManager.deleteFile("/usr/dir/zzzx");
//        simpleFileManager.writeFile("/usr/dir/txt.t", "hahhaha吴嘉贤hahhaha吴嘉贤hahhaha吴嘉贤hahhaha吴嘉贤h");
//        System.out.println(simpleFileManager.readFile("/usr/dir/txt.t"));
//        simpleFileManager.updateFile("/usr/dir/rr", "rr.xx");
    }
}
