package top.xiaohuashifu.filesystem.file.allocation;

/**
 * 描述: 文件分配表的常量
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-28 2:02
 */
public final class FileAllocationTableConstant {

    /**
     * 代表盘块损坏标志
     */
    public static final byte DAMAGE = -2;

    /**
     * 代表文件结尾标志
     */
    public static final byte END = -1;

    /**
     * 代表盘块空标志
     */
    public static final byte EMPTY = 0;

    /**
     * 文件分配表的所占磁盘块块数
     */
    public static final int NUMBER_OF_FAT_DISK_BLOCKS = 2;


    /**
     * 文件分配表长度
     */
    public static final int LENGTH = 128;

}
