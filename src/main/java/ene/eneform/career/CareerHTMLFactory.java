/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.colours.bos.ENEColoursDBEnvironment;
import ene.eneform.colours.database.AdditionalRaceLinkFactory;
import ene.eneform.colours.database.ENEColoursRunnerFactory;
import ene.eneform.colours.database.WikipediaFactory;
import ene.eneform.colours.service.WikipediaService;
import ene.eneform.smartform.bos.AdditionalRaceInstance;
import ene.eneform.smartform.bos.SmartformColoursRunner;
import ene.eneform.smartform.bos.SmartformRace;
import ene.eneform.smartform.bos.SmartformRunner;
import ene.eneform.smartform.factory.SmartformRaceFactory;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.Pair;
import ene.eneform.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author Simon
 */

// TO BE REMOVED - replaced by CareerJSONFactory
public class CareerHTMLFactory {

    public static final String HTML_OUTPUT_PATH="C:\\Users\\simon\\Documents\\NetBeansProjects\\JockeyColours\\web\\";
    private String m_strHorse;
    private String m_strBred;
    private ENEStatement m_statement;
    private List<AdditionalRaceInstance> m_aRaces = null;
    private GregorianCalendar m_calendar = new GregorianCalendar();
    private int m_anMonthTotals[][] = null;
    private int m_anMonthMax[] = null;
    private int m_nMinMonth = 12;
    private int m_nMaxMonth = 0;
    private int m_nMaxWidth = 0;
    private boolean m_bFlat = true;
    private static final String[] FLAT_MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private static final String[] JUMP_MONTHS = {"August", "September", "October", "November", "December", "January", "February", "March", "April", "May", "June", "July"};
    
    public CareerHTMLFactory(ENEStatement statement, String strHorse)
    {
        this(statement, strHorse, null, "");
    }
    public CareerHTMLFactory(ENEStatement statement, String strHorse, String strBred)
    {
        this(statement, strHorse, strBred, "");
    }
    public CareerHTMLFactory(ENEStatement statement, String strHorse, String strBred, String strWhere)
    {
        m_statement = statement;
        m_strHorse = strHorse;
        m_strBred = strBred;
        m_aRaces = AdditionalRaceLinkFactory.getAdditionalRaceHorseLinks(statement, strHorse, strBred, strWhere);
        ArrayList<String> alFlatSeasons = new ArrayList<String>();
        ArrayList<String> alNHSeasons = new ArrayList<String>();
        for (int i = 0; i < m_aRaces.size(); i++)
        {
            AdditionalRaceInstance race = m_aRaces.get(i);
            String strSeason = race.getSeasonString();
            if ((strSeason.indexOf("/") > 0) || (strSeason.indexOf("-") > 0))
            {
               if (!alNHSeasons.contains(strSeason))     
                    alNHSeasons.add(strSeason);
            }
            else
            {
               if (!alFlatSeasons.contains(strSeason))     
                    alFlatSeasons.add(strSeason);
            }
        }
        if (alNHSeasons.size() > alFlatSeasons.size())
            m_bFlat = false;

        analyse();  // set up arrays etc
        System.out.println(toString());
    }
    public  String generateCareer() 
    {
        String strContent = "<career id=\"" + m_strHorse + "\" name=\"" + m_strHorse + "\">\n";
        int nRaces = m_aRaces.size();
        strContent += generateRows();
        strContent += generateColumns();
        strContent += "</career>";
        return strContent;
    }
    private String generateRows()
    {
        String strContent = "";
        String strCurrentSeason = "";
        int nRaces = m_aRaces.size();
        strContent += "<rows>\n";
        int nPreviousMonth = m_nMinMonth - 1;
        int nCurrentMonthCount = 0;
        for (int i = 0; i < nRaces; i++) 
        {
            AdditionalRaceInstance race = m_aRaces.get(i);
            String strSeason = race.getSeasonString().replace("-", "/");
            m_calendar.setTimeInMillis(race.getDateTime().getTime());
            int nMonth = getMonthNumber();
            if (!strSeason.equals(strCurrentSeason)) 
            {
                // new row
                if (i > 0) 
                {
                    strContent += "</row>\n";
                }
                strContent += "<row title=\"" + strSeason + "\">\n";
                strCurrentSeason = strSeason;
                nPreviousMonth=m_nMinMonth - 1;
                nCurrentMonthCount = 1;
            }
            if (nMonth == nPreviousMonth)
            {
                nCurrentMonthCount++;
            }
            else
            {
                // about to write cell, but is it necessary to add empty cells for padding
                int nBlankCellCount = countBlankCells(nPreviousMonth, nMonth, nCurrentMonthCount);
                if(nBlankCellCount == 0)
                {
                    // no blanks
                }
                else if(nBlankCellCount == 1)
                {
                    strContent += "<cell></cell>\n";
                }
                else
                {
                    strContent += "<cell colspan=\"" + nBlankCellCount + "\"></cell>\n";
                }
                nCurrentMonthCount = 1;
            }

            String strSource = race.getSource();
            String strRaceTitle=CareerEnvironment.getInstance().getCareerRaceName(strSource + race.getRaceId());
            String strFullRaceTitle = race.getTitle();
            int nBracket = strFullRaceTitle.indexOf("(");
            if (nBracket > 0)
            strFullRaceTitle = strFullRaceTitle.substring(0, nBracket - 1);

            strContent+= "<cell><race";
            boolean bNewTitle = false;
            if (!"SF".equals(strSource))
                strContent += " source=\"" + strSource + "\"";
            if (strRaceTitle == null || "".equals(strRaceTitle))
            {
                strRaceTitle = race.getAbbreviatedTitle();
                bNewTitle = true;
            }
            strContent += " title=\"" + strRaceTitle.replace("&", "&amp;") + "\"";
            if (bNewTitle && !strRaceTitle.equals(strFullRaceTitle))
            {
                strContent += " full_title=\"" + strFullRaceTitle.replace("&", "&amp;") + "\"";
            }
            strContent+=(">" + race.getRaceId() + "</race></cell>\n");
            nPreviousMonth = nMonth;
        }
        strContent += "</row>\n";
        strContent += "</rows>\n";
        return strContent;
    }
    private String generateColumns()
    {
        String strContent = "";
        strContent += "<columns>\n";
        for (int j = 0; j < m_anMonthMax.length; j++) 
        {
            if (m_anMonthMax[j] > 0)
            {
                if (m_anMonthMax[j] == 1)
                {
                    strContent += "<column>" + (m_bFlat ? FLAT_MONTHS[j + m_nMinMonth] : JUMP_MONTHS[j + m_nMinMonth]) + "</column>\n";
                }
                else
                {
                    strContent += "<column colspan=\"" + m_anMonthMax[j] + "\">" + (m_bFlat ? FLAT_MONTHS[j + m_nMinMonth] : JUMP_MONTHS[j + m_nMinMonth]) + "</column>\n";
                }
            }
        }
        strContent += "</columns>\n";
        return strContent;
    }
    public static String generateCareers(ENEStatement statement, String[] astrHorses) {
        String strCareers = "";
        for (int i = 0; i < astrHorses.length; i++) {
            String strCareer = new CareerHTMLFactory(statement, astrHorses[i]).generateCareer();
            strCareers += (strCareer + "\n");
        }
        return strCareers;
    }

