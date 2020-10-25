package top.xiaohuashifu.filesystem.exception;

/**
 * 描述: 未找到异常
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-31 20:42
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
