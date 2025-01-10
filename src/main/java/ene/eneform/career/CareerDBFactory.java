/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.colours.web.rp.RacingPostHorse;
import ene.eneform.smartform.bos.AdditionalRaceInstance;
import ene.eneform.smartform.bos.SmartformBasicRace;
import ene.eneform.smartform.bos.SmartformHistoricRunner;
import ene.eneform.smartform.factory.SmartformRaceFactory;
import ene.eneform.utils.ENEStatement;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class CareerDBFactory {
    
    static public int insertCareerNames(ENEStatement statement) {
        int nInserts = 0;
        ArrayList<String> alNames = CareerEnvironment.getInstance().getCareerNames();
        if(alNames.size() > 0)
        {
            String strInsert = "insert into career_names (horse_name)";
            String strHorseList = "select '" + alNames.get(0).replace("'", "''") + "' as name";
            for(int i = 0; i < alNames.size(); i++)
                strHorseList += " union select '" + alNames.get(i).replace("'", "''") + "'";
            strInsert += "(select name from (" + strHorseList + ") d1 where not exists (select * from career_names where horse_name=name))";
            nInserts = statement.executeUpdate(strInsert);
        }
        
        return nInserts;
     }
    public static ArrayList<String> getAllCareerNames(ENEStatement statement)
    {
        ArrayList<String> list = new ArrayList<String>();
        
        String strQuery = "select horse_name from career_horses";
       
        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strName = rs.getString(1);
                    list.add(strName);
                }
               rs.close();
               // 20180819: Must not close statement, it is assumed that it can be reused
               //statement.close();
            }

        }
        catch(SQLException e)
        {
        }

        return list;
    }
    public static ArrayList<String> selectCareerHorses(ENEStatement statement, String strWhere)
    {
        ArrayList<String> list = new ArrayList<String>();
        
        String strQuery = "select concat(trim(horse_name), '|', bred) as name";
        strQuery += " from career_horses";
        if (!"".equals(strWhere))
            strQuery += (" where " + strWhere);
       
        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strName = rs.getString(1);
                    list.add(strName);
                }
               rs.close();
            }

        }
        catch(SQLException e)
        {
        }

        return list;
    }
    static public void insertCareerHorses(ENEStatement statement) {
        String strInsert = "insert into career_horses (horse_name, bred, foaling_date, colour, gender, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born, trainer_name, owner_name)";
        strInsert += "(select distinct c.name, bred, foaling_date, colour, gender, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born,trainer_name, owner_name";
        strInsert += " from career_runners c inner join (select name, max(meeting_date) as latest_date from career_runners group by name) t1 on c.name=t1.name and meeting_date=latest_date";
        strInsert += " where c.name not in (select horse_name from career_horses) and c.name not in ('Ruler of the world'))";
        statement.executeUpdate(strInsert);
     }
    
    static public void updateCareerHorses(ENEStatement statement, String strWhere) throws IOException
    {
        List<String> aNames = CareerDBFactory.selectCareerHorses(statement, strWhere);
        for(int i = 0; i < aNames.size(); i++)
        {
            System.out.println(aNames.get(i));
            
            String strName=aNames.get(i).split("\\|")[0];
            String strBred = "";
            if (aNames.get(i).split("\\|").length > 1)
                strBred = aNames.get(i).split("\\|")[1];
            RacingPostHorse horse = RacingPostHorse.getHorse(strName, strBred, 2017);
            if (horse != null)
            {
                String strUpdate = horse.updateCareerDetails();
                if (!"".equals(strUpdate))
                    statement.executeUpdate(strUpdate);           
            }
        }                       
    }
