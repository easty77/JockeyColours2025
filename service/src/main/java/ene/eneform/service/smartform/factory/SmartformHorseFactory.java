/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.factory;

import ene.eneform.service.smartform.bos.*;
import ene.eneform.service.utils.DbUtils;
import ene.eneform.service.utils.ENEStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Simon
 */
public class SmartformHorseFactory {
    public static ArrayList<SmartformHorseDefinition> findAncestors(ENEStatement statement)
    {
        ArrayList<SmartformHorseDefinition> list = new ArrayList<SmartformHorseDefinition>();
        
        String strQuery = "select name, bred, year_born, sex";
        strQuery += " from ancestors_old a";
        //strQuery += " where sex in ('H', 'C')";
        strQuery += " where sex != 'G'";
        strQuery += " and not exists (select * from ancestors a1 where a1.name=a.name and a1.bred=a.bred and a1.year_born=a.year_born and a1.gsv is not null)";
        strQuery += " and name > 'U'";
        strQuery += " order by 1";

        
        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strName = rs.getString(1);
                    String strBred = rs.getString(2);
                    int nYearBorn = rs.getInt(3);
                    SmartformHorseDefinition horse = new SmartformHorseDefinition(strName, nYearBorn, strBred);
                    if (!rs.wasNull())
                        horse.setYearBorn(nYearBorn);
                    String strGender = rs.getString(4);
                    horse.setGender(strGender.charAt(0));
                    list.add(horse);
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
        }

        return list;
    }

    public static SmartformHorse findCareerHorse(ENEStatement statement, String strName, String strBred)
    {
        SmartformHorse horse = null;
        String strQuery = "select 0 as runner_id, horse_name as name, dru.bred, dru.foaling_date, year(dru.foaling_date) as foaling_year, lower(dru.gender) as gender, lower(dru.colour) as colour, 0 as trainer_id, trainer_name, 0 as owner_id, owner_name, breeder_name, final_run";
        strQuery += ", case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end as sire_name";
        strQuery += ", coalesce(as1.bred, case when sire_name like '%(%' then substr(sire_name, locate('(', sire_name) + 1, locate(')', sire_name) - locate('(', sire_name) - 1) else '' end) as sire_bred";
        strQuery += ", case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end as dam_name";
        strQuery += ", case when dam_sire_name like '%(%' then substr(dam_sire_name,1, locate('(', dam_sire_name) - 1) else dam_sire_name end as dam_sire_name";
        strQuery += ", coalesce(ad.bred, case when dam_name like '%(%' then substr(dam_name, locate('(', dam_name) + 1, locate(')', dam_name) - locate('(', dam_name) - 1) else '' end) as dam_bred";
        strQuery += ", coalesce(as1.year_born, sire_year_born) as sire_year, as1.pq_code as sire_code, coalesce(ad.year_born, dam_year_born) as dam_year, ad.pq_code as dam_code";
        strQuery += " from ((career_horses dru";
        strQuery += " left outer join ancestors as1 on replace(replace(replace(replace(case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end, '-', ''), '''', ''), '.', ''), ' ', '')=as1.name and (sire_year_born is null or sire_year_born=as1.year_born) and as1.sex in ('H', 'C'))";
        strQuery += " left outer join ancestors ad on replace(replace(replace(replace(case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end, '-', ''), '''', ''), '.', ''), ' ', '')=ad.name and (dam_year_born is null or dam_year_born=ad.year_born) and ad.sex in ('M', 'F'))";
        strQuery += " where horse_name = '" + strName + "'";
        if (strBred != null)
            strQuery += " and bred = '" + strBred + "'";
      
       try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    horse = createSmartformHorse(rs, strQuery);
                }
               rs.close();
             }

        }
        catch(SQLException e)
        {
        }
     
        return horse;
    }
   public static ArrayList<SmartformHorse> findDailyRunners(ENEStatement statement, String strType, String strTrackType, String strWhere)
    {
        
        String strQuery = "select distinct dru.runner_id, dru.name, dru.bred, dru.foaling_date, year(dru.foaling_date) as foaling_year, dru.gender, dru.colour";
        strQuery += ", case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end as sire_name";
        strQuery += ", coalesce(as1.bred, case when sire_name like '%(%' then substr(sire_name, locate('(', sire_name) + 1, locate(')', sire_name) - locate('(', sire_name) - 1) else '' end) as sire_bred";
        strQuery += ", case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end as dam_name";
        strQuery += ", case when dam_sire_name like '%(%' then substr(dam_sire_name,1, locate('(', dam_sire_name) - 1) else dam_sire_name end as dam_sire_name";
        strQuery += ", coalesce(ad.bred, case when dam_name like '%(%' then substr(dam_name, locate('(', dam_name) + 1, locate(')', dam_name) - locate('(', dam_name) - 1) else '' end) as dam_bred";
         strQuery += ", coalesce(as1.year_born, sire_year_born) as sire_year, as1.pq_code as sire_code, coalesce(ad.year_born, dam_year_born) as dam_year, ad.pq_code as dam_code";
        strQuery += " from ((((daily_races dra inner join daily_runners dru on dru.race_id=dra.race_id)";
        strQuery += " inner join eneform_races er on dru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting'))";
        strQuery += " left outer join ancestors as1 on replace(replace(replace(replace(case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end, '-', ''), '''', ''), '.', ''), ' ', '')=as1.name and sire_year_born=as1.year_born and as1.sex in ('H', 'C'))";
        strQuery += " left outer join ancestors ad on replace(replace(replace(replace(case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end, '-', ''), '''', ''), '.', ''), ' ', '')=ad.name and dam_year_born=ad.year_born and ad.sex in ('M', 'F'))";
        strQuery += " where foaling_date is not null and dru.gender != ''";
        if ((strType != null) && !"".equals(strType))
            strQuery += (" and dra.race_type='" + strType + "'");   // Flat, Hurdle, Chase, N_H_Flat
        if ((strTrackType != null) && !"".equals(strTrackType))
            strQuery += (" and dra.track_type='" + strTrackType + "'");   // Turf, Fibresand, Polytrack, AllWeather, Dirt
        if ((strWhere != null) && !"".equals(strWhere))
            strQuery += (" and " + strWhere);
        //strQuery += " and age_range = '4YO only' and  penalty_value > 500";
        strQuery += " and not exists (select * from ancestors a where replace(replace(replace(replace(dru.name, '-', ''), '''', ''), '.', ''), ' ', '')=a.name and year(dru.foaling_date)=a.year_born)";
        //strQuery += " and dru.name > 'SILLY'";
        //strQuery += " and dru.name in ('SCRUM V')";
        strQuery += " and dru.name  not in ('Utrillo', 'Echo Ridge', 'Go Man Go')"; // on list of horses that can't be edited
        strQuery += " order by 2"; 
      
        return executeDailyRunnersQuery(statement, strQuery);
    }
                
  /*
   * select count(distinct hru.runner_id) from 
historic_runners hru inner join historic_races hra on hru.race_id=hra.race_id
where

;
   */      
        
    public static ArrayList<SmartformHorse> executeDailyRunnersQuery(ENEStatement statement, String strQuery)
    {
        ArrayList<SmartformHorse> list = new ArrayList<SmartformHorse>();
       try
        {
            System.out.println(strQuery);
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    SmartformHorse horse = createSmartformHorse(rs, strQuery);
                    list.add(horse);
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
            System.out.println("executeDailyRunnersQuery: " + e.getMessage());
        }

        return list;
    }

