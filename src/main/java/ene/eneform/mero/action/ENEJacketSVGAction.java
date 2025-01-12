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
public class ENEJacketSVGAction extends ENESVGAction implements Serializable{
    
    private static final int MERO_JACKET_XOFFSET = 220;
    private static final int MERO_JACKET_YOFFSET = 320;

    private static Dimension[] sm_size_rectangle = {new Dimension(86, 86), new Dimension(100, 100)};
            private static Point[][] sm_rectangle_points ={
                {new Point(7, 20)},
                {new Point(245, 360)} // adjusted 20160421
//                {new Point(MERO_JACKET_XOFFSET+25, MERO_JACKET_YOFFSET+45)}
            };

       public ENEJacketSVGAction(String strSVGName)
       {
           super(strSVGName, sm_rectangle_points, sm_size_rectangle);
       }
       public static class Badge extends ENESVGAction
       {
           private static Dimension[] sm_size_rectangle = {new Dimension(30, 30), new Dimension(40, 40)};
            private static Point[][] sm_rectangle_points ={
                {new Point(55, 30)},
                {new Point(305, 355)}   // adjusted 20160421
            };
 
            public Badge(String strSVGName)
            {
                super(strSVGName, sm_rectangle_points, sm_size_rectangle);
            }
      }
      public static class Neck extends ENESVGAction
       {
           private static Dimension[] sm_size_rectangle = {new Dimension(60, 15), new Dimension(60, 20)};
            private static Point[][] sm_rectangle_points ={
                {new Point(20, 1)},
                {new Point(MERO_JACKET_XOFFSET+48, MERO_JACKET_YOFFSET+15)}
            };
 
            public Neck(String strSVGName)
            {
                super(strSVGName, sm_rectangle_points, sm_size_rectangle);
            }
      }
       public static class Five extends ENESVGAction
       {
           private static Dimension[] sm_size_rectangle = {new Dimension(25, 25), new Dimension(50, 50)};
            private static Point[][] sm_rectangle_points ={
            {new Point(8, 15), new Point(68, 15), new Point(38, 53), new Point(12, 91), new Point(62, 91)}
                    ,
            {new Point(244, 350), new Point(304, 350), new Point(274, 405), new Point(244, 460), new Point(304, 460)}
            };


            public Five(String strSVGName)
            {
               super(strSVGName, sm_rectangle_points, sm_size_rectangle);
            }
      }
        public static class Four extends ENESVGAction
       {
          private static Dimension[] sm_size_rectangle = {new Dimension(40, 40), new Dimension(50, 50)};
            private static Point[][] sm_rectangle_points ={
            {new Point(8, 15), new Point(54, 15), new Point(8, 91), new Point(54, 91)}
                    ,
            {new Point(244, 350), new Point(304, 350), new Point(244, 460), new Point(304, 460)}
            };

            public Four(String strSVGName)
            {
               super(strSVGName, sm_rectangle_points, sm_size_rectangle);
            }
      }
        public static class TripleSash extends ENESVGAction
       {
            // add approx (240, 340) for Standard -> Mero
           private static Dimension[] sm_size_rectangle = {new Dimension(25, 25), new Dimension(40, 40)};
            private static Point[][] sm_rectangle_points ={
            {new Point(65, 15), new Point(40, 55), new Point(12, 90)}
                    ,
            {new Point(MERO_JACKET_XOFFSET+95, MERO_JACKET_YOFFSET+10), new Point(MERO_JACKET_XOFFSET+60, MERO_JACKET_YOFFSET+50), new Point(MERO_JACKET_XOFFSET+20, MERO_JACKET_YOFFSET+115)}
            };

            public TripleSash(String strSVGName)
            {
               super(strSVGName, sm_rectangle_points, sm_size_rectangle);
            }
      }
       public static class Twin extends ENESVGAction
       {
            // add approx (240, 340) for Standard -> Mero
           private static Dimension[] sm_size_rectangle = {new Dimension(50, 50), new Dimension(50, 50)};
            private static Point[][] sm_rectangle_points ={
            {new Point(25, 20), new Point(25, 75)}
                    ,
            {new Point(MERO_JACKET_XOFFSET+50, MERO_JACKET_YOFFSET+40), new Point(MERO_JACKET_XOFFSET+50, MERO_JACKET_YOFFSET+120)}
            };

            public Twin(String strSVGName)
            {
               super(strSVGName, sm_rectangle_points, sm_size_rectangle);
            }
      }
}
