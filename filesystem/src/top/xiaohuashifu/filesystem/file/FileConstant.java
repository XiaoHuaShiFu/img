package top.xiaohuashifu.filesystem.file;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 描述: 文件相关常量
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-31 18:51
 */
public class FileConstant {

    /**
     * 文件属性
     */
    interface FileAttribute {
        /**
         * 只读标志下标
         */
        int INDEX_OF_READ_ONLY = 0;

        /**
         * 系统文件标志下标
         */
        int INDEX_OF_SYSTEM = 1;

        /**
         * 读写标志下标
         */
        int INDEX_OF_READ_WRITE = 2;

        /**
         * 目录标志下标
         */
        int INDEX_OF_DIRECTORY = 3;
    }

    /**
     * 文件名大小
     */
    public static final int SIZE_OF_NAME = 3;

    /**
     * 文件类型大小
     */
    public static final int SIZE_OF_TYPE = 2;

    /**
     * 文件属性大小
     */
    public static final int SIZE_OF_ATTRIBUTE = 1;

    /**
     * 文件起始磁盘块号大小
     */
    public static final int SIZE_OF_FIRST_DISK_BLOCK_INDEX = 1;

    /**
     * 文件长度大小
     */
    public static final int SIZE_OF_LENGTH = 1;

    /**
     * 根目录磁盘块号
     */
    public static final int DISK_BLOCK_NUMBER_OF_ROOT_DIRECTORY = 2;

    /**
     * 一个文件项的大小（单位字节）
     */
    public static final int SIZE_OF_FILE = 8;

    /**
     * 一个目录的最大文件数
     */
    public static final int MAX_FILE_NUMBER_OF_DIRECTORY = 8;

    /**
     * 文件名与文件类型的分隔符
     */
    public static final String FILE_NAME_SEPARATOR = ".";

    /**
     * 空文件标识符
     */
    public static final byte EMPTY_FILE_SYMBOL = (byte) '$';

    /**
     * 文件名最大长度
     */
    public static final int MAX_LENGTH_OF_FILE_NAME = 3;

    /**
     * 文件类型最大长度
     */
    public static final int MAX_LENGTH_OF_FILE_TYPE = 2;

    /**
     * 文件结束标志
     */
    public static final byte END_OF_FILE = -1;

    /**
     * 文件的编码方式
     */
    public static final Charset ENCODING_OF_FILE = StandardCharsets.UTF_8;

}
