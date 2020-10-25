package top.xiaohuashifu.filesystem.page.file;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import top.xiaohuashifu.filesystem.exception.NotFoundException;
import top.xiaohuashifu.filesystem.file.manager.FileManager;
import top.xiaohuashifu.filesystem.page.util.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 描述: 一个文件的页面
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-04 18:04
 */
public class File implements Initializable {
    @FXML
    public TextArea content;

    /**
     * @param fileManager 文件管理器
     * @param path        文件路径
     */
    public File(FileManager fileManager, String path) {
        this.fileManager = fileManager;
        this.path = path;
    }

    /**
     * 文件管理器
     */
    private FileManager fileManager;

    /**
     * 文件路径
     */
    private String path;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // 读取文件内容
            String s = fileManager.readFile(path);
            // 设置文件内容
            content.setText(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存文件
     *
     * @param actionEvent 事件
     */
    public void onSave(ActionEvent actionEvent) {
        try {
            fileManager.writeFile(path, content.getText());
        } catch (NotFoundException e) {
            Component.alert("提示", "文件不存在");
        } catch (IllegalArgumentException e) {
            Component.alert("提示", "该路径不是指向一个文件夹");
        } catch (IOException e) {
            Component.alert("提示", "IO操作出错");
        }
    }

}