private static SmartformHorse createSmartformHorse(ResultSet rs, String strQuery) throws SQLException
{
    int nId = rs.getInt("runner_id");
    String strName = rs.getString("name");
    String strBred = rs.getString("bred");
    Calendar calFoaling = null;
    Date dateFoaling = rs.getDate("foaling_date");
    if (dateFoaling != null)
    {
        calFoaling = new GregorianCalendar();
        calFoaling.setTimeInMillis(dateFoaling.getTime());
    }
    String strGender = rs.getString("gender");
    SmartformHorse horse = new SmartformHorse(nId, strName, calFoaling, strBred, ((strGender == null) || (strGender.length() == 0)) ? ' ' : strGender.charAt(0));
    String strColour = rs.getString("colour");
    horse.setColour(strColour);
/*                   if (!rs.wasNull())
        horse.setYearBorn(nYearBorn); */
    String strSireName = rs.getString("sire_name");
    if ((strSireName != null) && !"".equals(strSireName))
    {
        horse.setSireName(strSireName);
        SmartformHorseDefinition sire = new SmartformHorseDefinition( strSireName, rs.getInt("sire_year"), rs.getString("sire_bred"), 'H');
        sire.setPQCode(rs.getString("sire_code"));
        horse.setSire(sire);
    }

    String strDamName = rs.getString("dam_name");
    if ((strDamName != null) && !"".equals(strDamName))
    {
        SmartformHorseDefinition dam = new SmartformHorseDefinition( strDamName, rs.getInt("dam_year"), rs.getString("dam_bred"), 'M');
        dam.setPQCode(rs.getString("dam_code"));
        String strDamSireName = rs.getString("dam_sire_name");
        if (strDamSireName != null)
            dam.setSireName(strDamSireName);
        horse.setDam(dam);
    }

    if (strQuery.indexOf("trainer_name") >= 0)
    {
        String strTrainerName = rs.getString("trainer_name");
        if ((strTrainerName != null) && !"".equals(strTrainerName))
        {
            SmartformTrainer trainer = SmartformRunnerFactory.createSmartformTrainerObject(rs);
            horse.setTrainer(trainer);
        }
    }
    if (strQuery.indexOf("owner_name") >= 0)
    {
        String strOwnerName = rs.getString("owner_name");
        if ((strOwnerName != null) && !"".equals(strOwnerName))
        {
            SmartformOwner owner = SmartformRunnerFactory.createSmartformOwnerObject(rs);
            horse.setOwner(owner);
        }
    }
    if (strQuery.indexOf("breeder_name") >= 0)
    {
        String strBreederName = rs.getString("breeder_name");
        if ((strBreederName != null) && !"".equals(strBreederName))
        {
            horse.setBreeder(strBreederName);
        }
    }
    if (strQuery.indexOf("final_run") >= 0)
    {
        Date dateFinalRun = rs.getDate("final_run");
        if (dateFinalRun != null)
        {
            Calendar calFinalRun = null;
            calFinalRun = new GregorianCalendar();
            calFinalRun.setTimeInMillis(dateFinalRun.getTime());
            horse.setFinalRun(calFinalRun);
        }
    }

    return horse;
}
public static SmartformHorse findRunnerLatest(ENEStatement statement, String strName)
   {
       AdditionalRaceInstance arl = null;
       String strQuery = "select race_id, source, meeting_date, source_order from";
        strQuery += " (select u.race_id, 'SF' as source, meeting_date, 1 as source_order from historic_runners u, historic_races a where name='" + strName.replace("'", "''") + "'";
        strQuery += " and u.race_id=a.race_id";
        strQuery += " union";
        strQuery +=" select u.race_id, aru_source as source, meeting_date, case when aru_source='RP' then 2 else 3 end as source_order from additional_runners u, additional_races a where name='" + strName.replace("'", "''") + "'";
        strQuery += " and u.race_id=a.race_id and aru_source=ara_source";
        strQuery += ") as d order by source_order asc, meeting_date desc limit 1";
       try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    arl = new AdditionalRaceInstance(rs.getString("source"), rs.getInt("race_id"), rs.getDate("meeting_date"));
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
        }
        
       if (arl != null)
       {
               return findRaceRunner(statement, strName, arl);
       }
       
       return null;
   }
   public static SmartformHorse findRaceRunner(ENEStatement statement, String strName, AdditionalRaceInstance arl)
    {
           if ("SF".equals(arl.getSource()))
           {
               return findSFRaceRunner(statement, strName, arl.getRaceId());
           }
           else
           {
               return findAdditionalRaceRunner(statement, strName, arl.getSource(), arl.getRaceId());
           }
    }
   public static SmartformHorse findSFRaceRunner(ENEStatement statement, String strName, int nRace)
    {
        // owner_id only available in historic_runners
        SmartformHorse horse = null;
        String strQuery = "select distinct hru.runner_id, hru.name, hru.bred, hru.foaling_date, year(hru.foaling_date) as foaling_year, lower(hru.gender) as gender, lower(hru.colour) as colour, hru.trainer_id, hru.trainer_name, hru.owner_id, hru.owner_name";
        strQuery += ", case when hru.sire_name like '%(%' then substr(hru.sire_name,1, locate('(', hru.sire_name) - 1) else hru.sire_name end as sire_name";
        strQuery += ", coalesce(as1.bred, case when hru.sire_name like '%(%' then substr(hru.sire_name, locate('(', hru.sire_name) + 1, locate(')', hru.sire_name) - locate('(', hru.sire_name) - 1) else '' end) as sire_bred";
        strQuery += ", case when hru.dam_name like '%(%' then substr(dru.dam_name,1, locate('(', hru.dam_name) - 1) else hru.dam_name end as dam_name";
        strQuery += ", coalesce(ad.bred, case when hru.dam_name like '%(%' then substr(hru.dam_name, locate('(', hru.dam_name) + 1, locate(')', hru.dam_name) - locate('(', hru.dam_name) - 1) else '' end) as dam_bred";
        strQuery += ", coalesce(as1.year_born, dru.sire_year_born) as sire_year, as1.pq_code as sire_code, coalesce(ad.year_born, dru.dam_year_born) as dam_year, ad.pq_code as dam_code";
        strQuery += ", case when hru.dam_sire_name like '%(%' then substr(hru.dam_sire_name,1, locate('(', hru.dam_sire_name) - 1) else hru.dam_sire_name end as dam_sire_name";
        strQuery += " from historic_races hra inner join historic_runners hru on hru.race_id=hra.race_id and hru.name='" + strName.replace("'", "''") + "'";
        strQuery += " left outer join daily_runners dru on hra.race_id=dru.race_id and dru.name=hru.name";
         strQuery += " left outer join ancestors as1 on replace(replace(replace(replace(case when hru.sire_name like '%(%' then substr(hru.sire_name,1, locate('(', hru.sire_name) - 1) else hru.sire_name end, '-', ''), '''', ''), '.', ''), ' ', '')=as1.name and dru.sire_year_born=as1.year_born and as1.sex in ('H', 'C')";
        strQuery += " left outer join ancestors ad on replace(replace(replace(replace(case when hru.dam_name like '%(%' then substr(hru.dam_name,1, locate('(', hru.dam_name) - 1) else hru.dam_name end, '-', ''), '''', ''), '.', ''), ' ', '')=ad.name and dru.dam_year_born=ad.year_born and ad.sex in ('M', 'F')";
        strQuery += (" where hra.race_id=" + nRace);
  
        ArrayList<SmartformHorse> list = executeDailyRunnersQuery(statement, strQuery);
        
        if (list.size() == 1)
        {
            horse =  list.get(0);
        }

        return horse;
    }
   public static SmartformHorse findAdditionalRaceRunner(ENEStatement statement, String strName, String strSource, int nRace)
    {
        // owner_id only available in historic_runners
        SmartformHorse horse = null;
        String strQuery = "select distinct 0 as runner_id, dru.name, dru.bred, null as foaling_date, 0 as foaling_year, lower(dru.gender) as gender, lower(dru.colour) as colour, 0 as trainer_id, dru.trainer_name,  0 as owner_id, dru.owner_name";
        strQuery += ", case when dru.sire_name like '%(%' then substr(dru.sire_name,1, locate('(', dru.sire_name) - 1) else dru.sire_name end as sire_name";
        strQuery += ", coalesce(as1.bred, case when dru.sire_name like '%(%' then substr(dru.sire_name, locate('(', dru.sire_name) + 1, locate(')', dru.sire_name) - locate('(', dru.sire_name) - 1) else '' end) as sire_bred";
        strQuery += ", case when dru.dam_name like '%(%' then substr(dru.dam_name,1, locate('(', dru.dam_name) - 1) else dru.dam_name end as dam_name";
        strQuery += ", coalesce(ad.bred, case when dru.dam_name like '%(%' then substr(dru.dam_name, locate('(', dru.dam_name) + 1, locate(')', dru.dam_name) - locate('(', dru.dam_name) - 1) else '' end) as dam_bred";
        strQuery += ", coalesce(as1.year_born, 0) as sire_year, as1.pq_code as sire_code, coalesce(ad.year_born, 0) as dam_year, ad.pq_code as dam_code";
        strQuery += ", '' as dam_sire_name";
        strQuery += " from additional_races dra inner join additional_runners dru on dru.race_id=dra.race_id and dru.name='" + strName.replace("'", "''") + "'";
         strQuery += " left outer join ancestors as1 on replace(replace(replace(replace(case when dru.sire_name like '%(%' then substr(dru.sire_name,1, locate('(', dru.sire_name) - 1) else dru.sire_name end, '-', ''), '''', ''), '.', ''), ' ', '')=as1.name and as1.sex in ('H', 'C')";
        strQuery += " left outer join ancestors ad on replace(replace(replace(replace(case when dru.dam_name like '%(%' then substr(dru.dam_name,1, locate('(', dru.dam_name) - 1) else dru.dam_name end, '-', ''), '''', ''), '.', ''), ' ', '')=ad.name and ad.sex in ('M', 'F')";
        strQuery += (" where dra.race_id=" + nRace + " and ara_source='" + strSource+ "'");
  
        ArrayList<SmartformHorse> list = executeDailyRunnersQuery(statement, strQuery);
        
        if (list.size() == 1)
        {
            horse =  list.get(0);
        }

        return horse;
    }
   public static SmartformHorse findSFRunnerLatest(ENEStatement statement, String strName)
    {
        // owner_id only available in historic_runners
        SmartformHorse horse = null;
        String strQuery = "select distinct dru.runner_id, dru.name, dru.bred, dru.foaling_date, year(dru.foaling_date) as foaling_year, dru.gender, dru.colour, dru.trainer_id, dru.trainer_name, hru.owner_id, hru.owner_name";
        strQuery += ", case when dru.sire_name like '%(%' then substr(dru.sire_name,1, locate('(', dru.sire_name) - 1) else dru.sire_name end as sire_name";
        strQuery += ", coalesce(as1.bred, case when dru.sire_name like '%(%' then substr(dru.sire_name, locate('(', dru.sire_name) + 1, locate(')', dru.sire_name) - locate('(', dru.sire_name) - 1) else '' end) as sire_bred";
        strQuery += ", case when dru.dam_name like '%(%' then substr(dru.dam_name,1, locate('(', dru.dam_name) - 1) else dru.dam_name end as dam_name";
        strQuery += ", coalesce(ad.bred, case when dru.dam_name like '%(%' then substr(dru.dam_name, locate('(', dru.dam_name) + 1, locate(')', dru.dam_name) - locate('(', dru.dam_name) - 1) else '' end) as dam_bred";
        strQuery += ", coalesce(as1.year_born, dru.sire_year_born) as sire_year, as1.pq_code as sire_code, coalesce(ad.year_born, dam_year_born) as dam_year, ad.pq_code as dam_code";
        strQuery += ", case when dru.dam_sire_name like '%(%' then substr(dru.dam_sire_name,1, locate('(', dru.dam_sire_name) - 1) else dru.dam_sire_name end as dam_sire_name";
        strQuery += " from daily_races dra inner join daily_runners dru on dru.race_id=dra.race_id and dru.name='" + strName.replace("'", "''") + "'";
        strQuery += " inner join historic_runners hru on hru.race_id=dru.race_id and dru.name=hru.name";
         strQuery += " left outer join ancestors as1 on replace(replace(replace(replace(case when dru.sire_name like '%(%' then substr(dru.sire_name,1, locate('(', dru.sire_name) - 1) else dru.sire_name end, '-', ''), '''', ''), '.', ''), ' ', '')=as1.name and dru.sire_year_born=as1.year_born and as1.sex in ('H', 'C')";
        strQuery += " left outer join ancestors ad on replace(replace(replace(replace(case when dru.dam_name like '%(%' then substr(dru.dam_name,1, locate('(', dru.dam_name) - 1) else dru.dam_name end, '-', ''), '''', ''), '.', ''), ' ', '')=ad.name and dru.dam_year_born=ad.year_born and ad.sex in ('M', 'F')";
        strQuery += (" where dra.race_id=(select max(race_id) from historic_runners where name='" + strName.replace("'", "''") + "')");
  
        ArrayList<SmartformHorse> list = executeDailyRunnersQuery(statement, strQuery);
        
        if (list.size() == 1)
        {
            horse =  list.get(0);
        }

        return horse;
    }
    public static ArrayList<SmartformHorse> findDayRunners(ENEStatement statement, int nDays, String strType)
    {
        String strQuery = "select distinct dru.runner_id, dru.name, dru.bred, dru.foaling_date, year(dru.foaling_date) as foaling_year, dru.gender, dru.colour";
        strQuery += ", case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end as sire_name";
        strQuery += ", coalesce(as1.bred, case when sire_name like '%(%' then substr(sire_name, locate('(', sire_name) + 1, locate(')', sire_name) - locate('(', sire_name) - 1) else '' end) as sire_bred";
        strQuery += ", case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end as dam_name";
        strQuery += ", coalesce(ad.bred, case when dam_name like '%(%' then substr(dam_name, locate('(', dam_name) + 1, locate(')', dam_name) - locate('(', dam_name) - 1) else '' end) as dam_bred";
        strQuery += ", coalesce(as1.year_born, sire_year_born) as sire_year, as1.pq_code as sire_code, coalesce(ad.year_born, dam_year_born) as dam_year, ad.pq_code as dam_code";
        strQuery += ", case when dam_sire_name like '%(%' then substr(dam_sire_name,1, locate('(', dam_sire_name) - 1) else dam_sire_name end as dam_sire_name";
        strQuery += " from ((((daily_races dra inner join daily_runners dru on dru.race_id=dra.race_id)";
        strQuery += " inner join eneform_races er on dru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting'))";
        strQuery += " left outer join ancestors as1 on replace(replace(replace(replace(case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end, '-', ''), '''', ''), '.', ''), ' ', '')=as1.name and sire_year_born=as1.year_born and as1.sex in ('H', 'C'))";
        strQuery += " left outer join ancestors ad on replace(replace(replace(replace(case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end, '-', ''), '''', ''), '.', ''), ' ', '')=ad.name and dam_year_born=ad.year_born and ad.sex in ('M', 'F'))";
        strQuery += (" where dra.meeting_date >= date_sub(current_date, interval " + nDays + " day)");
        if ((strType != null) && !"".equals(strType))
            strQuery += (" and dra.race_type='" + strType + "'");
        strQuery += " and not exists (select * from ancestors a where replace(replace(replace(replace(dru.name, '-', ''), '''', ''), '.', ''), ' ', '')=a.name and year(dru.foaling_date)=a.year_born)";
        strQuery += " and dru.name not in ('Joe Reed')";   // unable to edit
        //strQuery += " and dru.name > 'LOOK'";
        strQuery += " order by 2"; 
 
        return executeDailyRunnersQuery(statement, strQuery);
    }

    public static ArrayList<SmartformHorse> findHistoricRunners(ENEStatement statement, String strType, String strWhere)
    {
        String strQuery = "select distinct hru.runner_id, hru.name, hru.bred, year(hru.foaling_date), hru.gender, hru.colour";
        strQuery += ", case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end as sire_name";
        strQuery += ", case when sire_name like '%(%' then substr(sire_name, locate('(', sire_name) + 1, locate(')', sire_name) - locate('(', sire_name) - 1) else '' end as sire_bred1, as1.bred as sire_bred";
        strQuery += ", case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end as dam_name";
        strQuery += ", case when dam_name like '%(%' then substr(dam_name, locate('(', dam_name) + 1, locate(')', dam_name) - locate('(', dam_name) - 1) else '' end as dam_bred";
        strQuery += ", case when dam_sire_name like '%(%' then substr(dam_sire_name,1, locate('(', dam_sire_name) - 1) else dam_sire_name end as dam_sire_name";
        strQuery += ", as1.year_born as sire_year, '' as sire_code, 0 as dam_year, '' as dam_code";
        strQuery += " from (((historic_races hra inner join historic_runners hru on hru.race_id=hra.race_id)";
        strQuery += " inner join eneform_races er on hru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting'))";
        // Inner Join on ancestor sires - for Dams we have the Dam Sire
        strQuery += " inner join ancestors as1 on replace(replace(replace(replace(case when sire_name like '%(%' then substr(sire_name,1, locate('(', sire_name) - 1) else sire_name end, '-', ''), '''', ''), '.', ''), ' ', '')=as1.name and as1.sex = 'H')";
        strQuery += (" where 1=1");
        if ((strType != null) && !"".equals(strType))
            strQuery += (" and hra.race_type='" + strType + "'");   // Flat, Hurdle, Chase, All Weather Flat, National Hunt Flat
        if ((strWhere != null) && !"".equals(strWhere))
            strQuery += (" and " + strWhere);
        //strQuery += " where max_age = 2 and min_age = 2 and added_money > 25000";
        strQuery += " and not exists (select * from ancestors a where replace(replace(replace(replace(hru.name, '-', ''), '''', ''), '.', ''), ' ', '')=a.name and year(hru.foaling_date)=a.year_born)";
        strQuery += " and hru.name not in ('Joe Reed')";   // unable to edit
        //strQuery += " and hru.name > 'VIA '";
        strQuery += " order by 2"; 
 
        return executeDailyRunnersQuery(statement, strQuery);
    }

    public static ArrayList<SmartformHorseDefinition> findBreeding2(ENEStatement statement)
    {
        ArrayList<SmartformHorseDefinition> list = new ArrayList<SmartformHorseDefinition>();
        
        String strQuery = "select distinct dr1.dam_name, '', dr1.dam_year_born, 'F'";
        strQuery += " from daily_runners dr1, daily_runners dr2";
        strQuery += " where dr1.runner_id=dr2.runner_id and dr1.dam_sire_name is not null and dr2.dam_sire_name is not null and dr1.dam_sire_name not like '%(%' and dr2.dam_sire_name not like '%(%' and (dr1.dam_sire_name not like '%unregistered%' or dr2.dam_sire_name not like '%unregistered%') and dr1.dam_sire_name != dr2.dam_sire_name";
        strQuery += " and not exists (select * from unknown_ancestors where dr1.dam_name=name)";
        
        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strName = rs.getString(1);
                    String strBred = rs.getString(2);
                    int nYearBorn = rs.getInt(3);
                    SmartformHorseDefinition horse = new SmartformHorseDefinition(strName, nYearBorn, strBred);
                    if (!rs.wasNull())
                        horse.setYearBorn(nYearBorn);
                    String strGender = rs.getString(4);
                    horse.setGender(strGender.charAt(0));
                   list.add(horse);
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
        }

        return list;
    }
    public static ArrayList<SmartformHorseDefinition> findSires(ENEStatement statement)
    {
        ArrayList<SmartformHorseDefinition> list = new ArrayList<SmartformHorseDefinition>();
       // Daily_runners 
 /*     String strQuery = "select distinct sire_name, '' as bred, sire_year_born";
        strQuery += " from ((((daily_runners dru inner join eneform_races er on dru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting')) inner join historic_races hra on dru.race_id=hra.race_id) left outer join ancestors a on replace(replace(replace(replace(sire_name, '''', ''), '.', ''), '-', ''), ' ', '') = a.name and sire_year_born=year_born and  a.sex in ('C', 'H')) left outer join unknown_ancestors ua on sire_name=ua.name)";
        strQuery += " where sire_year_born is not null and sire_name not like '%(%' and a.name is null and ua.name is null";
         strQuery += " union "; 
        strQuery += "select distinct sire_name, '' as bred, sire_year_born";
        strQuery += " from ((((daily_runners dru inner join eneform_races er on dru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting')) inner join historic_races hra on dru.race_id=hra.race_id) left outer join ancestors a on replace(replace(replace(replace(sire_name, '''', ''), '.', ''), '-', ''), ' ', '') = a.name and a.sex in ('C', 'H')) left outer join unknown_ancestors ua on sire_name=ua.name)";
        strQuery += " where sire_year_born is null and sire_name not like '%(%' and a.name is null and ua.name is null";
         strQuery += " order by sire_name";  */
         // Historic_runners
       String strQuery = "select distinct sire_name, '' as bred, null as sire_year_born";
        strQuery += " from ((((historic_runners hru inner join eneform_races er on hru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting')) inner join historic_races hra on hru.race_id=hra.race_id) left outer join ancestors a on replace(replace(replace(replace(sire_name, '''', ''), '.', ''), '-', ''), ' ', '') = a.name and  a.sex in ('C', 'H')) left outer join unknown_ancestors ua on sire_name=ua.name)";
        strQuery += " where sire_name not like '%(%' and a.name is null and ua.name is null";
        strQuery += " and not exists (select * from daily_runners dru where dru.runner_id=hru.runner_id)";  
        strQuery += " order by sire_name";  
        

        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strName = rs.getString(1);
                    String strBred = rs.getString(2);
                    int nYearBorn = rs.getInt(3);
                    SmartformHorseDefinition horse = new SmartformAncestor(strName, nYearBorn, strBred);
                    if (!rs.wasNull())
                        horse.setYearBorn(nYearBorn);
                    horse.setGender('H');
                    list.add(horse);
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
        }

        return list;
    }

    public static ArrayList<SmartformHorseDefinition> findDamSires(ENEStatement statement)
    {
        ArrayList<SmartformHorseDefinition> list = new ArrayList<SmartformHorseDefinition>();
      // Dail_runners  
 /*     String strQuery = "select distinct dam_sire_name, '' as bred, dam_sire_year_born";
        strQuery += " from ((((daily_runners dru inner join eneform_races er on dru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting')) inner join historic_races hra on dru.race_id=hra.race_id) left outer join ancestors a on replace(replace(replace(replace(dam_sire_name, '''', ''), '.', ''), '-', ''), ' ', '') = a.name and dam_sire_year_born=year_born and  a.sex in ('C', 'H')) left outer join unknown_ancestors ua on dam_sire_name=ua.name)";
        strQuery += " where dam_sire_year_born is not null and dam_sire_name not like '%(%' and a.name is null and ua.name is null";
         strQuery += " union "; 
        strQuery += "select distinct dam_sire_name, '' as bred, dam_sire_year_born";
        strQuery += " from ((((daily_runners dru inner join eneform_races er on dru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting')) inner join historic_races hra on dru.race_id=hra.race_id) left outer join ancestors a on replace(replace(replace(replace(dam_sire_name, '''', ''), '.', ''), '-', ''), ' ', '') = a.name and  a.sex in ('C', 'H')) left outer join unknown_ancestors ua on dam_sire_name=ua.name)";
        strQuery += " where dam_sire_year_born is null and dam_sire_name not like '%(%' and a.name is null and ua.name is null";
         strQuery += " order by dam_sire_name";  */
        
         // Historic_runners
       String strQuery = "select distinct dam_sire_name, '' as bred, null as dam_sire_year_born";
         strQuery += " from ((((historic_runners hru inner join eneform_races er on hru.race_id=er.race_id and er.race_type not in ('Arab', 'Trotting')) inner join historic_races hra on hru.race_id=hra.race_id) left outer join ancestors a on replace(replace(replace(replace(dam_sire_name, '''', ''), '.', ''), '-', ''), ' ', '') = a.name and  a.sex in ('C', 'H')) left outer join unknown_ancestors ua on dam_sire_name=ua.name)";
        strQuery += " where dam_sire_name not like '%(%' and a.name is null and ua.name is null";
       strQuery += " and not exists (select * from daily_runners dru where dru.runner_id=hru.runner_id)";  
         strQuery += " order by dam_sire_name"; 
 
        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strName = rs.getString(1);
                    String strBred = rs.getString(2);
                    int nYearBorn = rs.getInt(3);
                    //if (!rs.wasNull())
                    //    sire.setYearBorn(nYearBorn);
                   SmartformHorseDefinition horse = new SmartformHorseDefinition(strName, nYearBorn, strBred);
                    horse.setGender('H');
                   list.add(horse);
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
        }

        return list;
    }
