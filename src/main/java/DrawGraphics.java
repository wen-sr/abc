import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wen-sr
 */
@SuppressWarnings("serial")
public class DrawGraphics extends JFrame implements ActionListener,ItemListener,MouseWheelListener,MouseMotionListener,MouseListener{
    //定义参数对象，该对象保存画图需要的参数信息
    static MyObject mo;
    static MyObject mo_bk;

    JButton bigger = null;
    JButton smaller = null;
    JButton clear = null;
    JFileChooser fc = new JFileChooser();
    JTextField textfield = null;
    JButton Choosefiles = null;
    JComboBox jComboBox = null;

    static File file = null;
    //放大的倍数
    static int time = 2;

    private int width = 1600;
    private int height = 1200;

    MyPanel mp = null;


    //鼠标监听属性
    boolean moveEnable = false;
    Point point1 = null;
    Point point2 = null;

    public static void main(String[] args) {
        mo = new MyObject();
        //实例化画图对象
        DrawGraphics dg = new DrawGraphics(mo);
    }

    /**
     * 构造函数
     */
    public DrawGraphics(final MyObject mo) {
        //实例化组件
        JPanel mp_0 = new JPanel();
        mp_0.setBackground(Color.GRAY);

        mp = new MyPanel(mo);

        Choosefiles = new JButton("选择文件");
        bigger = new JButton("放大");
        smaller = new JButton("缩小");
        clear = new JButton("清屏");
        jComboBox = new JComboBox(new String[]{"原图", "简化图"});

        mp_0.add(Choosefiles);
        mp_0.add(jComboBox);
        mp_0.add(bigger);
        mp_0.add(smaller);
        mp_0.add(clear);
        this.add(mp_0, BorderLayout.NORTH);
        this.add(mp, BorderLayout.CENTER);

        //注册按钮监听
        bigger.addActionListener(this);
        smaller.addActionListener(this);
        clear.addActionListener(this);
        Choosefiles.addActionListener(this);
        //注册下拉框改变监听
        jComboBox.addItemListener(this);
        //注册鼠标滚轮监听
        this.addMouseWheelListener(this);
        //注册鼠标事件
        mp.addMouseMotionListener(this);
        mp.addMouseListener(this);


        //设置窗体大小
//		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setSize(width, height);
        //设置窗体显示的位置
        this.setLocation(0, 0);
        //设置窗体大小不可变
//		this.setResizable(false);

        //设置关闭窗口时后台也退出
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗体可见
        this.setVisible(true);
    }



