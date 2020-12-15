package top.xiaohuashifu.filesystem.disk;

import java.io.File;

/**
 * 描述: 磁盘相关常量
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-29 17:58
 */
public class DiskConstant {

    public static final String DISK_NAME = "system.disk";

    public static final File DISK_FILE = new File(System.getProperty("user.dir"), DISK_NAME);

    public static final int BLOCK_SIZE = 64;

}
