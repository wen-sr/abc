import java.util.ArrayList;
import java.util.List;

/**
 *
 * Class Douglas.java 
 *
 * Description 
 *
 * Company mapbar 
 *
 * author Chenll E-mail: Chenll@mapbar.com 
 *
 * Version 1.0 
 *
 * Date 2012-6-28 下午02:53:58 
 */
public class Douglas {

    /**
     * 控制数据压缩精度的极差 
     */
    private static final double D = 1;

    /**
     * 对矢量曲线进行压缩 
     *
     * @param from
     *            曲线的起始点 
     * @param to
     *            曲线的终止点 
     */
    public List<Points> compress(Points from, Points to, List<Points> points) {

        /**
         * 压缩算法的开关量 
         */
        boolean switchvalue = false;

        /**
         * 由起始点和终止点构成的直线方程一般式的系数 
         */
//        System.out.println(from.getY());
//        System.out.println(to.getY());
        double A = (from.getY() - to.getY())
                / Math.sqrt(Math.pow((from.getY() - to.getY()), 2)
                + Math.pow((from.getX() - to.getX()), 2));

        /**
         * 由起始点和终止点构成的直线方程一般式的系数 
         */
        double B = (to.getX() - from.getX())
                / Math.sqrt(Math.pow((from.getY() - to.getY()), 2)
                + Math.pow((from.getX() - to.getX()), 2));

        /**
         * 由起始点和终止点构成的直线方程一般式的系数 
         */
        double C = (from.getX() * to.getY() - to.getX() * from.getY())
                / Math.sqrt(Math.pow((from.getY() - to.getY()), 2)
                + Math.pow((from.getX() - to.getX()), 2));

        double d = 0;
        double dmax = 0;
        int m = points.indexOf(from);
        int n = points.indexOf(to);
        if (n == m + 1)
            return null;
        Points middle = null;
        List<Double> distance = new ArrayList<Double>();
        for (int i = m + 1; i < n; i++) {
            d = Math.abs(A * (points.get(i).getX()) + B
                    * (points.get(i).getY()) + C)
                    / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));
            distance.add(d);
        }
        dmax = distance.get(0);
        for (int j = 1; j < distance.size(); j++) {
            if (distance.get(j) > dmax)
                dmax = distance.get(j);
        }
        if (dmax > D)
            switchvalue = true;
        else
            switchvalue = false;
        if (!switchvalue) {
            // 删除Points(m,n)内的坐标  
            for (int i = m + 1; i < n; i++) {
//                points.get(i).setIndex(-1);
                points.remove(i);
            }

        } else {
            for (int i = m + 1; i < n; i++) {
                if ((Math.abs(A * (points.get(i).getX()) + B
                        * (points.get(i).getY()) + C)
                        / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)) == dmax))
                    middle = points.get(i);
            }
            compress(from, middle, points);
            compress(middle, to, points);
        }

        return points;
    }

}