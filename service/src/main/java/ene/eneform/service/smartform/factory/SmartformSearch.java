/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Simon
 */

package ene.eneform.service.smartform.factory;

import ene.eneform.service.smartform.bos.SmartformJockey;
import ene.eneform.service.smartform.bos.SmartformTrainer;
import ene.eneform.service.utils.DbUtils;
import ene.eneform.service.utils.ENEStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SmartformSearch {

    public static ArrayList<SmartformJockey> findJockey(ENEStatement statement, String strJockey)
    {
        // three possibilities: 1) a numeric id 2) a name 3) a wildcard search
        int nReturn = 0;
        ArrayList<SmartformJockey> list = new ArrayList<SmartformJockey>();
        boolean bExact = false;
         if (strJockey.indexOf("'") >= 0)
            strJockey = strJockey.replace("'", "''");

        String strWhere="";
        try
        {
            int nJockey = Integer.valueOf(strJockey);
            strWhere = "jockey_id=" + nJockey;
        }
        catch (NumberFormatException e)
        {
        }
        if ("".equals(strWhere))
        {
           if (strJockey.indexOf("%") >= 0)
           {
               strWhere = "upper(jockey_name) like upper('" + strJockey +"')";
           }
           else
           {
               bExact = true;
               strWhere = "upper(jockey_name) = upper('" + strJockey +"')";
           }
        }

        String strQuery = "select distinct jockey_id, jockey_name from historic_runners where " + strWhere + " order by jockey_name";

        try
        {
             ResultSet rs = statement.executeQuery(strQuery);
            int nCount = 0;
            if (rs != null)
            {
                while (rs.next())
                {
                    nCount++;
                    SmartformJockey jockey = new SmartformJockey(rs.getInt("jockey_id"), DbUtils.getDBString(rs, "jockey_name"));
                    list.add(jockey);
                }
                rs.close();
            }

            if (bExact && (nCount == 0))
                return findJockey(statement, "%" + strJockey + "%");
        }
        catch(SQLException e)
        {
        }

        return list;
    }
    public static ArrayList<SmartformTrainer> findTrainer(ENEStatement statement, String strTrainer)
    {
        // three possibilities: 1) a numeric id 2) a name 3) a wildcard search
        int nReturn = 0;
        ArrayList<SmartformTrainer> list = new ArrayList<SmartformTrainer>();
        boolean bExact = false;
         if (strTrainer.indexOf("'") >= 0)
            strTrainer = strTrainer.replace("'", "''");

        String strWhere="";
        try
        {
            int nTrainer = Integer.valueOf(strTrainer);
            strWhere = "trainer_id=" + nTrainer;
        }
        catch (NumberFormatException e)
        {
        }
        if ("".equals(strWhere))
        {
           if (strTrainer.indexOf("%") >= 0)
           {
               strWhere = "upper(trainer_name) like upper('" + strTrainer +"')";
           }
           else
           {
               bExact = true;
               strWhere = "upper(trainer_name) = upper('" + strTrainer +"')";
           }
        }

        String strQuery = "select distinct trainer_id, trainer_name from historic_runners where " + strWhere + " order by trainer_name";

        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            int nCount = 0;
            if(rs != null)
            {
                while (rs.next())
                {
                    nCount++;
                    SmartformTrainer trainer = new SmartformTrainer(rs.getInt("trainer_id"), DbUtils.getDBString(rs, "trainer_name"));
                    list.add(trainer);
                }
               rs.close();
            }

            if (bExact && (nCount == 0))
                return findTrainer(statement, "%" + strTrainer + "%");
        }
        catch(SQLException e)
        {
        }

        return list;
    }
 }
