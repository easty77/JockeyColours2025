/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformNonRunner extends SmartformDailyRunner{

    public SmartformNonRunner(int nRace, int nRunner)
    {
        super(nRace, nRunner);
    }

    public @Override boolean isNonRunner()
    {
        return true;
    }
}
