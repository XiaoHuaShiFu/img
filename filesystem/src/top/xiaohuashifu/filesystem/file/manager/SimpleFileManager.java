package top.xiaohuashifu.filesystem.file.manager;

import top.xiaohuashifu.filesystem.directory.DirectoryTree;
import top.xiaohuashifu.filesystem.exception.DuplicateException;
import top.xiaohuashifu.filesystem.exception.IllegalOperationException;
import top.xiaohuashifu.filesystem.exception.NotFoundException;
import top.xiaohuashifu.filesystem.disk.DiskBlock;
import top.xiaohuashifu.filesystem.disk.DiskConstant;
import top.xiaohuashifu.filesystem.disk.DiskManager;
import top.xiaohuashifu.filesystem.file.*;
import top.xiaohuashifu.filesystem.file.allocation.FileAllocationTableConstant;
import top.xiaohuashifu.filesystem.util.Pair;
import top.xiaohuashifu.filesystem.util.StringUtils;

import java.io.IOException;
import java.util.*;


/**
 * 描述:文件管理器，提供文件操作
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-30 16:04
 */
public class SimpleFileManager implements FileManager {

    /**
     * 磁盘管理器
     */
    private DiskManager diskManager;

    /**
     * 被打开的文件列表
     */
    private List<OpenedFile> openedFileList;

    /**
     * 目录树
     */
    private DirectoryTree directoryTree;

    /**
     * 文件管理器构造器
     *
     * @param diskManager 磁盘管理器
     */
    public SimpleFileManager(DiskManager diskManager) throws IOException {
        this.diskManager = diskManager;
        this.openedFileList = new ArrayList<>();
        init();
    }

    /**
     * 创建一个目录
     *
     * @param directoryPath 文件目录路径
     * @param directoryName 目录名
     * @param system 是否是系统文件
     *
     * @return 创建的目录文件
     * @throws IOException IO操作出错
     * @throws IllegalArgumentException 非法目录名
     * @throws NotFoundException 目录不存在
     * @throws DuplicateException 文件名已经存在
     */
    public File createDirectory(String directoryPath, String directoryName, boolean system) throws IOException {
        // 非法目录名
        if (!FileSupporter.legalDirectoryName(directoryName)) {
            throw new IllegalArgumentException("非法目录名");
        }
        DirectoryTree.Node directory = directoryTree.getNode(directoryPath);
        // 目录不存在
        if (directory == null) {
            throw new NotFoundException("目录不存在");
        }

        // 判断文件名是否已经存在此目录下
        for (DirectoryTree.Node child : directory.getChildren()) {
            File file = child.getFile();
            // 如果文件是目录或者文件的类型是空白
            // 也就是只存在文件名
            if (file.getFileAttribute().isDirectory() || StringUtils.isBlank(file.getType())) {
                // 此文件名已经存在
                if (directoryName.equals(file.getName())) {
                    throw new DuplicateException("文件名已经存在");
                }
            }
        }

        // 为目录分配一块磁盘块
        DiskBlock newDiskBlock = diskManager.allocateDiskBlock(FileSupporter.getEmptyDirectoryDiskBlock());
        // 初始化目录磁盘块
        return createFile(directoryPath, directoryName, null,
                new FileAttribute(false, system, true, true), newDiskBlock.getIndex());
    }