    /**
     * 获取文件内容封装到MyObject对象中
     *
     * @param file
     * @param mo
     */
    private MyObject getFileData(File file, MyObject mo) throws IOException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(file));

        BufferedReader bufferedReader = new BufferedReader(read);

        String lineTxt = null;

        //用来保存从txt文件读取的数据，每一行保存成一个对象
        List<String> list = new ArrayList<String>();
        while ((lineTxt = bufferedReader.readLine()) != null) {
            list.add(lineTxt);
        }
        if (list.size() == 3) {
            //处理第一行数据
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    mo.setName(list.get(0));
                } else if (i == 1) {//将第二行数据分别保存到颜色和画笔大小的属性中
                    String[] s1 = list.get(1).split(":");
                    if (s1.length == 2) {
                        String[] s2 = s1[0].split(",");
                        List<Integer> colorNumber = new ArrayList<Integer>();
                        colorNumber.add(Integer.parseInt(s2[0]));
                        colorNumber.add(Integer.parseInt(s2[1]));
                        colorNumber.add(Integer.parseInt(s2[2]));
                        mo.setColorNumber(colorNumber);
                        mo.setgSize(Integer.parseInt(s1[1]));
                    } else {
                        System.out.println("该文本颜色格式不符合要求");
                    }
                } else if (i == 2) {//将第三行数据保存到x，y坐标的两个list中
                    String[] s3 = list.get(2).split("\t");
                    List<Integer> x_list = new ArrayList<Integer>();
                    ;
                    List<Integer> y_list = new ArrayList<Integer>();
                    ;
                    for (String s : s3) {
                        String[] s4 = s.split(",");
                        x_list.add(Integer.parseInt(s4[0]));
                        y_list.add(Integer.parseInt(s4[1]));
                    }
                    mo.setX_list(x_list);
                    mo.setY_list(y_list);
                }
            }
            mo_bk = new MyObject();
            mo_bk.setName(mo.getName());

            List<Integer> xxx = new ArrayList<Integer>();
            List<Integer> yyy = new ArrayList<Integer>();
            for (int i : mo.getX_list()) {
                xxx.add(i);
            }
            for (int j : mo.getY_list()) {
                yyy.add(j);
            }
            mo_bk.setX_list(xxx);
            mo_bk.setY_list(yyy);
            mo_bk.setColorNumber(mo.getColorNumber());
            mo_bk.setgSize(mo.getgSize());
        } else {
            System.out.println("该文本格式不符合要求");
        }
        read.close();
        return mo;
    }

    /**
     * 通过。。。算法处理坐标
     *
     * @param mo
     */
    private MyObject process(MyObject mo) {
        System.out.println("原来坐标个数：" + mo.getX_list().size());
        //对得到的坐标进行压缩处理
        List<Points> points = moToPoints(mo.getX_list(), mo.getY_list());
        Douglas d = new Douglas();
        points = d.compress(points.get(0), points.get(points.size() - 1), points);
        System.out.println("处理后坐标个数：" + points.size());
        mo = pointsToMo(points);
        return mo;
    }

    /**
     * 从MyObject中转换成points的数组
     *
     * @param list_x
     * @param list_y
     * @return
     */
    private List<Points> moToPoints(List<Integer> list_x, List<Integer> list_y) {
        List<Points> points = new ArrayList<Points>();
        for (int i = 0; i < list_x.size(); i++) {
            Points p = new Points(list_x.get(i), list_y.get(i), i);
            points.add(p);
        }
        return points;
    }

    /**
     * 把经过计算得到的points数组转换成mo对象的属性
     *
     * @param points
     * @return
     */
    private MyObject pointsToMo(List<Points> points) {
        mo.setX_list(null);
        mo.setY_list(null);
        List<Integer> listx = new ArrayList<Integer>();
        List<Integer> listy = new ArrayList<Integer>();
        for (Points p : points) {
            listx.add((int) p.getX());
            listy.add((int) p.getY());

        }
        mo.setX_list(listx);
        mo.setY_list(listy);

        return mo;
    }

    /**
     * 缩小
     */
    private void reduce() {
        if (mo != null) {
            //当缩小到（100，100）这个点的时候不能再缩小
            int min_i = Collections.min(mo.getX_list());
            if (min_i <= 100) {
                for (int i = 0; i < mo.getX_list().size(); i++) {
                    if (mo.getX_list().get(i) == min_i) {
                        if (mo.getY_list().get(i) <= 100) {
                            return;
                        }
                    }
                }
                return;
            }
            for (int i = 0; i < mo.getX_list().size(); i++) {
                mo.getX_list().set(i, (int) (mo.getX_list().get(i) / time));
                mo.getY_list().set(i, (int) (mo.getY_list().get(i) / time));
            }
        }

        super.repaint();

    }

    /**
     * 放大
     */
    private void zoom() {
        if (mo != null) {
            for (int i = 0; i < mo.getX_list().size(); i++) {
                mo.getX_list().set(i, (int) (mo.getX_list().get(i) * time));
                mo.getY_list().set(i, (int) (mo.getY_list().get(i) * time));
            }
        }
        super.repaint();
    }

    /**
     * 处理xy坐标(找出最左端的点，将其平移至（100，100）)
     *
     */
    private void getXYData() {
        int min_x = Collections.min(mo.getX_list());
        int min_y = 0;
        for (int i = 0; i < mo.getX_list().size(); i++) {
            if (min_x == mo.getX_list().get(i)) {
                if (min_y == 0) {
                    min_y = mo.getY_list().get(i);
                } else {
                    if (mo.getY_list().get(i) < min_y)
                        min_y = mo.getY_list().get(i);
                }

            }
        }
        for (int i = 0; i < mo.getX_list().size(); i++) {
            mo.getX_list().set(i, mo.getX_list().get(i) - (min_x - 100));
        }
        for (int i = 0; i < mo.getY_list().size(); i++) {
            mo.getY_list().set(i, mo.getY_list().get(i) - (min_y - 100));
        }

    }

    /**
     * 按钮事件监听
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //选择上传的文件
        if (e.getSource() == Choosefiles) {
            JFileChooser chooser = new JFileChooser(".");
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setMultiSelectionEnabled(false);
            int intRetVal = chooser.showOpenDialog(this);
            if (intRetVal == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                try {
                    mo = getFileData(file, mo);
                    this.getXYData();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (DrawGraphics.mo.getName() == null || DrawGraphics.mo.getColorNumber() == null || DrawGraphics.mo.getgSize() == 0 || DrawGraphics.mo.getX_list() == null || DrawGraphics.mo.getY_list() == null) {
            return;
        }
        //按2倍放大
        if (e.getSource() == bigger) {
            zoom();
        }
        //按2倍缩小
        else if (e.getSource() == smaller) {
            reduce();
        } else if (e.getSource() == clear) {
            mo.setX_list(null);
            mo.setY_list(null);
            mo.setColorNumber(null);
            mo.setgSize(0);
            mo.setName(null);
        }

        mp.repaint();
    }


    /**
     * 下拉框改变事件
     *
     * @param e
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (jComboBox.getSelectedItem() == "简化图") {
                List<Integer> xxx = new ArrayList<Integer>();
                List<Integer> yyy = new ArrayList<Integer>();
                for (int i : mo_bk.getX_list()) {
                    xxx.add(i);
                }
                for (int j : mo_bk.getY_list()) {
                    yyy.add(j);
                }
                mo.setX_list(xxx);
                mo.setY_list(yyy);
                process(mo);
                getXYData();
                DrawGraphics.super.repaint();
            } else if (jComboBox.getSelectedItem() == "原图") {
                List<Integer> xxx = new ArrayList<Integer>();
                List<Integer> yyy = new ArrayList<Integer>();
                for (int i : mo_bk.getX_list()) {
                    xxx.add(i);
                }
                for (int j : mo_bk.getY_list()) {
                    yyy.add(j);
                }
                mo.setX_list(xxx);
                mo.setY_list(yyy);
                getXYData();
                DrawGraphics.super.repaint();
            }
        }
    }

    /**
     * 鼠标滚轮事件
     *
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            zoom();
        } else {
            reduce();
        }
    }

    /**
     * 鼠标拖拽事件
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        //TODO mo为空时，不触发
        point2 = e.getPoint();
        if (moveEnable) {
            if (point1 != null && point2 != null) {
                int dx = point2.x - point1.x;
                int dy = point2.y - point1.y;

                for (int i = 0; i < mo.getX_list().size(); i++) {
                    mo.getX_list().set(i, (int) (mo.getX_list().get(i) + dx));
                    mo.getY_list().set(i, (int) (mo.getY_list().get(i) + dy));
                }
                point1 = point2;
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * 鼠标按下
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        moveEnable = true;
        point1 = e.getPoint();
    }

    /**
     * 鼠标释放
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        moveEnable = false;
        point1 = null;
        point2 = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}