    public void analyse() 
    {
        String strCurrentSeason = "";
        int nRaces = m_aRaces.size();
        int nSeasons = 0;
        int nWidth = 0;
        // 1) calculate dimensions of matrix
        for (int i = 0; i < nRaces; i++) 
        {
            AdditionalRaceInstance race = m_aRaces.get(i);
            String strSeason = race.getSeasonString().replace("-", "/");
            if (!strSeason.equals(strCurrentSeason)) 
            {
                nSeasons++;
                strCurrentSeason = strSeason;
                if (nWidth > m_nMaxWidth) 
                {
                    m_nMaxWidth = nWidth;
                }
                nWidth = 0;
            } 
            else 
            {
                nWidth++;
            }
            m_calendar.setTimeInMillis(race.getDateTime().getTime());
            int nMonth = getMonthNumber();    // note range is 0 - 11
            if (nMonth < m_nMinMonth) {
                m_nMinMonth = nMonth;
            }
            if (nMonth > m_nMaxMonth) {
                m_nMaxMonth = nMonth;
            }
        }
        if (nWidth > m_nMaxWidth) {
            m_nMaxWidth = nWidth;
        }
        
        // 2) populate 2-D array showing number of races in each season/month
        m_anMonthTotals = new int[nSeasons][m_nMaxMonth - m_nMinMonth + 1];
        int nRow = 0;
        strCurrentSeason = "";
        int nCurrentMonth = 0;
        int nCurrentTotal = 0;
        for (int i = 0; i < nRaces; i++) 
        {
            AdditionalRaceInstance race = m_aRaces.get(i);
            m_calendar.setTimeInMillis(race.getDateTime().getTime());
            int nMonth = getMonthNumber();        // note range is 0 - 11
            String strSeason = race.getSeasonString().replace("-", "/");
            //System.out.println(strSeason + "-"  + nMonth);
            if (!strSeason.equals(strCurrentSeason)) 
            {
                if (nCurrentMonth > 0) 
                {
                    //System.out.println("Season: " + nRow + "-"  + (nCurrentMonth - m_nMinMonth) + "=" + nCurrentTotal);
                    m_anMonthTotals[nRow][nCurrentMonth - m_nMinMonth] = nCurrentTotal;
                    nCurrentTotal = 1;
                    nRow++;
                    nCurrentMonth = nMonth;
                } 
                else 
                {
                    nCurrentMonth = nMonth;
                    nCurrentTotal = 1;
                }
                strCurrentSeason = strSeason;
            } 
            else if (nMonth != nCurrentMonth) 
            {
                 //System.out.println("Month: " + nRow + "-"  + (nCurrentMonth - m_nMinMonth) + "=" + nCurrentTotal);
                m_anMonthTotals[nRow][nCurrentMonth - m_nMinMonth] = nCurrentTotal;
                nCurrentTotal = 1;
                nCurrentMonth = nMonth;
            } 
            else 
            {
                nCurrentTotal++;
            }
        }
        //System.out.println("Final: " + nRow + "-"  + (nCurrentMonth - m_nMinMonth) + "=" + nCurrentTotal);
        m_anMonthTotals[nRow][nCurrentMonth - m_nMinMonth] = nCurrentTotal;
        
        // 3) Calculate maximum races for each month
        m_anMonthMax = new int[m_anMonthTotals[0].length];
        for (int i = 0; i < m_anMonthTotals[0].length; i++) 
        {
            m_anMonthMax[i] = 0;
            for (int j = 0; j < m_anMonthTotals.length; j++) 
            {
                if (m_anMonthTotals[j][i] > m_anMonthMax[i]) 
                {
                    m_anMonthMax[i] = m_anMonthTotals[j][i];
                }
            }
        }
    }
private int getMonthNumber()
{
    int nMonth = m_calendar.get(Calendar.MONTH);    // note range is 0 - 11
    if (!m_bFlat)
         nMonth = (nMonth + 5)%12;       // re-index for season August (7) - July (6)

    return nMonth;
}
private int countBlankCells(int nCurrentMonth, int nNextMonth, int nCurrentMonthTotal)
{
    int nCount = 0;
    int nStartMonth = (nCurrentMonth < m_nMinMonth) ? m_nMinMonth : nCurrentMonth + 1;
    
    if ((nCurrentMonth >= m_nMinMonth) && (nCurrentMonthTotal < m_anMonthMax[nCurrentMonth - m_nMinMonth]))
        nCount+= (m_anMonthMax[nCurrentMonth - m_nMinMonth] - nCurrentMonthTotal);
    
    for(int i = nStartMonth; i < nNextMonth; i++)
    {
        nCount += m_anMonthMax[i - m_nMinMonth];
    }
    System.out.println("Blank cell count: " + nCurrentMonth + "-" + nNextMonth + "-" + nCurrentMonthTotal + "->" +nCount);
    return nCount;
}
@Override public String toString()
{
   String strContent = ""; 
    strContent += ("#Seasons=" + m_anMonthTotals.length + "\n");
    strContent += ("Month range: " + m_nMinMonth + " to " + m_nMaxMonth + "\n");
    strContent += ("Max width: " + m_nMaxWidth + "\n");
    for (int i = 0; i < m_anMonthTotals.length; i++) 
    {
        int[] anMonthRow = m_anMonthTotals[i];
        for (int j = 0; j < anMonthRow.length; j++) 
        {
            strContent += anMonthRow[j] + " ";
        }
        strContent += "\n";
    }
    strContent += "\n";
    for (int i = 0; i < m_anMonthMax.length; i++) 
    {
        strContent += (m_anMonthMax[i] + " ");
    }
    strContent += "\n";

    return strContent;
}