    /**
     * 创建一个普通文件
     *
     * @param directoryPath 文件目录路径
     * @param fileName 文件名
     * @param system 是否是系统文件
     *
     * @return 创建的文件
     * @throws IOException IO操作出错
     * @throws IllegalArgumentException 非法文件名
     * @throws NotFoundException 目录不存在
     * @throws DuplicateException 文件名已经存在
     */
    public File createFile(String directoryPath, String fileName, boolean system) throws IOException {
        // 非法文件名
        if (!FileSupporter.legalFileName(fileName)) {
            throw new IllegalArgumentException("非法文件名");
        }

        DirectoryTree.Node directory = directoryTree.getNode(directoryPath);
        // 目录不存在
        if (directory == null) {
            throw new NotFoundException("目录不存在");
        }

        // 判断文件名是否已经存在此目录下
        for (DirectoryTree.Node child : directory.getChildren()) {
            File file = child.getFile();
            if (file.getFileAttribute().isDirectory()) {
                // 此文件名已经存在
                if (fileName.equals(file.getName())) {
                    throw new DuplicateException("文件名已经存在");
                }
            } else {
                // 此文件名已经存在
                if (fileName.equals(file.getName() + "." + file.getType())) {
                    throw new DuplicateException("文件名已经存在");
                }
            }
        }

        // 解析文件名
        Pair<String, String> fileName0 = FileSupporter.parseFileName(fileName);

        // 为文件分配一块磁盘块
        DiskBlock newDiskBlock = diskManager.allocateDiskBlock(new byte[] {-1});
        // 创建文件
        return createFile(directoryPath, fileName0.first, fileName0.second, new FileAttribute(false, system, true, false),
                newDiskBlock.getIndex());
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @throws NotFoundException 文件不存在
     * @throws IOException IO操作出错
     * @throws IllegalOperationException 无法删除非空目录
     */
    public void deleteFile(String path) throws IOException {
        DirectoryTree.Node node = directoryTree.getNode(path);
        // 文件不存在
        if (node == null) {
            throw new NotFoundException("文件不存在");
        }
        File file = node.getFile();
        // 如果文件是目录，且不是空文件，不能删除
        if (file.getFileAttribute().isDirectory() && node.getChildren().size() > 0) {
            throw new IllegalOperationException("无法删除非空目录");
        }
        // 在目录树里删除文件
        directoryTree.deleteNode(path);
        // 如果是文件，先释放掉文件内容所占的磁盘块
        if (!file.getFileAttribute().isDirectory()) {
            // 释放文件占用的磁盘块
            diskManager.releaseDiskBlocksStartWith(file.getFirstDiskBlockIndex());
        }
        // 获取父节点的磁盘块
        DiskBlock diskBlock = diskManager.getDiskBlock(node.getParent().getFile().getFirstDiskBlockIndex());
        // 获得此文件在目录磁盘块里的下标
        int diskBlockIndexOfFile = FileSupporter.getDiskBlockIndexOfFile(diskBlock.getBytes(), file);
        // 把目录磁盘块里的此文件设置为空
        diskManager.writeDiskBlock(new byte[] {FileConstant.EMPTY_FILE_SYMBOL}, 0, 1, diskBlock.getIndex(), diskBlockIndexOfFile);
    }

    /**
     * 通过路径获取一个文件
     *
     * @param path 文件路径
     * @return 文件
     * @throws NotFoundException 找不到该文件
     */
    public File getFile(String path) {
        DirectoryTree.Node node = directoryTree.getNode(path);
        // 找不到该文件
        if (node == null) {
            throw new NotFoundException("找不到该文件");
        }
        return node.getFile();
    }

    /**
     * 通过目录路径列出一个目录的所有文件
     *
     * @param directoryPath 目录路径
     * @return 该目录下的文件列表
     * @throws NotFoundException 找不到该文件夹
     * @throws IllegalArgumentException 该路径不是指向一个文件夹
     */
    public List<File> getFileList(String directoryPath) {
        DirectoryTree.Node node = directoryTree.getNode(directoryPath);
        // 文件夹不存在
        if (node == null) {
            throw new NotFoundException("找不到该文件夹");
        }
        // 该路径不是指向一个文件夹
        if (!node.getFile().getFileAttribute().isDirectory()) {
            throw new IllegalArgumentException("该路径不是指向一个文件夹");
        }
        List<File> fileList = new ArrayList<>(node.getChildren().size());
        for (DirectoryTree.Node child : node.getChildren()) {
            fileList.add(child.getFile());
        }
        return fileList;
    }

    /**
     * 更新文件属性
     *
     * @param path 文件绝对路径
     * @param newFileName 新文件名
     * @return 更新之后的文件
     * @throws IOException IO操作出错
     * @throws NotFoundException 文件不存在
     */
    public File updateFile(String path, String newFileName) throws IOException {
        DirectoryTree.Node node = directoryTree.getNode(path);
        // 文件不存在
        if (node == null) {
            throw new NotFoundException("文件不存在");
        }
        File file = node.getFile();
        // 判断文件名是否合法
        if (file.getFileAttribute().isDirectory()) {
            FileSupporter.legalDirectoryName(newFileName);
        } else {
            FileSupporter.legalFileName(newFileName);
        }
        // 解析文件名
        Pair<String, String> fileName = FileSupporter.parseFileName(newFileName);
        // 获取更新后的文件
        File file0 = new File(fileName.first, fileName.second, file.getFileAttribute(),
                file.getFirstDiskBlockIndex(), file.getLength());
        node.setFile(file0);
        // 获取该文件目录的磁盘块
        DiskBlock diskBlock = diskManager.getDiskBlock(node.getParent().getFile().getFirstDiskBlockIndex());
        // 获取该磁盘块里该文件的起始下标
        int diskBlockIndexOfFile = FileSupporter.getDiskBlockIndexOfFile(diskBlock.getBytes(), file);
        // 把文件转换成字节数组
        byte[] bytes = FileSupporter.parseFileToBytes(file0);
        // 更新文件信息到磁盘中
        diskManager.writeDiskBlock(bytes,0, FileConstant.SIZE_OF_FILE, diskBlock.getIndex(), diskBlockIndexOfFile);
        return file0;
    }

    /**
     * 把内容写入文件
     *
     * @param path 文件路径
     * @param content 文件内容
     * @throws IOException IO操作出错
     */
    public void writeFile(String path, String content) throws IOException {
        writeFile(path, content.getBytes(FileConstant.ENCODING_OF_FILE));
    }

    /**
     * 读取一个文件，使用UTF_8编码
     *
     * @param path 文件绝对路径
     * @return 读取的字符串
     * @throws IOException IO操作出错
     */
    public String readFile(String path) throws IOException {
        byte[] bytes = readFile(path, 0);
        return new String(bytes, FileConstant.ENCODING_OF_FILE);
    }

    /**
     * 获取磁盘容量信息
     *
     * @return Map<String, Double> 磁盘容量信息
     */
    public Map<String, Double> getDiskInfo() {
        return diskManager.getDiskInfo();
    }


    /**
     * 读取一个文件
     *
     * @param path 文件绝对路径
     * @param length 读取长度
     * @return 读取的字节
     * @throws NotFoundException 找不到该文件
     * @throws IllegalArgumentException 该路径指向的是一个文件夹
     * @throws IOException IO操作出错
     */
    private byte[] readFile(String path, int length) throws IOException {
        DirectoryTree.Node node = directoryTree.getNode(path);
        // 找不到该文件
        if (node == null) {
            throw new NotFoundException("找不到该文件");
        }
        // 该路径指向的是一个文件夹
        if (node.getFile().getFileAttribute().isDirectory()) {
            throw new IllegalArgumentException("该路径指向的是一个文件夹");
        }

        // 获取该文件的磁盘块列表
        List<DiskBlock> diskBlockList = diskManager.getDiskBlocksStartWith(node.getFile().getFirstDiskBlockIndex());
        // 最后一个磁盘块的文件结束标志下标
        int endOfFileSymbolIndex = FileSupporter.getEndOfFileSymbolIndex(
                diskBlockList.get(diskBlockList.size() - 1).getBytes());
        // 申请文件内容的空间
        byte[] bytes = new byte[(diskBlockList.size() - 1) * DiskConstant.BLOCK_SIZE + endOfFileSymbolIndex];
        for (int i = 0; i < diskBlockList.size(); i++) {
            byte[] bytes0 = diskBlockList.get(i).getBytes();
            // 对于最后一个磁盘块只读取到结束标志的下标处
            if (i == diskBlockList.size() - 1) {
                System.arraycopy(bytes0, 0, bytes, i * DiskConstant.BLOCK_SIZE,  endOfFileSymbolIndex);
            } else {
                System.arraycopy(bytes0, 0, bytes, i * DiskConstant.BLOCK_SIZE, bytes0.length);
            }
        }

        return bytes;
    }

    /**
     * 把内容写入文件
     *
     * @param path 文件路径
     * @param bytes 文件内容字节数组
     * @throws IOException IO操作出错
     * @throws NotFoundException 找不到该文件夹
     * @throws IllegalArgumentException 该路径不是指向一个文件夹
     */
    private void writeFile(String path, byte[] bytes) throws IOException {
        DirectoryTree.Node node = directoryTree.getNode(path);
        // 找不到该文件
        if (node == null) {
            throw new NotFoundException("找不到该文件");
        }
        // 该路径指向的是一个文件夹
        if (node.getFile().getFileAttribute().isDirectory()) {
            throw new IllegalArgumentException("该路径指向的是一个文件夹");
        }

        // 获取该文件的磁盘块列表
        List<DiskBlock> diskBlockList = diskManager.getDiskBlocksStartWith(node.getFile().getFirstDiskBlockIndex());
        byte[] bytes0 = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, bytes0, 0, bytes.length);
        // 设置文件结束标识符
        bytes0[bytes0.length - 1] = FileConstant.END_OF_FILE;
        // 需要使用的磁盘块数量
        int numberOfDiskBlocks = (int) Math.ceil((double)bytes0.length / DiskConstant.BLOCK_SIZE);
        // 把字节数组的内容写入磁盘
        for (int i = 0, lastDiskBlockIndex = diskBlockList.get(0).getIndex(); i < numberOfDiskBlocks; i++) {
            DiskBlock diskBlock;
            // 如果原本的磁盘块数量还有剩余，直接写到原本的磁盘块里
            if (diskBlockList.size() > i) {
                diskBlock = diskBlockList.get(i);
                lastDiskBlockIndex = diskBlock.getIndex();
            }
            // 否则申请一块磁盘块拼接在上一块磁盘块的后面
            else {
                diskBlock = diskManager.allocateDiskBlockPreviousWith(lastDiskBlockIndex);
                lastDiskBlockIndex = diskBlock.getIndex();
            }
            // 最后一个磁盘块，只写一部分字节
            if (i + 1 == numberOfDiskBlocks) {
                diskManager.writeDiskBlock(bytes0, i * DiskConstant.BLOCK_SIZE,
                        bytes0.length % DiskConstant.BLOCK_SIZE, diskBlock.getIndex(), 0);
            } else {
                diskManager.writeDiskBlock(bytes0, i * DiskConstant.BLOCK_SIZE, DiskConstant.BLOCK_SIZE,
                        diskBlock.getIndex(), 0);
            }
        }
        // 如果原来的磁盘块数量大于所需要的磁盘块数量且原来的磁盘块数量大于0，释放掉多余的磁盘块
        if (diskBlockList.size() > numberOfDiskBlocks && diskBlockList.size() > 0) {
            diskManager.releaseDiskBlocksPreviousWith(diskBlockList.get(numberOfDiskBlocks - 1).getIndex());
        }
    }

