package top.xiaohuashifu.filesystem.file.manager;

import top.xiaohuashifu.filesystem.exception.IllegalOperationException;
import top.xiaohuashifu.filesystem.exception.NotFoundException;
import top.xiaohuashifu.filesystem.file.File;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 描述: 文件管理器接口
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-03 11:23
 */
public interface FileManager {
    /**
     * 创建一个目录
     *
     * @param directoryPath 文件目录路径
     * @param directoryName 目录名
     * @param system 是否是系统文件
     *
     * @return 创建的目录文件
     * @throws IOException IO操作出错
     */
    File createDirectory(String directoryPath, String directoryName, boolean system) throws IOException;

    /**
     * 创建一个普通文件
     *
     * @param directoryPath 文件目录路径
     * @param fileName 文件名
     * @param system 是否是系统文件
     *
     * @return 创建的文件
     * @throws IOException IO操作出错
     */
    File createFile(String directoryPath, String fileName, boolean system) throws IOException;

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @throws NotFoundException 文件不存在
     * @throws IOException IO操作出错
     * @throws IllegalOperationException 无法删除非空目录
     */
    void deleteFile(String path) throws IOException;

    /**
     * 通过路径获取一个文件
     *
     * @param path 文件路径
     * @return 文件
     */
    File getFile(String path);

    /**
     * 通过目录路径列出一个目录的所有文件
     *
     * @param directoryPath 目录路径
     * @return 该目录下的文件列表
     */
    List<File> getFileList(String directoryPath);

    /**
     * 更新文件属性
     *
     * @param path 文件绝对路径
     * @param newFileName 新文件名
     * @return 更新之后的文件
     * @throws IOException IO操作出错
     * @throws NotFoundException 文件不存在
     */
    File updateFile(String path, String newFileName) throws IOException;

    /**
     * 把内容写入文件
     *
     * @param path 文件路径
     * @param content 文件内容
     * @throws IOException IO操作出错
     */
    void writeFile(String path, String content) throws IOException;

    /**
     * 读取一个文件，使用UTF_8编码
     *
     * @param path 文件绝对路径
     * @return 读取的字符串
     * @throws IOException IO操作出错
     */
    String readFile(String path) throws IOException;

    /**
     * 获取磁盘容量信息
     *
     * @return Map<String, Double> 磁盘容量信息
     */
    Map<String, Double> getDiskInfo();
}
