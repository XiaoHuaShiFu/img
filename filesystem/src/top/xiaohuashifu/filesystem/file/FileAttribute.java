package top.xiaohuashifu.filesystem.file;

/**
 * 描述: 文件属性类型
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-31 19:11
 */
public class FileAttribute {

    /**
     * 是否是只读文件
     */
    private final boolean readOnly;

    /**
     * 是否是系统文件
     */
    private final boolean system;

    /**
     * 是否是读写文件
     */
    private final boolean readWrite;

    /**
     * 是否是目录文件
     */
    private final boolean directory;

    public FileAttribute(boolean readOnly, boolean system, boolean readWrite, boolean directory) {
        this.readOnly = readOnly;
        this.system = system;
        this.readWrite = readWrite;
        this.directory = directory;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isSystem() {
        return system;
    }

    public boolean isReadWrite() {
        return readWrite;
    }

    public boolean isDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return "FileAttribute{" +
                "readOnly=" + readOnly +
                ", system=" + system +
                ", readWrite=" + readWrite +
                ", directory=" + directory +
                '}';
    }
}