    /**
     * 创建一个文件，会为文件分配一块磁盘块，会添加到磁盘里，并更新文件树
     *
     * @param directoryPath 文件的文件夹
     * @param name 文件名
     * @param type 文件类型
     * @param fileAttribute 文件属性
     * @param firstDiskBlockIndex 第一块磁盘块的下标
     * @return File 新创建的文件
     * @throws IOException IO操作出错
     */
    private File createFile(String directoryPath, String name, String type, FileAttribute fileAttribute,
                            int firstDiskBlockIndex) throws IOException {
        // 新建一个文件
        File file = new File(name, type, fileAttribute, firstDiskBlockIndex, fileAttribute.isDirectory() ? 0 : 1);

        // 添加节点
        DirectoryTree.Node parent = directoryTree.addNode(directoryPath, file);
        int parentFirstDiskBlockIndex = parent.getFile().getFirstDiskBlockIndex();
        // 获取父节点的磁盘块
        DiskBlock diskBlock = diskManager.getDiskBlock(parentFirstDiskBlockIndex);
        int emptySpaceIndex = FileSupporter.findEmptySpaceOfDiskBlock(diskBlock.getBytes(), FileConstant.SIZE_OF_FILE,
                FileConstant.EMPTY_FILE_SYMBOL);
        // 把文件转换成字节数组
        byte[] bytes = FileSupporter.parseFileToBytes(file);
        // 更新文件信息到磁盘中
        diskManager.writeDiskBlock(bytes,0, FileConstant.SIZE_OF_FILE, parentFirstDiskBlockIndex, emptySpaceIndex);
        return file;
    }

