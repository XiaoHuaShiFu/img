package top.xiaohuashifu.filesystem.directory;

import top.xiaohuashifu.filesystem.exception.NotFoundException;
import top.xiaohuashifu.filesystem.file.File;
import top.xiaohuashifu.filesystem.file.FileConstant;
import top.xiaohuashifu.filesystem.file.FileSupporter;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 目录树
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-10-30 20:15
 */
public class DirectoryTree {

    /**
     * 目录分隔符
     */
    private static final String SEPARATOR =  "/";

    /**
     * 根目录路径
     */
    private static final String ROOT_PATH = "/";

    /**
     * 根目录
     */
    private Node root;

    public DirectoryTree(File rootFile) {
        this.root = new Node(null, new ArrayList<>(), rootFile);
    }

    /**
     * 增加一个子节点
     *
     * @param directory Node 目录节点
     * @param file Node 子节点
     * @return 添加节点之后的目录
     * @throws NotFoundException 目录不存在
     * @throws IndexOutOfBoundsException 目录节点数已到达上限
     */
    public Node addNode(Node directory, Node file) {
        // 目录不存在
        if (directory == null) {
            throw new NotFoundException("目录不存在");
        }

        // 目录节点已经到达上限
        if (directory.children.size() >= FileConstant.MAX_FILE_NUMBER_OF_DIRECTORY) {
            throw new IndexOutOfBoundsException("目录节点数已到达上限");
        }

        directory.children.add(file);
        return directory;
    }

    /**
     * 增加一个子节点
     *
     * @param directory Node 目录节点
     * @param file File 文件节点
     * @return 添加节点之后的目录
     */
    public Node addNode(Node directory, File file) {
        Node child;
        // 如果是目录就初始化子节点列表
        if (file.getFileAttribute().isDirectory()) {
            child = new Node(directory, new ArrayList<>(), file);
        } else {
            child = new Node(directory, null, file);
        }
        return addNode(directory, child);
    }

    /**
     * 增加一个子节点
     *
     * @param directoryPath String 目录路径
     * @param file Node 要添加的节点
     * @return 添加节点之后的目录
     */
    public Node addNode(String directoryPath, Node file) {
        return addNode(getNode(directoryPath), file);
    }

    /**
     * 增加一个子节点
     *
     * @param directoryPath String 目录路径
     * @param file File 节点的文件
     * @return 添加节点之后的目录
     * @exception NotFoundException,IndexOutOfBoundsException .
     */
    public Node addNode(String directoryPath, File file) throws NotFoundException, IndexOutOfBoundsException {
        return addNode(getNode(directoryPath), file);
    }

    /**
     * 寻找一个文件，可以是目录或者是普通文件
     *
     * @param path 文件路径
     * @return Node 文件节点
     */
    public Node getNode(String path) {
        // 路径不是以"/"为开头的，非法路径
        if (!path.startsWith(SEPARATOR)) {
            return null;
        }

        // 解析路径
        String[] directories = path.trim().split(SEPARATOR);
        Node node = root;
        for (int i = 1; i < directories.length; i++) {
            for (int j = 0; j < node.children.size(); j++) {
                Node child = node.children.get(j);
                // 如果此文件是目录文件且文件名和路径相符合
                if (FileSupporter.getFileName(child.file).equals(directories[i])) {
                    if (i == directories.length - 1) {
                        return child;
                    }
                    // 如果此文件是目录
                    if (child.file.getFileAttribute().isDirectory()) {
                        node = child;
                        break;
                    }
                }
                // 如果遍历完目录还是没找到，说明该目录不存在
                if (j == node.children.size() - 1) {
                    return null;
                }
            }
            // 否则如果已经是最后一层了，表示找到文件
            if (i == directories.length - 1) {
                return null;
            }
        }
        // 如果以上情况都不符合，代表该路径是根目录
        return node;
    }

    /**
     * 删除一个子节点
     * @param path 节点的绝对路径
     * @return Node 被删除的节点
     */
    public Node deleteNode(String path) {
        int index = path.trim().lastIndexOf(SEPARATOR);
        String directoryPath;
        String fileName = path.substring(index + 1);
        // 该节点在根目录下
        if (index == 0) {
            directoryPath = ROOT_PATH;
        } else {
            directoryPath = path.substring(0, index);
        }
        return deleteNode(directoryPath, fileName);
    }

    /**
     * 删除一个节点
     *
     * @param directoryPath 目录路径
     * @param fileName 文件名
     * @return Node 被删除的节点
     * @throws NotFoundException 未找到文件
     */
    public Node deleteNode(String directoryPath, String fileName) {
        Node directory = getNode(directoryPath);
        if (directory == null) {
            throw new NotFoundException("未找到文件");
        }

        for (Node child : directory.children) {
            if (fileName.equals(FileSupporter.getFileName(child.getFile()))) {
                directory.children.remove(child);
                return child;
            }
        }
        throw new NotFoundException("未找到文件");
    }

    /**
     * 获取根节点
     * @return 根节点
     */
    public Node getRoot() {
        return root;
    }


    /**
     * 目录树的节点，每个节点代表一个目录文件或者普通文件
     * 叶子节点代表普通文件，非叶子节点代表目录文件
     */
    public static class Node {
        private Node parent;
        private List<Node> children;
        private File file;

        public Node(Node parent, List<Node> children, File file) {
            this.parent = parent;
            this.children = children;
            this.file = file;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "children=" + children +
                    ", file=" + file +
                    '}';
        }
    }

}
