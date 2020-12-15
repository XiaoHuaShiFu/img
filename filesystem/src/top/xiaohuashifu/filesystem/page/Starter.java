package top.xiaohuashifu.filesystem.page;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.xiaohuashifu.filesystem.page.index.Index;

import java.util.Objects;

/**
 * 描述: 图形界面启动方法
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-04 0:02
 */
public class Starter extends Application {

    /**
     * 启动方式1
     * @param primaryStage Stage
     * @throws Exception .
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(new Index());
        Parent parent = fxmlLoader.load(Objects.requireNonNull(
                Starter.class.getClassLoader().getResourceAsStream(
                        "top/xiaohuashifu/filesystem/page/index/index.fxml")));
        Scene scene = new Scene(parent);
        primaryStage.setTitle("文件管理系统");
        primaryStage.getIcons().add(new Image("top/xiaohuashifu/filesystem/page/image/folder.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 启动方式2
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        launch(args);
    }

}