public static ArrayList<SmartformHorseDefinition> findDams(ENEStatement statement)
    {
        ArrayList<SmartformHorseDefinition> list = new ArrayList<SmartformHorseDefinition>();
        
  /*      String strQuery = "select distinct case when dam_name like '%(%' then substr(dam_name,1, locate('(', dam_name) - 1) else dam_name end,";
        strQuery += "case when dam_name like '%(%' then substr(dam_name, locate('(', dam_name) + 1, locate(')', dam_name) - locate('(', dam_name) - 1) else '' end,";
        strQuery += "dam_year_born";
        //strQuery += " from daily_runners where sire_name is not null and sire_name not like '%(%' order by sire_name";
        // race_type = 'Flat'  and year(meeting_date) < 2010 
        strQuery += " from ((daily_runners dru inner join daily_races dra on dru.race_id=dra.race_id) left outer join unknown_ancestors ua on dam_name=ua.name and ua.sex='F')";
        strQuery += " where dam_name is not null and dam_year_born is not null and dam_name not like '%(%' and not exists (select * from ancestors s where s.name=replace(replace(replace(replace(dru.dam_name, '''', ''), '.', ''), '-', ''), ' ', '') and s.year_born=dru.dam_year_born)";
        //strQuery += " where dam_name is not null and dam_year_born is null and dam_name not like '%(%' and not exists (select * from ancestors s where s.name=replace(replace(replace(replace(dru.dam_name, '''', ''), '.', ''), '-', ''), ' ', ''))";
        strQuery += " and ua.name is null";
        strQuery += " order by dam_name";
        //strQuery += "dam_sire_name like 'entrepreneur%' or dam_sire_name like 'bustino%'";
*/
     /*   String strQuery = "select distinct dam_name, '' as bred, null as dam_year_born";
        strQuery += " from ((((historic_runners hru inner join eneform_races er on hru.race_id=er.race_id and er.race_type = 'Flat') inner join historic_races hra on hru.race_id=hra.race_id) left outer join ancestors a on replace(replace(replace(replace(dam_name, '''', ''), '.', ''), '-', ''), ' ', '') = a.name and  a.sex in ('F', 'M')) left outer join unknown_ancestors ua on dam_name=ua.name)";
        strQuery += " where dam_name not like '%(%' and a.name is null and ua.name is null";
        strQuery += " and not exists (select * from daily_runners dru where dru.runner_id=hru.runner_id)";  
        strQuery += " order by dam_name";   */

        String strQuery = "select distinct dr1.dam_name, '' as bred, null as dam_year_born";
        strQuery += " from historic_runners dr1, historic_runners dr2";
        strQuery += " where dr1.runner_id=dr2.runner_id and dr1.dam_sire_name is not null and dr2.dam_sire_name is not null and dr1.dam_sire_name not like '%(%' and dr2.dam_sire_name not like '%(%'";
        strQuery += " and replace(replace(replace(replace(dr1.dam_sire_name, '-', ''), '''', ''), '.', ''), ' ', '') != replace(replace(replace(replace(dr2.dam_sire_name, '-', ''), '''', ''), '.', ''), ' ', '')";
        strQuery += " order by 1, 2";  

        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strName = rs.getString(1);
                    String strBred = rs.getString(2);
                    int nYearBorn = rs.getInt(3);
                    //if (!rs.wasNull())
                    //    sire.setYearBorn(nYearBorn);
                   SmartformHorseDefinition horse = new SmartformHorseDefinition(strName, nYearBorn, strBred);
                    horse.setGender('M');
                    list.add(horse);
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
        }

        return list;
    }

    public static ArrayList<SmartformHorse> findHorse(ENEStatement statement, String strHorse)
    {
        // three possibilities: 1) a numeric id 2) a name 3) a wildcard search
        int nReturn = 0;
        ArrayList<SmartformHorse> list = new ArrayList<SmartformHorse>();
        boolean bExact = false;
        
        if (strHorse.indexOf("'") >= 0)
            strHorse = strHorse.replace("'", "''");

        String strWhere="";
        try
        {
            int nHorse = Integer.valueOf(strHorse);
            strWhere = "runner_id=" + nHorse;
        }
        catch (NumberFormatException e)
        {
        }
        if ("".equals(strWhere))
        {
           if (strHorse.indexOf("%") >= 0)
           {
               strWhere = "upper(name) like upper('" + strHorse +"')";
           }
           else
           {
                bExact = true;
                strWhere = "upper(name) = upper('" + strHorse +"')";
           }
        }
        // order by foaling_date desc so get most recent first - there are 2 Gleneagles, want most recent!
        String strQuery = "select distinct runner_id, name, upper(bred) as bred, year(foaling_date) as birth_year from historic_runners where " + strWhere + " order by name, foaling_date desc";

        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            int nCount = 0;
            if (rs != null)
            {
                while (rs.next())
                {
                    nCount++;
                    SmartformHorse horse = new SmartformHorse(rs.getInt("runner_id"), DbUtils.getDBString(rs, "name"));
                    horse.setBred(DbUtils.getDBString(rs, "bred"));
                    horse.setYearBorn(rs.getInt("birth_year"));
                    
                    list.add(horse);
                }
               rs.close();
            }

            if (bExact && (nCount == 0))
                return findHorse(statement, "%" + strHorse + "%");
        }
        catch(SQLException e)
        {
        }

        return list;
    }

