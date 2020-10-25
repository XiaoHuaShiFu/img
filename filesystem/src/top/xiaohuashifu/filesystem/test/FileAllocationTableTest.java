package top.xiaohuashifu.filesystem.test;

import top.xiaohuashifu.filesystem.file.allocation.FileAllocationTable;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-29 16:43
 */
public class FileAllocationTableTest {
    public static void main(String[] args) {
        byte[] bytes = {-1, -1, -1, 4, 9, 0, 7, 8, -1, 12, 11, -1, 13, -1, 0, 0};
        FileAllocationTable fileAllocationTable = new FileAllocationTable(bytes);
        System.out.println(fileAllocationTable);
    }

}
