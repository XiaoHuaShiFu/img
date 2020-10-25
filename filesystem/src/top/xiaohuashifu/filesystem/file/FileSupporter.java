package top.xiaohuashifu.filesystem.file;

import top.xiaohuashifu.filesystem.disk.DiskBlock;
import top.xiaohuashifu.filesystem.disk.DiskConstant;
import top.xiaohuashifu.filesystem.util.ByteUtils;
import top.xiaohuashifu.filesystem.util.Pair;
import top.xiaohuashifu.filesystem.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 文件操作的辅助类，为文件操作提供各种解析方法
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-01 15:24
 */
public class FileSupporter {

    /**
     * 寻找字节数组里的空的空间
     * 此方法用于文件的创建，因为文件的创建需要修改目录信息，
     * 因此需要查找某个目录所占的那块磁盘块里的空闲空间，添加新的文件信息。
     *
     * @param bytes 字节数组
     * @param pace 步长
     * @param symbol 空闲空间的标志符
     * @return 空闲空间的开始下标，如果返回-1表示没有空闲空间
     */
    public static int findEmptySpaceOfDiskBlock(byte[] bytes, int pace, byte symbol) {
        for (int i = 0; i < bytes.length; i += pace) {
            if (bytes[i] == symbol) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 把磁盘块解析成文件列表
     *
     * @param diskBlock 磁盘块
     * @return List<File> 文件列表
     */
    public static List<File> parseDiskBlockToFileList(DiskBlock diskBlock) {
        byte[] bytes = diskBlock.getBytes();
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < bytes.length / FileConstant.SIZE_OF_FILE; i++) {
            // 空文件
            if (bytes[i * FileConstant.SIZE_OF_FILE] == FileConstant.EMPTY_FILE_SYMBOL) {
                continue;
            }
            fileList.add(createFileByBytes(bytes, i * FileConstant.SIZE_OF_FILE));
        }
        return fileList;
    }

    /**
     * 通过一个字节数组创建一个文件
     *
     * @param bytes 字节数组
     * @param offset 字节数字的读取偏移量
     * @return File 文件
     */
    public static File createFileByBytes(byte[] bytes, int offset) {
        String name = ByteUtils.bytesToString(bytes, offset, FileConstant.SIZE_OF_NAME).trim();
        String type = ByteUtils.bytesToString(bytes, offset + FileConstant.SIZE_OF_NAME, FileConstant.SIZE_OF_TYPE).trim();
        FileAttribute fileAttribute = createFileAttributeByByte(bytes[offset + FileConstant.SIZE_OF_NAME
                + FileConstant.SIZE_OF_TYPE]);
        int firstDiskBlockIndex = bytes[offset + FileConstant.SIZE_OF_NAME + FileConstant.SIZE_OF_TYPE
                + FileConstant.SIZE_OF_ATTRIBUTE];
        int length = bytes[offset + FileConstant.SIZE_OF_NAME + FileConstant.SIZE_OF_TYPE
                + FileConstant.SIZE_OF_ATTRIBUTE + FileConstant.SIZE_OF_FIRST_DISK_BLOCK_INDEX];
        return new File(name, type, fileAttribute, firstDiskBlockIndex, length);
    }

    /**
     * 通过一个字节构造文件属性对象
     *
     * @param attribute 字节属性
     * @return FileAttribute 文件属性
     */
    public static FileAttribute createFileAttributeByByte(byte attribute) {
        boolean[] booleans = ByteUtils.byteToBooleans(attribute);
        boolean readOnly = booleans[FileConstant.SIZE_OF_FILE - FileConstant.FileAttribute.INDEX_OF_READ_ONLY - 1];
        boolean system = booleans[FileConstant.SIZE_OF_FILE - FileConstant.FileAttribute.INDEX_OF_SYSTEM - 1];
        boolean readWrite = booleans[FileConstant.SIZE_OF_FILE - FileConstant.FileAttribute.INDEX_OF_READ_WRITE - 1];
        boolean directory = booleans[FileConstant.SIZE_OF_FILE - FileConstant.FileAttribute.INDEX_OF_DIRECTORY - 1];
        return new FileAttribute(readOnly, system, readWrite, directory);
    }

    /**
     * 把一个文件解析成字节数组
     *
     * @param file 文件
     * @return byte[] 字节数组
     */
    public static byte[] parseFileToBytes(File file) {
        byte[] bytes = new byte[FileConstant.SIZE_OF_FILE];
        int i = 0;
        // 解析文件名
        byte[] name = file.getName().getBytes();
        for (; i < name.length; i++) {
            bytes[i] = name[i];
        }
        for (; i < FileConstant.SIZE_OF_NAME; i++) {
            bytes[i] = 0;
        }
        // 解析文件类型
        // 这里有两种情况，一种是目录文件，文件类型为空
        // 一种是普通文件，目录类型可能空
        byte[] type = file.getType() == null || "".equals(file.getType().trim()) ? new byte[0] : file.getType().getBytes();
        int j;
        for (j = 0; j < type.length; j++) {
            bytes[i++] = type[j];
        }
        for (; i < FileConstant.SIZE_OF_NAME + FileConstant.SIZE_OF_TYPE; i++) {
            bytes[i] = 0;
        }
        // 解析文件属性
        bytes[i++] = parseFileAttributeToByte(file.getFileAttribute());
        // 文件起始磁盘块下标
        bytes[i++] = (byte) file.getFirstDiskBlockIndex();
        // 文件长度
        bytes[i] = (byte) file.getLength();
        return bytes;
    }

    /**
     * 解析文件属性成一个字节
     *
     * @param fileAttribute 文件属性
     * @return 字节
     */
    public static byte parseFileAttributeToByte(FileAttribute fileAttribute) {
        String attribute = "0000";
        attribute = attribute + (fileAttribute.isDirectory() ? "1" : "0");
        attribute = attribute + (fileAttribute.isReadWrite() ? "1" : "0");
        attribute = attribute + (fileAttribute.isSystem() ? "1" : "0");
        attribute = attribute + (fileAttribute.isReadOnly() ? "1" : "0");
        return Byte.valueOf(attribute,2);
    }

    /**
     * 判断一个文件名是否合法
     *
     * @param fileName 文件名
     * @return 是否合法
     */
    public static boolean legalFileName(String fileName) {
        // 文件名.文件类型 超长
        if (fileName.length() > FileConstant.MAX_LENGTH_OF_FILE_NAME + FileConstant.MAX_LENGTH_OF_FILE_TYPE + 1) {
            return false;
        }
        // 文件名不能以 “.”开始或结尾
        if (fileName.startsWith(FileConstant.FILE_NAME_SEPARATOR) || fileName.endsWith(FileConstant.FILE_NAME_SEPARATOR)) {
            return false;
        }

        int index = fileName.lastIndexOf(FileConstant.FILE_NAME_SEPARATOR);
        // 文件名超长
        if (index == -1) {
            if (fileName.trim().length() > FileConstant.MAX_LENGTH_OF_FILE_NAME) {
                return false;
            }
        }
        // 类型名超长或者文件名超长
        else if (fileName.length() - index - 1 > FileConstant.MAX_LENGTH_OF_FILE_TYPE ||
                index > FileConstant.MAX_LENGTH_OF_FILE_NAME) {
            return false;
        }

        char[] chars = fileName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            // 文件名或文件类型包含非法字符
            if (i != index && (chars[i] == '$' || chars[i] == '.' || chars[i] == '/')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一个目录名是否合法
     *
     * @param directoryName 目录名
     * @return 是否合法
     */
    public static boolean legalDirectoryName(String directoryName) {
        // 目录名超长
        if (directoryName.length() > FileConstant.MAX_LENGTH_OF_FILE_NAME) {
            return false;
        }

        char[] chars = directoryName.toCharArray();
        for (char c : chars) {
            // 目录名包含非法字符
            if (c == '$' || c == '.' || c == '/') {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成一个空的目录磁盘块
     * @return byte[] 空目录磁盘块
     */
    public static byte[] getEmptyDirectoryDiskBlock() {
        byte[] bytes = new byte[DiskConstant.BLOCK_SIZE];
        for (int i = 0; i < bytes.length; i++) {
            if (i % 8 == 0) {
                bytes[i] = FileConstant.EMPTY_FILE_SYMBOL;
            } else {
                bytes[i] = 0;
            }
        }
        return bytes;
    }

    /**
     * 获取文件名，会自动拼接文件名和类型
     *
     * @param file 文件
     * @return 文件名
     */
    public static String getFileName(File file) {
        if (file.getFileAttribute().isDirectory()) {
            return file.getName().trim();
        }
        if (StringUtils.isBlank(file.getType())) {
            return file.getName().trim();
        }
        return file.getName().trim() + FileConstant.FILE_NAME_SEPARATOR + file.getType().trim();
    }

    /**
     * 获取结束标志的下标
     *
     * @param bytes 字节数组
     * @return 结束标志的下标
     */
    public static int getEndOfFileSymbolIndex(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == FileConstant.END_OF_FILE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获得一个文件在目录磁盘块内的下标
     *
     * @param bytes 磁盘块
     * @param file 文件
     * @return 目录磁盘块内的下标
     */
    public static int getDiskBlockIndexOfFile(byte[] bytes, File file) {
        byte[] fileBytes = parseFileToBytes(file);
        for (int i = 0; i < DiskConstant.BLOCK_SIZE / FileConstant.SIZE_OF_FILE; i++) {
            int j;
            for (j = 0; j < FileConstant.SIZE_OF_FILE; j++) {
                if (!(bytes[i * FileConstant.SIZE_OF_FILE + j] == fileBytes[j])) {
                     break;
                }
            }
            if (j == FileConstant.SIZE_OF_FILE) {
                return i * FileConstant.SIZE_OF_FILE;
            }
        }
        return -1;
    }

    /**
     * 把文件名解析成文件名和类型
     *
     * @param fileName 文件名
     * @return Pair<String, String>
     */
    public static Pair<String, String> parseFileName(String fileName) {
        // 寻找 “.”的下标
        int index = fileName.lastIndexOf(FileConstant.FILE_NAME_SEPARATOR);
        String name;
        String type;
        // 没有找到 “.”，说明没有文件类型
        if (index == -1) {
            name = fileName;
            type = null;
        } else {
            name = fileName.substring(0, index);
            type = fileName.substring(index + 1);
        }
        return new Pair<>(name, type);
    }

    /**
     * 把文件名解析成文件名和类型
     * 这个方法会去除文件名前面的路径，也就是比如/use/dd/zz.t会解析成zz和t
     *
     * @param fileName 文件名
     * @return Pair<String, String>
     */
    public static Pair<String, String> parseFileName0(String fileName) {
        int index = fileName.lastIndexOf("/");
        if (index != -1) {
            fileName = fileName.substring(index + 1);
        }
        return parseFileName(fileName);
    }

    /**
     * 从路径里获取文件名，包括文件类型
     *
     * @param path 路径
     * @return 文件名
     */
    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        if (index == -1) {
            return path;
        }
        return path.substring(index + 1);
    }

}
