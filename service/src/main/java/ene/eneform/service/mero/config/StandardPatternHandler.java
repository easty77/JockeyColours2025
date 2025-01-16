/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.mero.config;

import ene.eneform.service.mero.action.ENEPatternAction;

/**
 *
 * @author Simon
 */
public interface StandardPatternHandler {
    
     public ENEPatternAction createStandardAction(String strStdClassName, String strCurrentElement);
     
}
