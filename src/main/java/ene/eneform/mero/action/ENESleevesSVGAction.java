/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.action;

import ene.eneform.mero.action.ENESVGAction;
import ene.eneform.mero.config.ENEColoursEnvironment;

import java.awt.*;
import java.io.Serializable;

/**
 *
 * @author Simon
 */
public class ENESleevesSVGAction extends ene.eneform.mero.action.ENESVGAction implements Serializable {
    
    // Note that Mero have twice number of Standard, as Standard is currently reflected
            private static Dimension[] sm_size_rectangle = {new Dimension(25, 25), new Dimension(40, 40)};
            private static Point[][] sm_rectangle_points ={
                {new Point(3, 45)},
                {new Point(205, 360), new Point(350, 360)}
            };

       public ENESleevesSVGAction(ENEColoursEnvironment environment, String strSVGName)
       {
           super(environment, strSVGName, sm_rectangle_points, sm_size_rectangle);
       }
        public static class Three extends ENESVGAction {
    
           private static Dimension[] sm_size_rectangle = {new Dimension(25, 25), new Dimension(40, 40)};
            private static Point[][] sm_rectangle_points ={
                {new Point(3, 35), new Point(3, 70), new Point(3, 105)},
                {new Point(202, 360), new Point(197, 410), new Point(202, 460), // right sleeve
                new Point(355, 360), new Point(360, 410), new Point(355, 460)}  // left sleeve
            };

       public Three(ENEColoursEnvironment environment, String strSVGName)
       {
           super(environment, strSVGName, sm_rectangle_points, sm_size_rectangle);
       }
}
}
