package top.xiaohuashifu.filesystem.disk;

import java.util.Arrays;

/**
 * 描述: 磁盘块
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-29 17:52
 */
public class DiskBlock {

    private int index;

    private byte[] bytes;

    public DiskBlock(int index, byte[] bytes) {
        this.bytes = bytes;
        this.index = index;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "DiskBlock{" +
                "index=" + index +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
