/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.action;

import java.awt.*;
import java.io.Serializable;

/**
 *
 * @author Simon
 */
public class ENECapSVGAction extends ENESVGAction implements Serializable{
    
    private static final int MERO_CAP_XOFFSET = 265;
    private static final int MERO_CAP_YOFFSET = 205;
    
            private static Dimension[] sm_size_rectangle = {new Dimension(40, 40), new Dimension(40, 40)};
            private static Point[][] sm_rectangle_points ={
                {new Point(5, 5)},
                {new Point(MERO_CAP_XOFFSET+10, MERO_CAP_YOFFSET+10)}
            };
       
       public ENECapSVGAction(String strSVGName)
       {
                super(strSVGName, sm_rectangle_points, sm_size_rectangle);
       }
       public static class FiveAround extends ENESVGAction
       {
            // add approx (240, 340) for Standard -> Mero
            private static Dimension[] sm_size_rectangle = {new Dimension(14, 14), new Dimension(20, 20)};
            private static Point[][] sm_rectangle_points ={
            {
                new Point(18, 35), new Point(35, 22),  new Point(30, 5), new Point(6, 5), new Point(0, 22)
            }
            ,
            {
                new Point(MERO_CAP_XOFFSET+23, MERO_CAP_YOFFSET+45), new Point(MERO_CAP_XOFFSET+43, MERO_CAP_YOFFSET+27),  new Point(MERO_CAP_XOFFSET+35, MERO_CAP_YOFFSET+2), new Point(MERO_CAP_XOFFSET+8, MERO_CAP_YOFFSET+2), new Point(MERO_CAP_XOFFSET+0, MERO_CAP_YOFFSET+27)
            }
            };
            private static int[] sm_rotate_degrees = {0, -70, -140, -220, -290};
           
            public FiveAround(String strSVGName)
            {
                super(strSVGName, sm_rectangle_points, sm_size_rectangle, sm_rotate_degrees);
            }
      }
      public static class Front extends ENESVGAction
       {
            // add approx (240, 340) for Standard -> Mero
            private static Dimension[] sm_size_rectangle = {new Dimension(20, 20), new Dimension(25, 25)};
            private static Point[][] sm_rectangle_points ={
            {
                new Point(15, 27)
            }
            ,
            {
                new Point(MERO_CAP_XOFFSET+20, MERO_CAP_YOFFSET+40)
            }
            };
            
            public Front(String strSVGName)
            {
                super(strSVGName, sm_rectangle_points, sm_size_rectangle);
            }
      }
}
