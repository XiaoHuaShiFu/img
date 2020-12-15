package top.xiaohuashifu.filesystem.util;

/**
 * 描述: 关于字节类型的操作
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-30 15:37
 */
public class ByteUtils {

    /**
     * 字节类型转换成布尔型数组
     *
     * @param b 字节
     * @return boolean[]
     */
    public static boolean[] byteToBooleans(byte b) {
        char[] chars = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1).toCharArray();
        boolean[] booleans = new boolean[8];
        for (int i = 0; i < 8; i++) {
            booleans[i] = chars[i] == '1';
        }
        return booleans;
    }

    /**
     * 字节数组转字符串
     *
     * @param bytes 字节数组
     * @param offset 起始下标
     * @param length 长度
     * @return String 字符串
     */
    public static String bytesToString(byte[] bytes, int offset, int length) {
        char[] chars = new char[length];
        for (int i = 0, j = 0; i < length; i++) {
            chars[j++] = (char) bytes[i + offset];
        }
        return String.valueOf(chars);
    }

}
