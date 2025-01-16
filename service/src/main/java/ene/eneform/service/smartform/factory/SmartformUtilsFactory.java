/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.factory;

import ene.eneform.service.utils.DbUtils;
import ene.eneform.service.utils.ENEStatement;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Simon
 */
public class SmartformUtilsFactory {
    
public static void createTrainerChangeStatistics(ENEStatement statement, char cDirection, int nRunNr, boolean bHandicap, String strFilename)
{
    boolean bInsert = true;
    String strHeader="nr_run,direction,duration,%trainer profit,#trainer runners,%trainer winners,handicap,#runs,%wins,%profit,#handicap runs,%handicap wins,%handicap profit,#non-handicap runs,%non-handicap wins,%non-handicap profit \r\n";
    String strKeyColumns = "s.nr_run, direction, duration, %d, %d, %d, %d";
    String strAllColumns = "count(*), cast((sum(etc.winner_flag) * 100)/count(*) as unsigned), cast((sum(s.win_profit) * 100)/count(*) as signed)";
    String strHandicapColumns = "sum(case when etc.handicap=1 then 1 else 0 end), cast((sum(case when etc.handicap = 1 then etc.winner_flag else 0 end) * 100)/sum(case when etc.handicap=1 then 1 else 0 end) as unsigned), cast((sum(case when etc.handicap=1 then s.win_profit else 0 end) * 100)/sum(case when etc.handicap=1 then 1 else 0 end) as signed)";
    String strNonHandicapColumns = "sum(case when etc.handicap=0 then 1 else 0 end), cast((sum(case when etc.handicap = 0 then etc.winner_flag else 0 end) * 100)/sum(case when etc.handicap=0 then 1 else 0 end) as unsigned), cast((sum(case when etc.handicap=0 then s.win_profit else 0 end) * 100)/sum(case when etc.handicap=0 then 1 else 0 end) as signed)";
    
    String strWhere = "(s.historic_win_profit * 100)/s.historic_nr_runners > %d and s.historic_nr_runners >= %d and ((s.historic_nr_winners * 100)/s.historic_nr_runners) >= %d";
    String strHandicapWhere = "(s.hcap_win_profit * 100)/s.hcap_nr_runners > %d and s.hcap_nr_runners >= %d and ((s.hcap_nr_winners * 100)/s.hcap_nr_runners) >= %d";
    
    String strQueryFormat = "select " + strKeyColumns + ", " + strAllColumns + ", " + strHandicapColumns + ", " + strNonHandicapColumns + " from eneform_trainer_change_summary s, eneform_trainer_change etc where s.race_id=etc.race_id and s.runner_id=etc.runner_id and s.nr_run=etc.nr_run and s.nr_run=%d and s.direction = '%c'  and s.duration = %d and ";
    
    // either where or handicap where
    strQueryFormat += bHandicap ? strHandicapWhere : strWhere;
    
    FileWriter fwriter = null;
    try
    {
        if (!bInsert)
        {
            fwriter = new FileWriter(strFilename);
            fwriter.append(strHeader);
        }
        for (int nYears = 0; nYears <= 2; nYears++)
        {
            for(int nTrainerRuns = 1; nTrainerRuns <= 10; nTrainerRuns++)
            {
                for(int nPctTrainerWins = 0; nPctTrainerWins < 100; nPctTrainerWins = nPctTrainerWins + 10)
                {
                    for(int nPctTrainerProfit = -20; nPctTrainerProfit <= 200; nPctTrainerProfit = nPctTrainerProfit + 10)
                    {
                        String strQuery = String.format(strQueryFormat, nPctTrainerProfit, nTrainerRuns, nPctTrainerWins, bHandicap ? 1 : 0, nRunNr, cDirection, nYears, nPctTrainerProfit, nTrainerRuns, nPctTrainerWins);
                        if (bInsert)
                        {
                            String strUpdate = "insert into eneform_trainer_change_strategy (nr_run, direction, duration, pct_trainer_profit, nr_trainer_runners, pct_trainer_winners, handicap, nr_runs, pct_wins, pct_profit, handicap_runs, nr_handicap_wins, pct_handicap_profit, nr_non_handicap_runs, pct_non_handicap_wins, pct_non_handicap_profit) (" + strQuery + ")";
                            statement.executeUpdate(strUpdate);
                        }
                        else
                        {
                            ResultSet rs = statement.executeQuery(strQuery);
                            DbUtils.writeResultSet2csv(fwriter, rs);
                            rs.close();
                        }
                    }
                }
            }
        }
        if (fwriter != null)
            fwriter.close();
    }
    catch(SQLException e)
    {
        System.out.println("SQLException: " + e.getMessage());
    }
    catch(IOException e)
    {
        System.out.println("IOException: " + e.getMessage());
    }
}
}
