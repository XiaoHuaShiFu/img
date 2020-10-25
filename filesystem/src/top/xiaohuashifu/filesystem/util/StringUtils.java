package top.xiaohuashifu.filesystem.util;

/**
 * 描述: 字符串相关工具类
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-02 1:48
 */
public class StringUtils {

    public static boolean isBlank(String s) {
        return s == null || "".equals(s.trim());
    }

}
