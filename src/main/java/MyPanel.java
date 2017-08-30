import javax.swing.*;
import java.awt.*;
import java.util.Collections;

/**
 * 自定义面板
 * @author wen-sr
 *
 */
@SuppressWarnings("serial")
class MyPanel extends JPanel {

    MyObject myObject;

    public MyPanel(MyObject myObject) {
        super();
        this.myObject = myObject;
    }

    /**
     * 重写画图方法
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(myObject.getName() == null || myObject.getColorNumber() == null || myObject.getgSize() == 0 || myObject.getX_list() == null || myObject.getY_list() == null ){
            return;
        }

//		System.out.println("x:" + Collections.min(myObject.getX_list()));
        //左边部分开始--------------------------------
        //创建颜色
        Color color=new Color(myObject.getColorNumber().get(0),myObject.getColorNumber().get(1),myObject.getColorNumber().get(2));
        g.setColor(color);
        Graphics2D g2 = (Graphics2D)g;
        //设置画笔大小
        g2.setStroke(new BasicStroke(myObject.getgSize(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        for(int i=0;i<myObject.getX_list().size();i++){
            if(i<myObject.getX_list().size()-1){
                g2.drawLine(myObject.getX_list().get(i), myObject.getY_list().get(i), myObject.getX_list().get(i+1), myObject.getY_list().get(i+1));
            }
            //最后一个坐标与第一个坐标相连
//			if(i == myObject.getX_list().size()-1){
//				g2.drawLine(myObject.getX_list().get(i), myObject.getY_list().get(i), myObject.getX_list().get(0), myObject.getY_list().get(0));
//			}
        }

        //左边实线图
        g2.setColor(Color.DARK_GRAY);
        g2 = (Graphics2D)g;
        //设置画笔大小
        g2.setStroke(new BasicStroke((float) 1.0));

        for(int i=0;i<myObject.getX_list().size();i++){
            if(i<myObject.getX_list().size()-1){
                g.drawLine(myObject.getX_list().get(i), myObject.getY_list().get(i), myObject.getX_list().get(i+1), myObject.getY_list().get(i+1));
            }
        }
        //左边部分结束--------------------------------

        //右边部分开始--------------------------------
        int distance = Collections.max(myObject.getX_list()) - Collections.min(myObject.getX_list()) + 20;
        //创建颜色
        Color color2=new Color(myObject.getColorNumber().get(0),myObject.getColorNumber().get(1),myObject.getColorNumber().get(2));
        g2.setColor(color2);
        //设置画笔大小
        g2.setStroke(new BasicStroke(myObject.getgSize(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        for(int i=0;i<myObject.getX_list().size();i++){
            if(i<myObject.getX_list().size()-1){
                g2.drawLine(myObject.getX_list().get(i)+distance, myObject.getY_list().get(i), myObject.getX_list().get(i+1)+distance, myObject.getY_list().get(i+1));
            }
        }


        //右边叉图
        g2.setColor(Color.DARK_GRAY);
        g2 = (Graphics2D)g;
        //设置画笔大小
        g2.setStroke(new BasicStroke((float) 1.0));
        for(int i=0;i<myObject.getX_list().size();i++){
            g.drawLine(myObject.getX_list().get(i)+distance-1, myObject.getY_list().get(i)-1, myObject.getX_list().get(i)+distance+1, myObject.getY_list().get(i)+1);
            g.drawLine(myObject.getX_list().get(i)+distance+1, myObject.getY_list().get(i)-1, myObject.getX_list().get(i)+distance-1, myObject.getY_list().get(i)+1);
        }
        //右边部分结束--------------------------------

    }
}