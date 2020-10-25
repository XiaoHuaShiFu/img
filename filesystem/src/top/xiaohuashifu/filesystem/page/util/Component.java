package top.xiaohuashifu.filesystem.page.util;

import javafx.scene.control.Alert;

/**
 * 描述: JavaFX的一些组件
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-04 23:31
 */
public class Component {

    /**
     * 生成一个弹窗提醒
     *
     * @param title 标题
     * @param contentText 提醒内容
     */
    public static void alert(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