    /**
     * 文件管理器初始化方法
     */
    private void init() throws IOException {
        // 读取根目录磁盘块
        DiskBlock rootDiskBlock = diskManager.getDiskBlock(FileConstant.DISK_BLOCK_NUMBER_OF_ROOT_DIRECTORY);
        // 解析成文件列表
        List<File> children = FileSupporter.parseDiskBlockToFileList(rootDiskBlock);
        // 新建根目录文件
        File root = new File("/", "", new FileAttribute(false, true, true, true),
                FileConstant.DISK_BLOCK_NUMBER_OF_ROOT_DIRECTORY, children.size());
        // 设置根节点
        directoryTree = new DirectoryTree(root);

        // 从根节点递归初始化
        initDirectory(directoryTree.getRoot());
    }

    /**
     * 初始化目录，递归调用
     *
     * @param directory 目录
     * @throws IOException IO操作出错
     */
    private void initDirectory(DirectoryTree.Node directory) throws IOException {
        // 把目录的所有节点添加到目录里
        // 拿到此文件的磁盘块
        DiskBlock diskBlock = diskManager.getDiskBlock(directory.getFile().getFirstDiskBlockIndex());
        // 把此磁盘块解析成文件列表
        List<File> children = FileSupporter.parseDiskBlockToFileList(diskBlock);
        // 添加此目录下的所有子节点
        for (File child : children) {
            // 如果子节点是目录，把子节点添加到目录里，再递归调用初始化子目录
            if (child.getFileAttribute().isDirectory()) {
                DirectoryTree.Node node = new DirectoryTree.Node(directory, new ArrayList<>(), child);
                // 先把该子节点文件添加到目录里
                directoryTree.addNode(directory, node);
                // 再递归调用此方法初始化子节点
                initDirectory(node);
            }
            // 如果子节点是普通文件，把子节点添加到目录里
            else {
                DirectoryTree.Node node = new DirectoryTree.Node(directory, null, child);
                // 先把该子节点文件添加到目录里
                directoryTree.addNode(directory, node);
            }
        }
    }