public static List<CareerHorse> getRunnerNames(ENEStatement stmt, String strWhere)
 {
     System.out.println("getRunnerNames: " + strWhere);
     ArrayList<CareerHorse> aHorses = new ArrayList<CareerHorse>();
   SmartformHistoricRunner runner = null;
   // use daily_races so can filter by country
    String strQuery = "select horse_name, bred, foaling_date, colour, gender, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born, trainer_name, owner_name, wi_owner, finish_position, race_title";
    strQuery += " from (select distinct c.horse_name, c.bred, c.foaling_date, c.colour, c.gender, c.dam_name, c.dam_year_born, c.sire_name, c.sire_year_born, c.dam_sire_name, c.dam_sire_year_born, c.trainer_name, c.owner_name, wi_owner, case when coalesce(unfinished, '') != '' then unfinished else concat(coalesce(amended_position,finish_position), '') end as finish_position, coalesce(arl_name, '') as race_title, meeting_date from career_horses c inner join historic_runners h on c.horse_name=h.name inner join historic_races a on a.race_id=h.race_id inner join racing_post_course_2017 on course=sf_course_name left outer join additional_race_link on a.race_id=arl_race_id and arl_source = 'SF' where coalesce(h.unfinished, '') != 'Non-Runner' " + strWhere + "";
    strQuery += " union select distinct c.horse_name, c.bred, c.foaling_date, c.colour, c.gender, c.dam_name, c.dam_year_born, c.sire_name, c.sire_year_born, c.dam_sire_name, c.dam_sire_year_born, c.trainer_name, c.owner_name, wi_owner, case when coalesce(unfinished, '') != '' then unfinished else concat(coalesce(amended_position,finish_position), '') end as finish_position, coalesce(arl_name, '') as race_title, meeting_date from career_horses c inner join additional_runners h on c.horse_name=h.name inner join additional_races a on h.race_id = a.race_id and a.meeting_date < date_add(c.foaling_date, interval 15 year) inner join racing_post_course_2017 on course=rp_course_name left outer join additional_race_link on a.race_id=arl_race_id and arl_source = ara_source where 1=1 " + strWhere + "";
    strQuery += ") d1 order by horse_name, meeting_date";
    
    ResultSet rs = stmt.executeQuery(strQuery);
    CareerHorse horse = null;
    if (rs != null)
    {
        try
        {
            String strPreviousHorse = "";
            while (rs.next())   // 1 record
            {
                // horse_name, bred, foaling_date, colour, gender, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born, trainer_name, owner_name
                String strHorse = rs.getString("horse_name");
                if (!strPreviousHorse.equals(strHorse))
                {
                    if (horse != null)
                        aHorses.add(horse);
                    strPreviousHorse = strHorse;
                    horse = new CareerHorse(strHorse);    
                    horse.setBred(rs.getString("bred"));
                    horse.setFoalingDate(rs.getString("foaling_date"));
                    horse.setColour(rs.getString("colour"));
                    horse.setGender(rs.getString("gender"));
                    horse.setDamName(rs.getString("dam_name"));
                    horse.setDamYearBorn(rs.getInt("dam_year_born"));
                    horse.setSireName(rs.getString("sire_name"));
                    horse.setSireYearBorn(rs.getInt("sire_year_born"));
                    horse.setDamSireName(rs.getString("dam_sire_name"));
                    horse.setDamSireYearBorn(rs.getInt("dam_sire_year_born"));
                    horse.setTrainerName(rs.getString("trainer_name"));
                    horse.setOwnerName(rs.getString("owner_name"));
                    horse.setWiOwner(rs.getString("wi_owner"));
                }
                String strPosition = rs.getString("finish_position");
                String strRaceName = rs.getString("race_title");
                if ((!"".equals(strRaceName)))
                {
                    horse.addMajorRace(strRaceName, strPosition);
                }
             }
            rs.close();
        }
        catch(SQLException e)
        {
            
        }
    }
    if (horse != null)
        aHorses.add(horse);

    return aHorses;
     
 }

    public static String nameMatch(ArrayList<String> aRunners, String strName) {
        for (int i = 0; i < aRunners.size(); i++) {
            if (aRunners.get(i).equalsIgnoreCase(strName)) {
                return aRunners.get(i);
            }
        }
        return null;
    }

    public static List<AdditionalRaceInstance> getMeetingRaces(ENEStatement statement, int nYear, String strCourse, int nMonth) 
    {
        List<AdditionalRaceInstance> lstRaces = SmartformRaceFactory.getRaceInstances(statement, "d.course='" + strCourse + "' and year(d.meeting_date) = " + nYear + " and month(d.meeting_date) = " + nMonth);
        return lstRaces;
    }
    public static List<AdditionalRaceInstance> getCategoryRaces(ENEStatement statement, String strCategory, String strSubCategory, String strStartDate, String strEndDate, boolean bFixtures) 
    {
        
        List<AdditionalRaceInstance> lstRaces = SmartformRaceFactory.getRaceInstancesByCategory(statement, strCategory, strSubCategory, strStartDate, strEndDate, bFixtures);
        return lstRaces;
    }
    public static String convertRaceTitle(AdditionalRaceInstance arl, String strTopic, String strRaceType) {
        // 20180908 - assume age and sex not shown, and then add back in for categories where it should be
        boolean bAge = false;
        boolean bSex = false;
        /*
        if ((strTopic.toLowerCase().equals("derby")) || (strTopic.toLowerCase().equals("oaks")) || (strTopic.toLowerCase().equals("stleger")) || (strTopic.toLowerCase().equals("guineas1000")) || (strTopic.toLowerCase().equals("guineas2000")) || (strTopic.toLowerCase().indexOf("filly") >= 0)) {
            bAge = false;
            bSex = false;
        }
        if ((strTopic.toLowerCase().equals("classics")) || (strTopic.toLowerCase().indexOf("threeyear") == 0) || (strTopic.toLowerCase().indexOf("twoyear") == 0) || (strTopic.toLowerCase().equals("threeyear")) || (strTopic.toLowerCase().equals("juvenile_hurdle"))) {
            bAge = false;
        } */
        if (strTopic.toLowerCase().equals("twoyear"))
            bSex = true;
        String strTitle = arl.getTitle();
        if (strTitle == null) {
            strTitle = "";
        }
        strTitle = SmartformBasicRace.replaceSponsorString(strTitle);
        strTitle = strTitle.replace("(Grade 1)", "").replace("(Fillies Group 3)", "").replace("(Fillies' Group 3)", "").replace("(Fillies' Group 2)", "").replace("(Grade 2)", "").replace("(Grade 3)", "").replace("(Group 1)", "").replace("(Group 2)", "").replace("(Group 3)", "").replace("(Listed)", "").replace("(Listed Race)", "").replace("(Fillies' Listed)", "");
        strTitle = strTitle.replace("(3yo Fillies)", "").replace("(4yo+)", "").replace("(Turf)", "").replace("(4yo+ Fillies & Mares)", "").replace("(3yo+ Fillies & Mares)", "").replace("(2yo Colts & Fillies)", "").replace("(3yo Colts)", "").replace("(Straight)", "").replace("4-Y-O", "").replace("4YO plus", "").replace("5YO plus", "").replace("6YO plus", "").replace("(2yo)", "").replace("(2yo Fillies)", "").replace("(3yo)", "").replace("(3yo Colts & Fillies)", "").replace("(3yo+)", "").trim();
        int nLocation = 0;
        if (strTopic.contains("novice")) {
            int nNovice = strTitle.toLowerCase().indexOf("novice");
            if (nNovice > 0) {
                strTitle = strTitle.substring(0, nNovice).trim();
            }
        } else if (strTopic.contains("juvenile")) {
            int nJuvenile = strTitle.toLowerCase().indexOf("juvenile");
            if (nJuvenile > 0) {
                strTitle = strTitle.substring(0, nJuvenile).trim();
            }
        } else if ((nLocation = strTitle.toLowerCase().lastIndexOf(strRaceType.toLowerCase())) > 0) {
            strTitle = strTitle.substring(0, nLocation).trim();
        }
        if (bAge) {
            strTitle += (arl.getAgeRange());
        }
        if (bSex) {
            strTitle += (" " + arl.getSex());
        }
        return strTitle.trim();
    }

    public static List<CareerHorse> getSeasonCareerHorses(ENEStatement statement, int nYear, String strRaceType, String strCountry, int nAge, String strGender) {
        String strWhere = "";
        if ("NH".equals(strRaceType) || "Chase".equals(strRaceType) || "Hurdle".equals(strRaceType))
        {
            // JUMPS
            if ("NH".equals(strRaceType))
            {
                strWhere += (" and race_type in ('Chase', 'Hurdle', 'N_H_Flat')");
            } 
            else if ("Chase".equals(strRaceType) || "Hurdle".equals(strRaceType)) 
            {
                strWhere += (" and race_type='" + strRaceType + "'");
            }
            strWhere += (" and meeting_date >='" + (nYear - 1) + "-07-01' and meeting_date < '" + nYear + "-07-01'");
            // gender used for novice
            if (strGender == null) 
            {
                strGender = ""; // all chasers/hurdlers
            } 
            else if ("juvenile".equals(strGender.toLowerCase()) || "novice".equals(strGender.toLowerCase())) 
            {
                strWhere += (" and locate('" + strGender + "', lower(race_name)) > 0");
            } 
            else if ("mares".equals(strGender.toLowerCase())) 
            {
                strWhere += (" and (h.gender='F' or h.gender='M')");
            } 
            else 
            {
                strWhere += (" and race_name not like '%novice%' and race_name not like '%juvenile%' and race_name not like '%beginner%' and race_name not like '%maiden%'");
            }
        }
        else 
        {
            // FLAT
            strWhere += (" and race_type='" + strRaceType + "'");
            strWhere += (" and year(meeting_date)=" + nYear);
            // gender only for flat
            if ((strGender != null) && !"".equals(strGender)) {
                strWhere += (" and locate(h.gender, '" + strGender + "') > 0");
            }
        }
        if (nAge > 0) {
            if ("Flat".equals(strRaceType) && (nAge == 4)) {
                strWhere += (" and h.age >= 4");
            } else {
                strWhere += (" and h.age = " + nAge);
            }
        }
        if ("".equals(strCountry)) {
            //
        } else if ("UKI".equals(strCountry)) {
            strWhere += (" and rp_country in ('GB', 'IRE')");
        } else {
            strWhere += (" and rp_country='" + strCountry + "'");
        }
        List<CareerHorse> lstSeasonHorses = CareerDBFactory.getRunnerNames(statement, strWhere);
        return lstSeasonHorses;
    }

    private static String getTopicWhere(String strWhere, String strRaceType, String strTopic, int nGroupRace) {
        if (strRaceType.equals("Chase") || strRaceType.equals("Hurdle")  || strRaceType.equals("NH")) 
        {
            if (strRaceType.equals("NH"))
                strWhere += " and arl_name in (select ard_name from additional_race_data where ard_race_type in ('Chase', 'Hurdle', 'N_H_Flat') and ard_country in ('England', 'Scotland', 'Wales', 'Eire', 'Northern Ireland')";
            else if (strTopic.indexOf("france") >= 0)
                strWhere += " and arl_name in (select ard_name from additional_race_data where ard_race_type='" + strRaceType + "' and ard_country = 'France'";
            else
                strWhere += " and arl_name in (select ard_name from additional_race_data where ard_race_type='" + strRaceType + "' and ard_country in ('England', 'Scotland', 'Wales', 'Eire', 'Northern Ireland')";
            // Mares
            if (strTopic.toLowerCase().indexOf("mare") >= 0) 
            {
                strWhere += " and ard_name like '%mare%'";
            } 
            else
            {
                strWhere += " and ard_name not like '%mare%'";
            }
            if (strTopic.toLowerCase().equals("conditions")) 
            {
                strWhere += " and ard_handicap = 0 and ((ard_name not like '%mare%' and ard_group_race in (1, 2, 3)) or (ard_name like '%mare%' and ard_group_race in (1, 2, 3, 4)))";
            } 
            else if (strTopic.toLowerCase().equals("handicap")) {
                strWhere += " and ard_handicap = 1 and ((ard_name not like '%mare%' and ard_group_race in (1, 2, 3)) or (ard_name like '%mare%' and ard_group_race in (1, 2, 3, 4)))";
            } 
            else 
            {
                if ("france".equals(strTopic))
                {
                    strWhere += " and ard_group_race = 1";
                }
                else if (nGroupRace == 0) 
                {
                    strWhere += " and ard_group_race in (1, 2, 3, 4)"; // Listed also?
                } 
                else 
                {
                    strWhere += " and ard_group_race = " + nGroupRace;
                }
            }
                // Distance
                if (strTopic.toLowerCase().indexOf("two_mile") == 0) {
                    strWhere += " and ard_distance_yards/220 <= 18";
                } else if (strTopic.toLowerCase().indexOf("two_half_mile") == 0) {
                    strWhere += " and ard_distance_yards/220 >18 and ard_distance_yards/220 <=21";
                } else if (strTopic.toLowerCase().indexOf("three_mile") == 0) {
                    strWhere += " and ard_distance_yards/220 > 21";
                }
                // Novice, Juvenile
                if (strTopic.toLowerCase().indexOf("novice") >= 0) 
                {
                    strWhere += " and (ard_name like '%novice%' or ard_name like '%4-y-o%')";
                } 
                else if (strTopic.toLowerCase().indexOf("juvenile") >= 0) 
                {
                    strWhere += " and ard_name like '%juvenile%'";
                } 
                else  
                {
                    strWhere += " and ard_name not like '%novice%' and ard_name not like '%4-y-o%' and ard_name not like '%juvenile%'";
                }
                // Handicap
                if (strTopic.toLowerCase().indexOf("hcap") >= 0) {
                    strWhere += " and ard_handicap = 1";
                } else {
                    strWhere += " and ard_handicap = 0";
                }
            strWhere += ")";
        } else {
            // if no country then must be topic based - Flat
            if (strTopic.toLowerCase().equals("classics")) {
                strWhere += " and ard_name in ('1000 Guineas', '2000 Guineas', 'Derby', 'Oaks', 'St Leger', 'Irish 1000 Guineas', 'Irish 2000 Guineas', 'Irish Derby', 'Irish Oaks', 'French 1000 Guineas', 'French 2000 Guineas', 'French Derby', 'Prix de Diane')";
            } else if (strTopic.toLowerCase().indexOf("guineas2000") == 0) {
                strWhere += " and ard_name in ('Burradon', 'UAE 2000 Guineas', 'Lingfield Spring Cup', 'UAE Derby', 'International Trial Stakes', 'Leopardstown 2000 Guineas Trial', 'Gladness', 'Prix Djebel', 'Patton Stakes', 'Prix de Fontainebleau', 'European Free Handicap', 'Craven', 'Greenham', 'Dr. Busch-Memorial', 'Premio Parioli', 'Tetrarch', '2000 Guineas','French 2000 Guineas', 'Irish 2000 Guineas', 'St James Palace', 'Jean Prat', 'Mehl-Mulhens-Rennen')";
            } else if (strTopic.toLowerCase().indexOf("guineas1000") == 0) {
                strWhere += " and ard_name in ('Nell Gwyn','Fred Darling','Leopardstown 1000 Guineas Trial','Prix Imprudence','Prix de la Grotte', 'Masaka Stakes','UAE 1000 Guineas','Park Express','Athasi','Derrinstown Stud 1000 Guineas Trial','1000 Guineas', 'French 1000 Guineas', 'Irish 1000 Guineas', 'Coronation Stakes')";
            } else if (strTopic.toLowerCase().indexOf("derby") == 0) {
                strWhere += " and ard_name in ('2000 Guineas', 'Feilden', 'Epsom Derby Trial', 'Guardian Classic Trial', 'Ballysax', 'Predominate', 'Chester Vase', 'Dante', 'Dee Stakes', 'Fairway', 'Yeats Stakes', 'Newmarket Stakes', 'Lingfield Derby Trial', 'Derrinstown Stud Derby Trial', 'Derby', 'Gallinule', 'King Edward VII', 'Irish Derby', 'Grand Prix de Paris', 'UAE Derby')";
            } else if (strTopic.toLowerCase().indexOf("oaks") == 0) {
                strWhere += " and ard_name in ('1000 Guineas', 'Cheshire Oaks', 'Oaks', 'Lupe', 'Musidora', 'Swettenham Stud Fillies', 'Lingfield Oaks Trial', 'Irish Oaks', 'Pretty Polly', 'Ribblesdale', 'Saint-Alary', 'Prix de Diane', 'Naas Oaks Trial','UAE Oaks')";
            } else if (strTopic.toLowerCase().indexOf("stleger") == 0) {
                strWhere += " and ard_name in ('Derby', 'Irish Derby', 'Gordon', 'Great Voltigeur', 'St Leger', 'King George V Cup', 'Noel Murless', 'Glasgow', 'Queens Vase')";
            } else if (strTopic.toLowerCase().indexOf("threeyearsprint") == 0) {
                strWhere += " and ard_name in ('Pavilion Stakes', 'Carnarvon', 'Sandy Lane', 'Power Stakes', 'Westow Stakes', 'Lacken Stakes', 'Commonwealth Cup')";
            } else {
                // Flat
                strWhere += " and arl_name in (select ard_name from additional_race_data where ard_race_type='" + strRaceType + "' and ard_country in ('England', 'Scotland', 'Wales', 'Eire', 'Northern Ireland', 'France')";
                strWhere += " and ard_handicap = 0";
                if (nGroupRace == 0) {
                    strWhere += " and ard_group_race in (1, 2, 3)";
                } else {
                    strWhere += " and ard_group_race = " + nGroupRace;
                }
                if (strTopic.toLowerCase().indexOf("twoyear") == 0) {
                    strWhere += " and ard_age_range in ('2yo', '2YO only')";
                } else if (strTopic.toLowerCase().indexOf("threeyear") == 0) {
                    strWhere += " and ard_age_range in ('3yo', '3YO only')";
                } else {
                    strWhere += " and ard_age_range not in ('2yo', '2YO only', '3yo', '3YO only')";
                }
                if (strTopic.toLowerCase().equals("sprint")) {
                    strWhere += " and ard_distance_yards/220 <= 6.1";
                } else if (strTopic.toLowerCase().equals("seven")) {
                    strWhere += " and ard_distance_yards/220.0 > 6.1 and ard_distance_yards/220 < 7.9";
                } else if (strTopic.toLowerCase().equals("mile")) {
                    strWhere += " and ard_distance_yards/220.0 >= 7.9 and ard_distance_yards/220 < 8.9";
                } else if (strTopic.toLowerCase().equals("quarter")) {
                    strWhere += " and ard_distance_yards/220.0 >= 8.9 and ard_distance_yards/220 < 10.9";
                } else if (strTopic.toLowerCase().equals("half")) {
                    strWhere += " and ard_distance_yards/220.0 > 10.9 and ard_distance_yards/220 < 12.9";
                } else if (strTopic.toLowerCase().equals("stayer")) {
                    strWhere += " and ard_distance_yards/220.0 >= 12.9";
                } else if (strTopic.toLowerCase().equals("fmshort")) {
                    strWhere += " and ard_distance_yards/220.0 <= 8.5 and ard_sex in ('F', 'FM')";
                } else if (strTopic.toLowerCase().equals("fmlong")) {
                    strWhere += " and ard_distance_yards/220.0 > 8.5 and ard_sex in ('F', 'FM')";
                } else if (strTopic.toLowerCase().indexOf("filly") >= 0) {
                    strWhere += " and ard_sex in ('F', 'FM')";
                } else if (strTopic.toLowerCase().indexOf("colt") >= 0 || strTopic.equals("half") || strTopic.equals("quarter") || strTopic.equals("seven") || strTopic.equals("mile") || strTopic.equals("sprint") || strTopic.equals("stayer")) {
                    // older have specific Fillies/Mares categories
                    strWhere += " and ard_sex not in ('F', 'FM')";
                }
                strWhere += ")";
            }
        }
        return strWhere;
    }
}
