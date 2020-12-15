package top.xiaohuashifu.filesystem.page.index;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import top.xiaohuashifu.filesystem.disk.DiskConstant;
import top.xiaohuashifu.filesystem.disk.DiskManager;
import top.xiaohuashifu.filesystem.exception.DuplicateException;
import top.xiaohuashifu.filesystem.exception.IllegalOperationException;
import top.xiaohuashifu.filesystem.exception.NotFoundException;
import top.xiaohuashifu.filesystem.file.File;
import top.xiaohuashifu.filesystem.file.FileSupporter;
import top.xiaohuashifu.filesystem.file.manager.FileManager;
import top.xiaohuashifu.filesystem.file.manager.SimpleFileManager;
import top.xiaohuashifu.filesystem.page.Starter;
import top.xiaohuashifu.filesystem.page.util.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-11-03 23:33
 */
public class Index implements Initializable {

    /**
     * 根目录路径
     */
    private static final String ROOT = "/";

    /**
     * 当前路径
     */
    private String currentPath;

    /**
     * 文件管理器
     */
    private FileManager fileManager;

    @FXML
    public TextField path;

    @FXML
    public VBox container;

    @FXML
    public GridPane gridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            fileManager = new SimpleFileManager(new DiskManager(DiskConstant.DISK_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        changeDirectory(ROOT);
        currentPath = ROOT;
        gridPane.setOnMouseClicked(this::onClickGridPane);
        showDiskInfo(null);
    }

    /**
     * 点击文件夹
     *
     * @param mouseEvent 鼠标事件
     */
    private void onClickGridPane(MouseEvent mouseEvent) {
        // 右键单击事件
        if (mouseEvent.getButton().name().equals("SECONDARY")
                && mouseEvent.getTarget().getClass().getName().equals("javafx.scene.layout.GridPane")) {
            MenuItem createFile = new MenuItem("新建文件");
            createFile.setOnAction(this::createFile);
            MenuItem createDirectory = new MenuItem("新建文件夹");
            createDirectory.setOnAction(this::createDirectory);
            MenuItem showDiskInfo = new MenuItem("磁盘信息");
            showDiskInfo.setOnAction(this::showDiskInfo);
            ContextMenu contextMenu = new ContextMenu(createFile, createDirectory, showDiskInfo);
            contextMenu.show((GridPane) mouseEvent.getSource(), Side.RIGHT, mouseEvent.getX() - 600,
                    mouseEvent.getY());
        }
    }

    /**
     * 显示磁盘信息
     *
     * @param actionEvent 事件
     */
    private PieChart pieChart = null;
    private void showDiskInfo(ActionEvent actionEvent) {
        Map<String, Double> diskInfo = fileManager.getDiskInfo();
        try {
            Stage stage = new Stage();
            stage.setTitle("磁盘信息");
            stage.getIcons().add(new Image("top/xiaohuashifu/filesystem/page/image/disk.png"));
            stage.setX(100);
            stage.setY(100);
            stage.setOnCloseRequest((handle)-> {
                this.pieChart = null;
            });

            final PieChart[] innerPieChart = {null};
            new Application() {
                @Override
                public void start(Stage primaryStage) throws Exception {
                    final PieChart pieChart = new PieChart();
                    pieChart.setData(getChartData());
                    primaryStage.setTitle("磁盘容量");
                    innerPieChart[0] = pieChart;

                    StackPane root = new StackPane();
                    root.getChildren().add(pieChart);
                    primaryStage.setScene(new Scene(root, 400, 250));
                    primaryStage.show();
                }
                private ObservableList<PieChart.Data> getChartData() {
                    ObservableList<PieChart.Data> answer = FXCollections.observableArrayList();
                    answer.addAll(new PieChart.Data("已用" + diskInfo.get("usedCount").intValue(),
                                    diskInfo.get("usedCount")),
                            new PieChart.Data("剩余" + diskInfo.get("remainCount").intValue(),
                                    diskInfo.get("remainCount")));
                    return answer;
                }
            }.start(stage);
            this.pieChart = innerPieChart[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 磁盘容量
     * 改变饼形图
     */
    public void changePieChartInfo() {
        Map<String, Double> diskInfo = fileManager.getDiskInfo();
        if (pieChart != null) {
            ObservableList<PieChart.Data> answer = FXCollections.observableArrayList();
            answer.addAll(new PieChart.Data("已用" + diskInfo.get("usedCount").intValue(),
                            diskInfo.get("usedCount")),
                    new PieChart.Data("剩余" + diskInfo.get("remainCount").intValue(),
                            diskInfo.get("remainCount")));
            pieChart.setData(answer);
        }
    }

    /**
     * 新建文件
     *
     * @param actionEvent 事件
     */
    private void createFile(ActionEvent actionEvent) {
        try {
            // 新建文件
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("新建文件");
            dialog.setHeaderText(null);
            dialog.setContentText("文件名");
            Optional<String> result = dialog.showAndWait();
            // 用户点击取消
            if (!result.isPresent()) {
                return;
            }
            // 新建文件
            fileManager.createFile(currentPath, result.get(), false);
            // 刷新文件夹
            changeDirectory(currentPath);
            // 刷新磁盘容量饼状图
            changePieChartInfo();
        } catch (IOException e) {
            Component.alert("提示", "IO操作出错");
        } catch (NotFoundException e) {
            Component.alert("提示", "目录不存在");
        } catch (IllegalArgumentException e) {
            Component.alert("提示", "非法文件名");
        } catch (DuplicateException e) {
            Component.alert("提示", "文件名已经存在");
        }
    }

    /**
     * 新建文件夹
     *
     * @param actionEvent 事件
     */
    private void createDirectory(ActionEvent actionEvent) {
        try {
            // 新建文件
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("新建文件夹");
            dialog.setHeaderText(null);
            dialog.setContentText("文件名");
            Optional<String> result = dialog.showAndWait();
            // 用户点击取消
            if (!result.isPresent()) {
                return;
            }
            // 新建文件
            fileManager.createDirectory(currentPath, result.get(), false);
            // 刷新文件夹
            changeDirectory(currentPath);
            // 刷新磁盘容量饼状图
            changePieChartInfo();
        } catch (IOException e) {
            Component.alert("提示", "IO操作出错");
        } catch (NotFoundException e) {
            Component.alert("提示", "目录不存在");
        } catch (IllegalArgumentException e) {
            Component.alert("提示", "非法文件名");
        } catch (DuplicateException e) {
            Component.alert("提示", "文件名已经存在");
        }
    }

    /**
     * 点击一个文件
     *
     * @param mouseEvent 鼠标事件
     */
    private void onClick(MouseEvent mouseEvent) {
        VBox vBox = (VBox) mouseEvent.getSource();
        // 如果当前目录是根目录，就不用在前面加“/”
        String path;
        if (currentPath.equals(ROOT)) {
            path = currentPath + vBox.getId();
        } else {
            path = currentPath + "/" + vBox.getId();
        }
        // 左键双击事件
        if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton().name().equals("PRIMARY")) {
            // 判断所点击的文件是不是一个目录
            if ((boolean) vBox.getUserData()) {
                changeDirectory(path);
            } else {
                openFile(path, mouseEvent.getX(), mouseEvent.getY());
            }
        }
        // 右键单击事件
        else if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton().name().equals("SECONDARY")) {
            MenuItem delete = new MenuItem("删除");
            delete.setOnAction(this::deleteFile);
            MenuItem rename = new MenuItem("重命名");
            rename.setOnAction(this::renameFile);
            ContextMenu contextMenu = new ContextMenu(delete, rename);
            contextMenu.setId(path);
            contextMenu.show((VBox) mouseEvent.getSource(), Side.RIGHT, mouseEvent.getX() - 150, mouseEvent.getY());
        }
    }

    /**
     * 删除文件
     *
     * @param actionEvent 事件
     */
    private void deleteFile(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();
        try {
            // 删除文件
            fileManager.deleteFile(menuItem.getParentPopup().getId());
            // 刷新文件夹
            changeDirectory(currentPath);
            // 刷新磁盘容量饼状图
            changePieChartInfo();
        } catch (IOException e) {
            Component.alert("提示", "IO操作出错");
        } catch (NotFoundException e) {
            Component.alert("提示", "文件不存在");
        } catch (IllegalOperationException e) {
            Component.alert("提示", "无法删除非空目录");
        }
    }

    /**
     * 重命名文件
     *
     * @param actionEvent 事件
     */
    private void renameFile(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();
        try {
            // 重命名文件
            TextInputDialog dialog =
                    new TextInputDialog(FileSupporter.getFileName(menuItem.getParentPopup().getId()));
            dialog.setTitle("重命名");
            dialog.setHeaderText(null);
            dialog.setContentText("新文件名");
            Optional<String> result = dialog.showAndWait();
            // 用户点击取消重命名
            if (!result.isPresent()) {
                return;
            }
            // 重命名
            fileManager.updateFile(menuItem.getParentPopup().getId(), result.get());
            // 刷新文件夹
            changeDirectory(currentPath);
        } catch (IOException e) {
            Component.alert("提示", "IO操作出错");
        } catch (NotFoundException e) {
            Component.alert("提示", "文件不存在");
        }
    }

    /**
     * 返回上一层目录
     *
     * @param actionEvent 鼠标事件
     */
    public void onBack(ActionEvent actionEvent) {
        // 如果当前目录是根目录，不做任何操作
        if (currentPath.equals(ROOT)) {
            return;
        }
        // 切换目录
        int lastIndex = currentPath.lastIndexOf("/");
        String path;
        if (lastIndex == 0) {
            path = ROOT;
        } else {
            path = currentPath.substring(0, lastIndex);
        }
        changeDirectory(path);
    }

    /**
     * 从路径输入栏输入
     *
     * @param actionEvent 鼠标事件
     */
    public void onChangeDirectory(ActionEvent actionEvent) {
        TextField textField = (TextField) actionEvent.getSource();
        String text = textField.getText();
        File file;
        try {
            file = fileManager.getFile(text);
            // 如果文件是一个目录，改变目录
            if (file.getFileAttribute().isDirectory()) {
                changeDirectory(text);
            } else {
                openFile(text, 100, 100);
            }
        } catch (NotFoundException e) {
            Component.alert("提示", "请输入正确的路径");
        }
    }

    /**
     * 改变文件夹
     *
     * @param path 文件夹路径
     */
    private void changeDirectory(String path) {
        this.path.setText(path);
        this.currentPath = path;
        List<File> fileList = fileManager.getFileList(path);
        gridPane.getChildren().clear();
        for (int i = 0; i < fileList.size(); i++) {
            VBox vBox = createFile(FileSupporter.getFileName(fileList.get(i)),
                    fileList.get(i).getFileAttribute().isDirectory());
            gridPane.add(vBox, i % 4, i / 4);
        }
    }

    /**
     * 创建一个文件对象
     *
     * @param fileName    文件名
     * @param isDirectory 是不是目录
     * @return 文件对象
     */
    private VBox createFile(String fileName, boolean isDirectory) {
        // 新建一个图片对象
        ImageView imageView = new ImageView();
        imageView.setFitHeight(176);
        imageView.setFitWidth(150);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        if (isDirectory) {
            imageView.setImage(new Image("top/xiaohuashifu/filesystem/page/image/folder.png"));
        } else {
            imageView.setImage(new Image("top/xiaohuashifu/filesystem/page/image/document.png"));
        }

        // 新建一个文本对象，存放文件名
        Text textField = new Text();
        textField.setFont(new Font(20));
        textField.setStyle("-fx-background-color: #f4f4f4");
        textField.setFont(new Font("Candara", 14));
        textField.setText(fileName);

        // 新建一个文件的显式对象
        VBox vBox = new VBox();
        vBox.setMaxHeight(205);
        vBox.setPrefHeight(205);
        vBox.setPrefWidth(150);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-max-height: 100%");
        vBox.setId(fileName);
        // 设置文件是不是目录的标志
        if (isDirectory) {
            vBox.setUserData(true);
        } else {
            vBox.setUserData(false);
        }
        vBox.setOnMouseClicked(this::onClick);
        vBox.getChildren().addAll(imageView, textField);
        return vBox;
    }

    /**
     * 打开一个文件
     *
     * @param path 文件路径
     */
    private void openFile(String path, double x, double y) {
        top.xiaohuashifu.filesystem.page.file.File file =
                new top.xiaohuashifu.filesystem.page.file.File(this, fileManager, path);
        try {
            Stage stage = new Stage();
            stage.setTitle(FileSupporter.getFileName(fileManager.getFile(path)) + " - 文本文件");
            stage.getIcons().add(new Image("top/xiaohuashifu/filesystem/page/image/document.png"));
            stage.setX(x);
            stage.setY(y);
            new Application() {
                @Override
                public void start(Stage primaryStage) throws Exception {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setController(file);
                    Parent parent = fxmlLoader.load(Objects.requireNonNull(
                            Starter.class.getClassLoader().getResourceAsStream(
                                    "top/xiaohuashifu/filesystem/page/file/file.fxml")));
                    Scene scene = new Scene(parent);
                    primaryStage.setScene(scene);
                    primaryStage.show();
                }
            }.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