    public static String generateRaceWinnerThumbnails(String strRace, String[] astrRunners) 
    {
        String strOutput = "<div class=\"row\">\n";
        for (int i = 0; i < astrRunners.length; i++) {
            String[] astrElements = astrRunners[i].split("\\|");
            String strYear = astrElements[0];
            String strHorse = astrElements[1];
            String strOwner = astrElements[2];
            strOutput += generateThumbnail(strHorse, strOwner, strYear, "", "", true);
        }
        strOutput += "</div>";
        return strOutput;
    }
    public static String generateThumbnail(String strHorse, String strOwner, String strPreCaption1, String strPreCaption2, String strOverlay, boolean bLink) 
    {
        // Bootstrap
        String strOutput = "<div class=\"thumbnail_container col-sm-6 col-md-3 col-lg-2\">\n";
        if ((strPreCaption1 != null) && !"".equals(strPreCaption1) && (strPreCaption2 != null) && !"".equals(strPreCaption2))
        {
            strOutput+= "<div class=\"pre-content_new\">";
            strOutput+= "<div class=\"pre-content_left\"><h2>" + strPreCaption1 + "</h2></div>";
            strOutput+= "<div class=\"pre-content_right\"><h2>" + strPreCaption2 + "</h2></div>";
            strOutput+= "</div>\n";
        }
        else if ((strPreCaption1 != null) && !"".equals(strPreCaption1))
        {
            strOutput+= "<div class=\"pre-content\">";
            strOutput+= "<h2>" + strPreCaption1 + "</h2>";
            strOutput+= "</div>\n";
        }
        if (bLink)
            strOutput += "<a href=\"/horses/" + strHorse.replace(" ", "_") + ".html\" target=\"_blank\" class=\"thumbnail\">";
        strOutput += "<c:choose><c:when test=\"${firefox}\"><img src=\"/images/colours/svg/wikipedia/owners/owner_" + strOwner + ".svg\" /></c:when>\n";
        strOutput += "<c:otherwise><object type=\"image/svg+xml\" data=\"/images/colours/svg/wikipedia/owners/owner_" + strOwner + ".svg\"></object></c:otherwise></c:choose>\n";
        if (!"".equals(strOverlay))
        {
            strOutput += "<div class=\"thumbnail_overlay\">";
            strOutput += "<div class=\"thumbnail_overlay_text\">" +strOverlay + "</div>\n";
            strOutput += "</div>\n";
        }
        if (bLink)
            strOutput+= "</a>\n";
        strOutput+= "<div class=\"post-content\"><h3>" + strHorse + "</h3></div>\n";
        strOutput += "</div>\n";
        
            return strOutput;
    }
    public static String generateRaceWinnerThumbnailsV1(String strRace, String[] astrRunners) 
    {
        String strOutput = "<h1>" + strRace + "</h1>\n";
        for (int i = 0; i < astrRunners.length; i++) {
            String[] astrElements = astrRunners[i].split("\\|");
            String strYear = astrElements[0];
            String strHorse = astrElements[1];
            String strOwner = astrElements[2];
            strOutput += "<div class=\"grid10\">\n";
            strOutput += ("<div class=\"year10\">" + strYear + "</div>\n");
            strOutput += ("<a href=\"/horses/" + strHorse.replace(" ", "_") + ".html\" target=\"_blank\">");
            //strOutput += ("<object type=\"image/svg+xml\" data=\"/images/colours/svg/wikipedia/owners/owner_" + strOwner + ".svg\"></object>");
        strOutput += "<c:choose><c:when test=\"${firefox}\"><img src=\"/images/colours/svg/wikipedia/owners/owner_" + strOwner + ".svg\" /></c:when>\n";
        strOutput += "<c:otherwise><object type=\"image/svg+xml\" data=\"/images/colours/svg/wikipedia/owners/owner_" + strOwner + ".svg\"></object></c:otherwise></c:choose>\n";
        strOutput += "</a>\n";
            strOutput += ("<div class=\"horse10\"><a href=\"/horses/" + strHorse.replace(" ", "_") + ".html\" target=\"_blank\">" + strHorse + "</a></div>\n");
            strOutput += "</div>\n";
            if ((i + 1) % 9 == 0) {
                strOutput += "<div class=\"clear\"></div>";
            }
        }
        strOutput += "<div class=\"clear\"></div>";
        return strOutput;
    }

