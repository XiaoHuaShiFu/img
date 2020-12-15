package top.xiaohuashifu.filesystem.util;

/**
 * 描述: 对两个元素的封装
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-03 22:21
 */
public class Pair<T1, T2> {
    public final T1 first;
    public final T2 second;
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