public static boolean checkAncestor(ENEStatement statement, String strFullName, int nYearBorn)
{
    return false;
}
public static int insertPedigree(ENEStatement statement, SmartformPedigree pedigree, boolean bReplace)
{
    int nReturn = 0;
    String strUpdate="insert ";
    if (bReplace)
        strUpdate = "replace ";
    strUpdate += "into pedigree (name, bred, year_born, sex, ancestor_type, ancestor_name, ancestor_bred, ancestor_year_born)";
    strUpdate += " values (?, ?, ?, ?, ?, ?, ?, ?)";

    PreparedStatement insert = null;
    try
    {
     insert = statement.getPreparedStatement(strUpdate);
     insert.setString(1, pedigree.getName().replace("'", "''"));
     insert.setString(2, pedigree.getBred());
     insert.setInt(3, pedigree.getYearBorn());
     insert.setString(4, Character.toString(pedigree.getSex()));
     insert.setString(5, Character.toString(pedigree.getAncestorType()));
     insert.setString(6, pedigree.getAncestorName());
     insert.setString(7, pedigree.getAncestorBred());
     insert.setInt(8, pedigree.getAncestorYearBorn());
     nReturn  = insert.executeUpdate();
    }
    catch(SQLException e)
    {
        System.out.println("insertPedigree Exception: " + e.getMessage());
    }
/*    finally
    {
        if (insert != null)
        {
            try
            {
                insert.close();
            }
            catch(SQLException e)
            {
                
            }
        }
    } */
 
     return nReturn;
}
public static int insertAncestorGRASP(ENEStatement statement, SmartformHorseDefinition horse, boolean bReplace)
{
    int nReturn = 0;
    String strUpdate="insert ";
    if (bReplace)
        strUpdate = "replace ";
    strUpdate += "into ancestor_grasps (name, bred, year_born, grasp)";
    strUpdate += " values (?, ?, ?, ?)";

    PreparedStatement insert = null;
    try
    {
     insert = statement.getPreparedStatement(strUpdate);
     insert.setString(1, horse.getName().replace("'", "''"));
     insert.setString(2, horse.getBred());
     insert.setInt(3, horse.getYearBorn());
     //insert.setString(4, Character.toString(horse.getGender()));
     insert.setDouble(4, horse.getGRASP());
      nReturn  = insert.executeUpdate();
    }
    catch(SQLException e)
    {
        System.out.println("insertAncestorGRASP Exception: " + e.getMessage());
    }
/*    finally
    {
        if (insert != null)
        {
            try
            {
                insert.close();
            }
            catch(SQLException e)
            {
                
            }
        }
    } */
 
     return nReturn;
}
public static int insertAncestorGeneration(ENEStatement statement, SmartformAncestorGeneration sire)
{
    int nReturn = 0;
    String strUpdate = "replace into ancestor_generations (name, bred, year_born, generation, brilliant, intermediate, classic, solid, professional)";
    strUpdate += " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    PreparedStatement insert = null;
    try
    {
     insert = statement.getPreparedStatement(strUpdate);
     insert.setString(1, sire.getName().replace("'", "").replace(".", "").replace("-", "").replace(" ", ""));
     insert.setString(2, sire.getBred());
     insert.setInt(3, sire.getYearBorn());
     insert.setInt(4, sire.getGeneration());
     insert.setInt(5, sire.getBrilliance());
     insert.setInt(6, sire.getIntermediate());
     insert.setInt(7, sire.getClassic());
     insert.setInt(8, sire.getSolid());
     insert.setInt(9, sire.getProfessional());
     nReturn  = insert.executeUpdate();
    }
    catch(SQLException e)
    {
        System.out.println("insertAncestorGeneration Exception: " + e.getMessage());
    }
/*    finally
    {
        if (insert != null)
        {
            try
            {
                insert.close();
            }
            catch(SQLException e)
            {
                
            }
        }
    } */
 
     return nReturn;
}
public static int insertAncestor(ENEStatement statement, SmartformAncestor sire)
{
    int nReturn = 0;
    String strUpdate = "replace into ancestors (name, bred, sex, colour, year_born, brilliant, intermediate, classic, solid, professional, career_earnings, nr_starts, nr_wins, nr_places, nr_shows, gsv, display_name, pq_code)";
    strUpdate += " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    PreparedStatement insert = null;
    try
    {
     insert = statement.getPreparedStatement(strUpdate);
     insert.setString(1, sire.getName().replace("'", "").replace(".", "").replace("-", "").replace(" ", ""));
     insert.setString(2, sire.getBred());
     insert.setString(3, Character.toString(sire.getGender()));
     insert.setString(4, sire.getColour());
     insert.setInt(5, sire.getYearBorn());
     insert.setInt(6, sire.getConduitBrilliance());
     insert.setInt(7, sire.getConduitIntermediate());
     insert.setInt(8, sire.getConduitClassic());
     insert.setInt(9, sire.getConduitSolid());
     insert.setInt(10, sire.getConduitProfessional());
     String strCareerEarnings = sire.getCareerEarnings();
     if ((strCareerEarnings != null) && (strCareerEarnings.length() > 40))
         strCareerEarnings = strCareerEarnings.substring(40);
     insert.setString(11, strCareerEarnings);
     if (sire.getStarts() >= 0)
         insert.setInt(12,  sire.getStarts());
     else
         insert.setNull(12, java.sql.Types.INTEGER);
     if (sire.getWins() >= 0)
         insert.setInt(13,  sire.getWins());
     else
         insert.setNull(13, java.sql.Types.INTEGER);
     if (sire.getPlaces() >= 0)
         insert.setInt(14,  sire.getPlaces());
     else
         insert.setNull(14, java.sql.Types.INTEGER);
     if (sire.getShows() >= 0)
         insert.setInt(15,  sire.getShows());
     else
         insert.setNull(15, java.sql.Types.INTEGER);
     if (sire.getGSV() >= 0)
         insert.setDouble(16, sire.getGSV());
     else
         insert.setNull(16, java.sql.Types.DOUBLE);
     insert.setString(17, sire.getName().trim());
     insert.setString(18, sire.getPQCode().trim());
     
     nReturn  = insert.executeUpdate();
     for(int i = 0; i < 5; i++)
     {
         insertAncestorGeneration(statement, sire.getGeneration(i));
     }
    }
    catch(SQLException e)
    {
        System.out.println("insertAncestor Exception: " + e.getMessage());
    }
/*    finally
    {
        if (insert != null)
        {
            try
            {
                insert.close();
            }
            catch(SQLException e)
            {
                
            }
        }
    } */
 
     return nReturn;
}
}