    public static String generateRaceRunnerThumbnails(String strRace, String[] astrRunners) 
    {
        // forthcoming race
        // name|owner|odds
        String strOutput = "<a name=\"" + strRace + "\"</a>\n";
        strOutput += "<h1>" + strRace + "</h1>\n";
        strOutput += "<div class=\"row\">";
        for (int i = 0; i < astrRunners.length; i++) 
        {
            if ((i > 0) && (i%6 == 0))
                strOutput+="<!-- slightly taller row?? but this is needed to force to fill entire row -->\n<div class=\"clearfix visible-lg-block\"></div>\n";
            String[] astrElements = astrRunners[i].split("\\|");
            String strHorse = astrElements[0];
            String strOwner = astrElements[1];
            String strOdds = astrElements[2];
            strOutput += generateThumbnail(strHorse, strOwner, strOdds, "", "", CareerDBFactory.nameMatch(CareerEnvironment.getInstance().getCareerNames(), strHorse) != null);
        }
        strOutput += "</div>";
        return strOutput;
    }

public static void generateNHSeasonRunnerThumbnailsFile(ENEStatement statement, int nYear, String strRaceType, String strTopic)
{
    // strTopic is empty, novice or juvenile
    // strRaceType is chase or hurdle
    String strContent = generateSeasonRunnerThumbnails(statement, nYear, strRaceType, "", 0, strTopic, false);
    String strFileName ="jump" + nYear + "/index/" + strRaceType.toLowerCase();
    if (strTopic != null && !"".equals(strTopic))
        strFileName += ("_" + strTopic.toLowerCase());
    
    strFileName += ".jsp";
    
    try
    {
        Files.write(Paths.get(HTML_OUTPUT_PATH+"career\\" + strFileName), strContent.getBytes(StandardCharsets.ISO_8859_1));
        
    }
    catch(IOException e)
    {
        System.out.println("Exception generateNHSeasonRunnerThumbnailsFile: " + e.getMessage());
    }
}
public static String generateSeasonRunnerThumbnails(ENEStatement statement, int nYear, String strRaceType, String strCountry, int nAge, String strGender, boolean bJSON)
{
    List<CareerHorse> lstSeasonHorses =CareerDBFactory.getSeasonCareerHorses(statement, nYear, strRaceType, strCountry, nAge, strGender);
    return generateRunnerThumbnails(statement, lstSeasonHorses); 
}

public static String generateMeetingRunnerThumbnails(ENEStatement statement, int nMeeting, boolean bHistoric)
{
   String strOutput="";
    String strWhere = "d.meeting_id = " + nMeeting;
    List<Integer> lstRaces = SmartformRaceFactory.getRaceIds(statement, strWhere, bHistoric, false);
    for(int i = 0; i < lstRaces.size(); i++)
    {
        if (bHistoric)
            strOutput += generateRaceRunnerThumbnails(statement, lstRaces.get(i), "SF", null);
        else
            strOutput += generateDailyRaceRunnerThumbnails(statement, lstRaces.get(i)); // getRaceIds false if current day
    }
    return strOutput;
   
}
public static String generateGroupRaceRunnerThumbnails(ENEStatement statement, String strStartDate, String strRaceType, String strCountry, int nGroupRace, String strTopic)
{
    return generateGroupRaceRunnerThumbnails(statement, strStartDate, strRaceType, strCountry, nGroupRace, strTopic, false);
}
private static String generateGroupRaceRunnerThumbnails(ENEStatement statement, String strStartDate, String strRaceType, String strCountry, int nGroupRace, String strTopic, boolean bJSON)
{
    String strOutput="";
    List<AdditionalRaceInstance> lstRaces = new ArrayList<AdditionalRaceInstance>();
    // commented out to avoid errors
         //   CareerDBFactory.getGroupRaces(statement, strStartDate, strRaceType, strCountry, nGroupRace, strTopic);

        for(int i = 0; i < lstRaces.size(); i++)
        {
            AdditionalRaceInstance ari = lstRaces.get(i);
            String strRaceTitle = CareerEnvironment.getInstance().getCareerRaceName(ari.getSource() + ari.getRaceId());
            if ((strRaceTitle == null) || ("".equals(strRaceTitle)))
                strRaceTitle = CareerDBFactory.convertRaceTitle(AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(statement, ari.getRaceId(), ari.getSource()), 
                        strTopic, strRaceType);
            strOutput += generateRaceRunnerThumbnails(statement, ari.getRaceId(), ari.getSource(), strRaceTitle);
        }
    return strOutput;
}
public static String generateRaceRunnerThumbnails(ENEStatement statement, int nRace, String strSource, String strRaceTitle)
{
    AdditionalRaceInstance arl = AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(statement, nRace, strSource);
    if (strRaceTitle != null)
        arl.setTitle(strRaceTitle);
    return generateRaceRunnerThumbnailsCard(statement, arl, CareerDBFactory.getAllCareerNames(statement));
}

public static String generateRaceRunnerThumbnails(ENEStatement statement, AdditionalRaceInstance race)
{
    return generateRaceRunnerThumbnailsCard(statement, race, CareerDBFactory.getAllCareerNames(statement));
}
   public static String generateDailyRaceRunnerThumbnails(ENEStatement statement, int nRace) 
    {
        /*
        ArrayList<String> aColours = ENEColoursRunnerFactory.createUnparsedJockeyColours(statement, new AdditionalRaceLink("SF", nRace, null));
        Iterator<String> citer = aColours.iterator();
        while(citer.hasNext())
        {
            String strDescription = citer.next();
            ENEColoursRunnerFactory.createJockeyColoursParse(statement, strDescription);
         }
*/
        SmartformRace race = SmartformRaceFactory.createSmartformRace(statement, nRace, "SF");
        return generateDailyRaceRunnerThumbnails(statement, race, CareerDBFactory.getAllCareerNames(statement));
    }
    private static String generateDailyRaceRunnerThumbnails(ENEStatement statement, SmartformRace race, ArrayList<String> aRunners) 
    {
        String strCountry = ENEColoursDBEnvironment.getInstance().getRaceCountry(new AdditionalRaceInstance("SF", race.getRaceId(), race.getDateTime()));
        String strOutput = "<a name=\"" + race.getAbbreviatedTitle() + "\"></a>\n";
        strOutput += "<h2>" + race.getTitle() + " : " + race.getCourse().replace("_" , " ") + ", " + race.getLongFormattedDistance(true).trim() + "</h2>\n";
        strOutput += "<h3>" + race.getFormattedMeetingDate() + ", " + race.getGoing() + ", " + race.getNrRunners() + " Ran</h3>\n";
        strOutput += "<div class=\"row\">\n";
        int nRunnerCount = 0;
        ArrayList<SmartformColoursRunner> hRunners = ENEColoursRunnerFactory.createDailyColoursRunners(statement, race.getRaceId());
        for (int i = 0; i < hRunners.size(); i++) 
        {
            SmartformColoursRunner runner = hRunners.get(i);
            String strName = runner.getName();
            String strJockeyColours = runner.getJockeyColours();
            if (!"".equals(strJockeyColours))
            {
                String strOwner = WikipediaFactory.selectWikipediaOwnerByColours(statement, strJockeyColours, strCountry);
                if (!"".equals(strOwner))
                {
                    if ((nRunnerCount > 0) && (nRunnerCount%6 == 0))
                        strOutput+="<div class=\"clearfix visible-lg-block\"></div>\n";
                    String strFinishPosition = String.valueOf(runner.getStall());    // StringUtils.getOrdinalString(runner.getFinishPosition())
                    String strOverlay = buildThumbnailOverlay(runner, race.isHandicap());
                    strOutput += generateThumbnail(strName, strOwner, strFinishPosition, runner.getShortDistanceBeatenString(), strOverlay, CareerDBFactory.nameMatch(aRunners, strName) != null);
                    nRunnerCount++;
                }
                else
                {
                    //System.out.println("generateDailyRaceRunnerThumbnails colours not found: " + strCountry + " " + race.getTitle() + "(" + race.getRaceId() + ") " + runner.getFinishPositionString() + " " + runner.getJockeyColours() + "(" + strName + "-" + runner.getOwnerName() + ")");
                    System.out.println("generateDailyRaceRunnerThumbnails colours not found: " + runner.getFinishPositionString() + " Wikipedia.generateWikipediaOwner(getStatement(), \"" + runner.getOwnerName() + "\", \"" + runner.getJockeyColours() + "\", \"" + strName + ":" + race.getTitle() + "\", \"en\", true);");
                }
            }
            else
                System.out.println("Missing colours: " + strCountry + " " + race.getTitle() + "(" + race.getRaceId() + ") " + runner.getFinishPositionString() + " ("  + strName+ "-" + runner.getOwnerName() + ")");
        }
        strOutput += "</div>\n";
         return strOutput;
    }

