package top.xiaohuashifu.filesystem.file.allocation;

import java.util.*;

/**
 * 描述: 文件分配表
 * 此类不保证操作的安全性，不声明任何显式异常
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-28 1:43
 */
public class FileAllocationTable {

    /**
     * 文件分配表项数组
     */
    public Item[] items;

    /**
     * 初始化文件分配表，包括初始化保留项
     * @param items 文件分配表的字节数组
     */
    public FileAllocationTable(byte[] items) {
        init(items);
    }

    /**
     * 获取以startIndex为起点的文件分配表项列表
     *
     * @param startIndex 文件分配表起点下标
     * @return startIndex为起点的文件分配表项列表
     */
    public List<Item> getItemsStartWith(int startIndex) {
        // 该下标指向文件分配表项
        if (startIndex < FileAllocationTableConstant.NUMBER_OF_FAT_DISK_BLOCKS) {
            return Collections.singletonList(getItem(startIndex));
        }

        // 该磁盘分配表项指向空盘块
        if (getItem(startIndex).getNext() == FileAllocationTableConstant.EMPTY) {
            return Collections.singletonList(getItem(startIndex));
        }

        // 生成该文件的盘块链
        List<Item> itemList = new ArrayList<>();
        Item item = getItem(startIndex);
        itemList.add(item);
        while (item.getNext() != FileAllocationTableConstant.END) {
            item = getItem(item.getNext());
            itemList.add(item);
        }
        return itemList;
    }

    /**
     * 获取一个文件分配表项
     * @param index 文件分配表项下标
     * @return 文件分配表项
     */
    public Item getItem(int index) {
        return items[index];
    }

    /**
     * 随机分配一个空文件分配表项
     * 该文件分配项会作为最后一个项，也就是value==FileAllocationTableConstant.END
     * 并更新文件分配表的内容
     *
     * 如果已经没有空闲磁盘块会返回null
     *
     * @return Item 文件分配表项
     */
    public Item allocateItem() {
        Item item = getEmptyItem();
        // 没有空闲磁盘块
        if (item == null) {
            return null;
        }

        // 更新文件分配表
        Item newItem = new Item(item.index, FileAllocationTableConstant.END);
        updateItem(newItem);
        return newItem;
    }

    /**
     * 随机获取一个空文件分配表项
     * 该文件分配项会插入已经分配的文件分配表项链之中
     *
     * 如果已经没有空闲磁盘块会返回null
     *
     * @param previous 前一个分配表下标
     * @return Item 新分配的文件分配表项
     */
    public Item allocateItem(int previous) {
        Item item = getEmptyItem();
        // 没有空闲磁盘块
        if (item == null) {
            return null;
        }

        // 前一个文件分配表项
        Item newPreviousItem = new Item(previous, item.index);
        // 新分配的文件分配表项
        Item newItem = new Item(item.index, getItem(previous).next);
        // 更新文件分配表
        updateItem(newPreviousItem);
        updateItem(newItem);
        return newItem;
    }

    /**
     * 释放文件分配表项，链式的从start下标开始
     * 用于释放单项或整个文件
     *
     * @param start 被释放的文件分配表项的第一项
     */
    public void releaseItemsStartWith(int start) {
        releaseItemsPreviousWith(start);
        updateItem(new Item(start, 0));
    }

    /**
     * 释放文件分配表项，链式的从previous下标的下一项开始
     * 用于修改文件内容时使用
     *
     * @param previous 被释放的文件分配表项的前一项
     */
    public void releaseItemsPreviousWith(int previous) {
        while (getItem(previous).getNext() != -1) {
            releaseItem(previous);
        }
    }

    /**
     * 释放文件分配表项，从一个链中摘除某一项
     *
     * @param previous 被释放的文件分配表项的前一项
     */
    private void releaseItem(int previous) {
        Item previousItem = getItem(previous);
        Item releasedItem = getItem(previousItem.next);
        Item newPreviousItem = new Item(previous, releasedItem.next);
        Item newReleasedItem = new Item(releasedItem.index, FileAllocationTableConstant.EMPTY);
        updateItem(newPreviousItem);
        updateItem(newReleasedItem);
    }

    @Override
    public String toString() {
        return "FileAllocationTable{" +
                "items=" + Arrays.toString(items) +
                '}';
    }

    /**
     * 随机获取一个空文件分配表项
     *
     * 如果已经没有空闲磁盘块会返回null
     * @return Item 文件分配表项
     */
    private Item getEmptyItem() {
        for (int i = FileAllocationTableConstant.NUMBER_OF_FAT_DISK_BLOCKS;
             i < FileAllocationTableConstant.LENGTH; i++) {
            if (getItem(i).next == FileAllocationTableConstant.EMPTY) {
                return getItem(i);
            }
        }
        return null;
    }

    /**
     * 初始化文件分配表
     * @param items 文件分配表的字节数组
     */
    private void init(byte[] items) {
        this.items = new Item[items.length];
        for (int i = 0; i < items.length; i++) {
            this.items[i] = new Item(i, items[i]);
        }
    }

    /**
     * 更新分配表的一项
     * @param item 分配表项
     */
    private void updateItem(Item item) {
        items[item.getIndex()] = item;
    }

    /**
     * 文件分配表的项
     */
    static final public class Item implements Cloneable {

        /**
         * 当前文件分配表项的下标
         */
        private final int index;

        /**
         * 指向该文件的文件分配表的下一个下标
         */
        private final int next;

        private Item(int index, int next) {
            this.index = index;
            this.next = next;
        }

        public int getNext() {
            return next;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return index == item.index;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index);
        }

        @Override
        public String toString() {
            return "Item{" +
                    "index=" + index +
                    ", next=" + next +
                    '}';
        }
    }

}
