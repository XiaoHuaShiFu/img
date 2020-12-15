package top.xiaohuashifu.filesystem.exception;

/**
 * 描述: 重复异常
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-01 16:09
 */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