    private static String generateRaceRunnerThumbnailsAnchor(ENEStatement statement, AdditionalRaceInstance race, ArrayList<String> aRunners) 
    {
        String strOutput = "<a name=\"" + race.getAbbreviatedTitle() + "\"></a>\n";
         if (race.getGroupRace() > 0)
            strOutput += "<div class=\"group" + race.getGroupRace() + "_panel\">\n";
        else
            strOutput += "<div class=\"ungraded_panel\">\n";
            
        strOutput += "<h2>" + race.getTitle() + " : " + race.getCourse().replace("_" , " ") + ", " + race.getLongFormattedDistance(true).trim() + "</h2>\n";
        strOutput += "<h3>" + race.getFormattedMeetingDate() + ", " + race.getGoing() + ", " + race.getNrRunners() + " Ran</h3>\n";
        strOutput+= "</div>\n";
        strOutput = generateRaceRunnerThumbnails(statement, strOutput, race, aRunners);
        return strOutput;
    }
    private static String generateRaceRunnerThumbnailsCard(ENEStatement statement, AdditionalRaceInstance race, ArrayList<String> aRunners) 
    {
        String strOutput = "<div class=\"card\">\n";
        String strRaceTitle = race.getTitle();
        String strCourse = race.getCourse().replace("_" , " ");
        String strDistance = race.getLongFormattedDistance(true).trim();
        String strDate = race.getFormattedMeetingDate("MMMM d yyyy");
        int nRace = race.getRaceId();
        String strPanel = "ungraded_panel";
         if (race.getGroupRace() > 0)
            strPanel = "group" + race.getGroupRace() + "_panel";
        strOutput += "<div class=\"card-header " + strPanel + "\" role=\"tab\" id=\"heading" + nRace + "\">\n";
        strOutput += "<h5 class=\"mb-0\">\n";
        strOutput += "<a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse" + nRace + "\" aria-expanded=\"true\" aria-controls=\"collapse" + nRace + "\">\n";
        String strFullRaceTitle = strRaceTitle + " : " + strCourse + ", " + strDistance + ", " + strDate + ", " + race.getGoing() + ", " + race.getNrRunners() + " Ran\n";
        strOutput += strFullRaceTitle;
        strOutput += "<span class=\"float-right\">${WINNER}</span>\n";
        strOutput += "</a>\n";
        strOutput += "</h5>\n";
        strOutput += "</div>\n";
        strOutput += "<div id=\"collapse" + nRace + "\" class=\"collapse\" role=\"tabpanel\" aria-labelledby=\"heading" + nRace + "\">\n";
        strOutput = generateRaceRunnerThumbnails(statement, strOutput, race, aRunners);
        strOutput += "</div>\n";
        strOutput += "</div>\n";

        return strOutput;
    }
    private static String generateRaceRunnerThumbnails(ENEStatement statement, String strOutput, AdditionalRaceInstance race, ArrayList<String> aRunners) 
    {
         String strCountry = ENEColoursDBEnvironment.getInstance().getRaceCountry(race);
       // pass existing output in, so can replace ${WINNER}n tag with the winner's name
        strOutput += "<div class=\"row\">\n";
        //ArrayList<SmartformHistoricRunner> hRunners = SmartformRunnerFactory.createRaceRunners(statement, race.getRaceId(), false);
        ArrayList<SmartformColoursRunner> hRunners = ENEColoursRunnerFactory.getSmartformRaceRunners(statement, race, -1);
        int nRunnerCount = 0;
        for (int i = 0; i < hRunners.size(); i++) 
        {
            SmartformColoursRunner runner = hRunners.get(i);
            if (i == 0)
                strOutput = strOutput.replace("${WINNER}", runner.getName());
            
            String strName = runner.getName();
            String strJockeyColours = runner.getJockeyColours();
            if (!"".equals(strJockeyColours))
            {
                String strOwner = WikipediaFactory.selectWikipediaOwnerByColours(statement, strJockeyColours, strCountry);
                if (!"".equals(strOwner))
                {
                    if ((nRunnerCount > 0) && (nRunnerCount%6 == 0))
                        strOutput+="<div class=\"clearfix visible-lg-block\"></div>\n";
                    String strFinishPosition = runner.getShortFinishPositionString();    // StringUtils.getOrdinalString(runner.getFinishPosition())
                    String strOverlay = buildThumbnailOverlay(runner, race.isHandicap());
                    strOutput += generateThumbnail(strName, strOwner, strFinishPosition, runner.getShortDistanceBeatenString(), strOverlay, CareerDBFactory.nameMatch(aRunners, strName) != null);
                    nRunnerCount++;
                }
                else
                {
                    //System.out.println("generateRaceRunnerThumbnails colours not found: " + strCountry + " " + race.getTitle() + "(" + race.getRaceId() + ") " +runner.getFinishPositionString() + " " + runner.getJockeyColours() + "(" + strName + "-" + runner.getOwnerName() + ")");
                    //if (runner.getFinishPosition() > 0) // don't care about runners that didn't finish
                    System.out.println("generateRaceRunnerThumbnails colours not found: Wikipedia.generateWikipediaOwner(getStatement(), \"" + runner.getOwnerName() + "\", \"" + runner.getJockeyColours() + "\", \"" + strName + ":" + race.getTitle() + " " + race.getYearString() + "-" + runner.getFinishPositionString() + "\", \"en\", true);");
                }
            }
            else
                System.out.println("Missing colours: " + strCountry + " " + race.getTitle() + "(" + race.getRaceId() + ") " +runner.getFinishPositionString() + "(" + strName+ "-" + runner.getOwnerName() + ")");
        }
        strOutput += "</div>\n";
         return strOutput;
    }
    private static String buildThumbnailCareerOverlay(CareerHorse runner)
    {
        String strOverlay = "<div class=\"trainer\">" + runner.getTrainerName() + "</div>";
        if (!"".equals(runner.getOwnerName()))
            strOverlay += ("<div class=\"owner\">" + runner.getOwnerName() + "</div>");
        if (!"".equals(runner.getSireName()))
            strOverlay += ("<div class=\"sire\">" + runner.getSireName() + "</div>");
        if (!"".equals(runner.getDamName()))
        {
            String strDam = runner.getDamName();
            if (!"".equals(runner.getDamSireName()))
                strDam += ("(" + runner.getDamSireName() + ")");
            strOverlay += ("<div class=\"dam\">" + strDam + "</div>");
        }
        List<Pair<String,String>> lstRaces = runner.getMajorRaces();
        if (lstRaces.size() > 0)
        {
            strOverlay += "<div class=\"win_heading\">Major Races</div>";
            Iterator<Pair<String,String>> iter = runner.getMajorRaces().iterator();
            while(iter.hasNext())
            {
                Pair<String,String> pair = iter.next();
                String strRaceName = pair.getElement0();
                String strPosition = pair.getElement1();
                try
                {
                    int nPosition = Integer.parseInt(strPosition);
                    strOverlay += ("<div class=\"win\">" +StringUtils.getOrdinalString(nPosition) + " " + strRaceName + "</div>");

                }
                catch(NumberFormatException e)
                {
                    strOverlay += ("<div class=\"win\">" + strPosition + " " + strRaceName + "</div>");
                }
            }
        }
        return strOverlay;
    }
    private static String buildThumbnailOverlay(SmartformColoursRunner runner, boolean bHandicap)
    {
        String strOverlay = "<div class=\"trainer\">" + runner.getTrainerName() + "</div>";
        strOverlay += ("<div class=\"jockey\">" + runner.getJockeyName() + "</div>");
        strOverlay += ("<div class=\"sp\">" + runner.getFullStartingPrice() + "</div>");
        strOverlay += ("<div class=\"weight\">" + SmartformRunner.getWeightString(runner.getWeightPounds()) + "</div>");
        if (bHandicap && runner.getOfficialRating() > 0)
            strOverlay += ("<div class=\"rating\">"  + runner.getOfficialRating()+ "</div>");
        if (runner.getClothNumber() > 0)
            strOverlay += ("<div class=\"cloth_nr\">"  + runner.getClothNumber()+ "</div>");
        if (runner.getStall() > 0)
            strOverlay += ("<div class=\"stall\">"  + runner.getStall() + "</div>");
        if (runner.getTack().hasTack())
            strOverlay += ("<div class=\"tack\">"  + runner.getTack().getTackString() + "</div>");
        if (!"".equals(runner.getOwnerName()))
            strOverlay += ("<div class=\"owner\">" + runner.getOwnerName() + "</div>");
        if (!"".equals(runner.getJockeyColours()))
            strOverlay += ("<div class=\"colours\">" + runner.getJockeyColours()+ "</div>");
        if (!"".equals(runner.getInRaceComment()))
            strOverlay += ("<div class=\"in_race_comment\">"  + runner.getInRaceComment() + "</div>");
        
        return strOverlay;
    }
    private static String generateRunnerThumbnails(ENEStatement statement, List<CareerHorse> aRunners) 
    {
        // new version, where wi_owner has already been retrieved from career_horses table
       String strOutput = "<div class=\"row\">\n";
        for (int i = 0; i < aRunners.size(); i++) 
        {
            if ((i > 0) && (i%6 == 0))
                strOutput+="<!-- slightly taller row?? but this is needed to force to fill entire row -->\n<div class=\"clearfix visible-lg-block\"></div>\n";
            String strName = aRunners.get(i).getHorseName();
            String strOwner = aRunners.get(i).getWiOwner();
            String strOverlay = buildThumbnailCareerOverlay(aRunners.get(i));
            strOutput += generateThumbnail(strName, strOwner, null, "", strOverlay, true);
        }
        strOutput += "</div>\n";
         return strOutput;
    }
    
public static String generateRaceRunnerWikipedia(ENEStatement statement, int nRace, String strSource, String strTitle)
{
    AdditionalRaceInstance arl = AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(statement, nRace, strSource);
    String strRaceTitle = CareerEnvironment.getInstance().getCareerRaceName(strSource + arl.getRaceId());
    if (strRaceTitle != null)
        arl.setTitle(strRaceTitle);
    return generateRaceRunnerWikipedia(statement, arl, CareerDBFactory.getAllCareerNames(statement), strTitle);
}
    public static String generateRaceRunnerWikipedia(ENEStatement statement, AdditionalRaceInstance race, ArrayList<String> aRunners, String strTitle) 
    {
        ArrayList<SmartformColoursRunner> hAllRunners = ENEColoursRunnerFactory.getSmartformRaceRunners(statement, race, -1);
        Iterator<SmartformColoursRunner> iter = hAllRunners.iterator();
        ArrayList<SmartformColoursRunner> alThreeRunners = new ArrayList<SmartformColoursRunner>();
        String strHeader="{{Jockey colours header\n| name = " + strTitle + "}}\n";
        String strFooter = "{{Jockey colours footer}}\n";
        String strAlsoRan="|}\n{| class=\"collapsible collapsed\" cellpadding=\"0\" cellspacing=\"0\" style=\"clear:right; float:right; text-align:center; font-weight:bold;\" width=\"280px\"\n! colspan=\"3\" style=\"border:1px solid black; background-color: #77DD77;\" | Also Ran\n";
        int nCount = -2;
        String strOutput = strHeader;
        while(iter.hasNext())
        {
            if (alThreeRunners.size() < 3)
                alThreeRunners.add(iter.next());
            
            if (alThreeRunners.size() == 3)
            {
                nCount += 3;
                if (nCount == 4)
                    strOutput+= strAlsoRan;
                String strTitle1 = nCount + "-" + (nCount+1 + "-" + (nCount + 2));
                // For now: remove wikipedia
                // strOutput += wikipediaService.generateRaceRunnerTriplet(statement, alThreeRunners, strTitle1, "\n");
                alThreeRunners.clear();
            }
        }
        if (alThreeRunners.size() > 0)
        {
            // For now: remove wikipedia
            // strOutput += Wikipedia.generateRaceRunnerTriplet(statement, alThreeRunners, "", "\n");
        }
        
        strOutput+= strFooter;
        return strOutput;
    }
    public static void generateFlatYearPages(ENEStatement statement, int nYear)
    {
        // Topic may be Classic, Sprint, Stayer, Twoyear, Middle, Mile
/*        generateFlatCareerFile(statement, nYear, "UK", 1, "Classics");
        generateFlatCareerFile(statement, nYear, "UK", 1, "Sprint");
        generateFlatCareerFile(statement, nYear, "UK", 1, "Stayer");
        generateFlatCareerFile(statement, nYear, "UK", 1, "Twoyear");
        generateFlatCareerFile(statement, nYear, "UK", 1, "Middle");
        generateFlatCareerFile(statement, nYear, "UK", 1, "Mile");
        generateFlatCareerFile(statement, nYear, "France", 1, "");
        generateFlatCareerFile(statement, nYear, "Eire", 1, "");
*/
        generateFlatCareerSide(statement, nYear);
        generateFlatCareerIndex(statement, nYear);
    }
    public static void generateFlatCareerIndex(ENEStatement statement, int nYear)
    {
        try
        {
            InputStream is = new FileInputStream(HTML_OUTPUT_PATH + "career\\templates\\flat_index.jsp"); 
            BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
            String line = buf.readLine(); 
            StringBuilder sb = new StringBuilder(); 
            while(line != null)
            { 
                sb.append(line).append("\n"); 
                line = buf.readLine(); 
            } 
            String strFileContent = sb.toString(); 
            strFileContent = strFileContent.replace("{YEAR}", String.valueOf(nYear));
            strFileContent = strFileContent.replace("{CONTENT_2_Y_OC}", CareerHTMLFactory.generateSeasonRunnerThumbnails(statement, nYear, "Flat", "", 2, "CGH", false));
            strFileContent = strFileContent.replace("{CONTENT_2_Y_OF}", CareerHTMLFactory.generateSeasonRunnerThumbnails(statement, nYear, "Flat", "", 2, "FM", false));
            strFileContent = strFileContent.replace("{CONTENT_3_Y_OC}", CareerHTMLFactory.generateSeasonRunnerThumbnails(statement, nYear, "Flat", "", 3, "CGH", false));
            strFileContent = strFileContent.replace("{CONTENT_3_Y_OF}", CareerHTMLFactory.generateSeasonRunnerThumbnails(statement, nYear, "Flat", "", 3, "FM", false));
            strFileContent = strFileContent.replace("{CONTENT_4_Y_OC}", CareerHTMLFactory.generateSeasonRunnerThumbnails(statement, nYear, "Flat", "", 4, "CGH", false));
            strFileContent = strFileContent.replace("{CONTENT_4_Y_OF}", CareerHTMLFactory.generateSeasonRunnerThumbnails(statement, nYear, "Flat", "", 4, "FM", false));
            
            Files.write(Paths.get(HTML_OUTPUT_PATH + "career\\flat" + nYear + "\\index.jsp"), strFileContent.getBytes(StandardCharsets.ISO_8859_1));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException: " + e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("IOException: " + e.getMessage());
        }
    }    
    public static void generateFlatCareerSide(ENEStatement statement, int nYear)
    {
        try
        {
            InputStream is = new FileInputStream(HTML_OUTPUT_PATH + "career\\templates\\flat_side.jsp"); 
            BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
            String line = buf.readLine(); 
            StringBuilder sb = new StringBuilder(); 
            while(line != null)
            { 
                sb.append(line).append("\n"); 
                line = buf.readLine(); 
            } 
            String strFileContent = sb.toString(); 
            strFileContent = strFileContent.replace("{YEAR}", String.valueOf(nYear));
            
            Files.write(Paths.get(HTML_OUTPUT_PATH + "career\\flat" + nYear + "\\side.jsp"), strFileContent.getBytes(StandardCharsets.ISO_8859_1));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException: " + e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("IOException: " + e.getMessage());
        }
    }    
    public static void generateNHCareerFile(ENEStatement statement, int nYear, String strRaceType, String strTopic, String strTitle)
    {
        // not worth incorporating France into UK/Ireland Jumps season
        // strRaceType is Hurdle or Chase
        // nYear is start year (year in which season starts - Authumn)
        try
        {
            InputStream is = new FileInputStream(HTML_OUTPUT_PATH + "career\\templates\\nh_races.jsp"); 
            BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
            String line = buf.readLine(); 
            StringBuilder sb = new StringBuilder(); 
            while(line != null)
            { 
                sb.append(line).append("\n"); 
                line = buf.readLine(); 
            } 
            String strFileContent = sb.toString(); 
            strFileContent = strFileContent.replace("{CONTENT}", CareerHTMLFactory.generateGroupRaceRunnerThumbnails(statement, nYear + "-08-01", strRaceType, "", 0, strTopic, false));
            int nEndYear = nYear%100 + 1;
            strFileContent = strFileContent.replace("{YEAR}", String.valueOf(nYear) + "/" + nEndYear);
            strFileContent = strFileContent.replace("{RACE_TYPE}", strRaceType);
            strFileContent = strFileContent.replace("{TITLE}", strTitle);
            
            String strFileName = strTopic.toLowerCase() + ("france".equals(strTopic) ? "" : ("_" + strRaceType.toLowerCase()));
            Files.write(Paths.get(HTML_OUTPUT_PATH + "career\\jump" + (nYear + 1) + "\\" + strFileName + ".jsp"), strFileContent.getBytes(StandardCharsets.ISO_8859_1));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException: " + e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("IOException: " + e.getMessage());
        }
    }
    public static void generateFlatCareerFile(ENEStatement statement, int nYear, String strTopic, String strSubDir, String strTitle)
    {
        // extend to include UK, Ireland and France in single page classified by Topic
        try
        {
            InputStream is = new FileInputStream(HTML_OUTPUT_PATH + "career\\templates\\flat_races.jsp"); 
            BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
            String line = buf.readLine(); 
            StringBuilder sb = new StringBuilder(); 
            while(line != null)
            { 
                sb.append(line).append("\n"); 
                line = buf.readLine(); 
            } 
            String strContent = CareerHTMLFactory.generateGroupRaceRunnerThumbnails(statement, nYear + "-03-01", "Flat", "", 0, strTopic, false);
            String strFileName = strTopic.toLowerCase();
            if ("".equals(strSubDir))
            {
                // standalone page
                String strFileContent = sb.toString(); 
                strFileContent = strFileContent.replace("{CONTENT}", strContent);
                strFileContent = strFileContent.replace("{YEAR}", String.valueOf(nYear));
                strFileContent = strFileContent.replace("{TITLE}", strTitle);
            
                Files.write(Paths.get(HTML_OUTPUT_PATH + "career\\flat" + nYear + "\\" + strFileName + ".jsp"), strFileContent.getBytes(StandardCharsets.ISO_8859_1));
            }
            else
            {
                // subdir means that multiple embedded in single page
                // accordion values must be suffixed with the topic name
                
                strContent = strContent.replaceAll("data-parent=\"#accordion\"", "data-parent=\"#accordion" + strTopic + "\"");
                Files.write(Paths.get(HTML_OUTPUT_PATH + "career\\flat" + nYear + "\\" + strSubDir + "\\" + strFileName + ".jsp"), strContent.getBytes(StandardCharsets.ISO_8859_1));
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException: " + e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
