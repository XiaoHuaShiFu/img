package top.xiaohuashifu.filesystem.file;

/**
 * 描述: 文件
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-30 15:11
 */
public class File {

    /**
     * 文件名
     */
    private final String name;

    /**
     * 文件类型
     */
    private final String type;

    /**
     * 文件属性
     */
    private final FileAttribute fileAttribute;

    /**
     * 起始盘块号
     */
    private final int firstDiskBlockIndex;

    /**
     * 文件长度
     */
    private final int length;

    public File(String name, String type, FileAttribute fileAttribute, int firstDiskBlockIndex, int length) {
        this.name = name;
        this.type = type;
        this.fileAttribute = fileAttribute;
        this.firstDiskBlockIndex = firstDiskBlockIndex;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public FileAttribute getFileAttribute() {
        return fileAttribute;
    }

    public int getFirstDiskBlockIndex() {
        return firstDiskBlockIndex;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", fileAttribute=" + fileAttribute +
                ", firstDiskBlockIndex=" + firstDiskBlockIndex +
                ", length=" + length +
                '}';
    }
}
