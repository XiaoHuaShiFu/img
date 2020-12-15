package top.xiaohuashifu.filesystem.test;

import top.xiaohuashifu.filesystem.disk.DiskBlock;
import top.xiaohuashifu.filesystem.disk.DiskConstant;
import top.xiaohuashifu.filesystem.disk.DiskManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-29 19:27
 */
public class DiskManagerTest {

    public static void main(String[] args) throws IOException {
        initDisk();
    }

    public static void testUpdateDiskBlock() throws IOException {
        DiskManager diskManager = new DiskManager(DiskConstant.DISK_FILE);
        diskManager.writeDiskBlock(new byte[] {0}, 0, 1, 0, 2);
    }

    public static void testGetDiskBlock() throws IOException {
        DiskManager diskManager = new DiskManager(DiskConstant.DISK_FILE);
        System.out.println(diskManager.getDiskBlock(2));
    }

    public static void testAllocateDiskBlock() throws IOException {
        DiskManager diskManager = new DiskManager(DiskConstant.DISK_FILE);

        DiskBlock diskBlock = diskManager.allocateDiskBlock();
        System.out.println(diskBlock);
    }

    public static void testWriteDiskBlock() throws IOException {
        DiskManager diskManager = new DiskManager(DiskConstant.DISK_FILE);
        byte[] bytes = new byte[DiskConstant.BLOCK_SIZE];
        for (int i = 0; i < DiskConstant.BLOCK_SIZE; i++) {
            bytes[i] = (byte) i;
        }
        diskManager.writeDiskBlock(new DiskBlock(2, bytes));
    }

//    public static void testReleaseDiskBlock() throws IOException {
//        DiskManager diskManager = new DiskManager(DiskConstant.DISK_FILE);
////        diskManager.releaseDiskBlocksStartWith(3);
////        diskManager.allocateDiskBlockPreviousWith(3);
//        diskManager.releaseDiskBlocksStartWith(3);
//        System.out.println(diskManager.fileAllocationTable);
//    }


    /**
     * 初始化磁盘
     */
    public static void initDisk() throws IOException {
        try (OutputStream outputStream = new FileOutputStream(DiskConstant.DISK_FILE)) {
            outputStream.write(-1);
            outputStream.write(-1);
            outputStream.write(-1);
            for (int i = 3; i < 128; i++) {
                outputStream.write(0);
            }
            for (int j = 2; j < 128; j++) {
                for (int i = 0; i < 64; i++) {
                    outputStream.write(0);
                }
            }
        }
        DiskManager diskManager = new DiskManager(DiskConstant.DISK_FILE);
        byte[] bytes = new byte[64];
        for (int i = 0; i < 64; i++) {
            if ((i % 8) == 0) {
                bytes[i] = '$';
            } else {
                bytes[i] = 0;
            }
        }
        diskManager.writeDiskBlock(new DiskBlock(2, bytes));
    }
}