    /**
     * 已经被打开的文件
     */
    private static class OpenedFile {
        /**
         * 文件绝对路径
         */
        private String absolutePath;

        /**
         * 文件
         */
        private File file;

        /**
         * 文件的打开方式
         */
        private OpenMode openMode;

        /**
         * 读指针
         */
        private Pointer readPointer;

        /**
         * 写指针
         */
        private Pointer writePointer;

        public OpenedFile(String absolutePath, File file, OpenMode openMode, Pointer readPointer, Pointer writePointer) {
            this.absolutePath = absolutePath;
            this.file = file;
            this.openMode = openMode;
            this.readPointer = readPointer;
            this.writePointer = writePointer;
        }

        public String getAbsolutePath() {
            return absolutePath;
        }

        public void setAbsolutePath(String absolutePath) {
            this.absolutePath = absolutePath;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public OpenMode getOpenMode() {
            return openMode;
        }

        public void setOpenMode(OpenMode openMode) {
            this.openMode = openMode;
        }

        public Pointer getReadPointer() {
            return readPointer;
        }

        public void setReadPointer(Pointer readPointer) {
            this.readPointer = readPointer;
        }

        public Pointer getWritePointer() {
            return writePointer;
        }

        public void setWritePointer(Pointer writePointer) {
            this.writePointer = writePointer;
        }

        @Override
        public String toString() {
            return "OpenedFile{" +
                    "absolutePath='" + absolutePath + '\'' +
                    ", file=" + file +
                    ", openMode=" + openMode +
                    ", readPointer=" + readPointer +
                    ", writePointer=" + writePointer +
                    '}';
        }
    }

    /**
     * 读写指针
     */
    private static class Pointer {
        /**
         * 当前操作的磁盘块号
         */
        private int diskBlockNum;

        /**
         * 当前操作的磁盘块内的下标
         */
        private int diskBlockIndex;

        public Pointer(int diskBlockNum, int diskBlockIndex) {
            this.diskBlockNum = diskBlockNum;
            this.diskBlockIndex = diskBlockIndex;
        }

        public int getDiskBlockNum() {
            return diskBlockNum;
        }

        public void setDiskBlockNum(int diskBlockNum) {
            this.diskBlockNum = diskBlockNum;
        }

        public int getDiskBlockIndex() {
            return diskBlockIndex;
        }

        public void setDiskBlockIndex(int diskBlockIndex) {
            this.diskBlockIndex = diskBlockIndex;
        }

        @Override
        public String toString() {
            return "Pointer{" +
                    "diskBlockNum=" + diskBlockNum +
                    ", diskBlockIndex=" + diskBlockIndex +
                    '}';
        }
    }

    /**
     * 描述: 文件的打开方式
     */
    private enum OpenMode {
        /**
         * 以读模式打开文件
         */
        READ,

        /**
         * 以写模式打开文件
         */
        WRITE
    }

}
