/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.smartform.factory.SmartformRaceFactory;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.Pair;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Simon
 */
public class MeetingDefinition extends CareerDefinition {
    
    private String m_strData = "";   // meeting data

    public MeetingDefinition(String strName, String strData)
    {
        super(strName, strName, "meeting");
        m_strData = strData;
        
        // position="no" display_row_titles="yes" display_column_titles="no"
        suppressColumnTitles();
    }

    @Override public void expand(ENEStatement statement)
    {
            String strData = getData();    // course|month|year
            String[] astrData = strData.split("\\|");
            String strCourse = astrData[0];
            int nMonth = Integer.parseInt(astrData[1]);
            int nYear = Integer.parseInt(astrData[2]);
            List<Pair<String,Integer>> alRaces = SmartformRaceFactory.getMeetingRaceIds(statement, strCourse, nMonth, nYear);
            CareerTable table = new CareerTable(1);
            Iterator<Pair<String,Integer>> iter = alRaces.iterator();
            String strPreviousDate = null;
            CareerRow currentRow = null;
            int nRows = 0;
            int nCols = 1;
            int nCells=0;
            int nMaxColCount = 0;
            int nColCount = 0;
            while (iter.hasNext())
            {
                Pair<String,Integer> race = iter.next();
                String strDate = race.getElement0();
                if ((strPreviousDate == null) || (!strPreviousDate.equals(strDate)))
                {
                    if (nColCount > nMaxColCount)
                        nMaxColCount = nColCount;
                    nColCount=0;
                    nRows++;
                    nCols = 1;
                    strPreviousDate = strDate;
                    if (currentRow != null)
                        table.addRow(currentRow);
                    currentRow = new CareerRow(nRows, strDate, 1);
                }
                nColCount++;
                nCells++;
                CareerRow.CareerCell cell = currentRow.new CareerCell(nCells, nCols, 1);
                int nRace = race.getElement1();
                String strKey = "SF" + nRace;
                String strRaceName = CareerEnvironment.getInstance().getCareerRaceName(strKey);
                CareerRow.CareerCell.CareerRace crace = cell.new CareerRace("SF", strRaceName);
                crace.setContent(String.valueOf(nRace));
                cell.addRace(crace);
                currentRow.addCell(cell);
                nCols++;
            }
            if (nColCount > nMaxColCount)
                 nMaxColCount = nColCount;
            table.addRow(currentRow);
            for(int i = 1;i <= nMaxColCount; i++)
            {
                CareerColumn column = new CareerColumn(i, 1);
                column.setContent(String.valueOf(i));
                column.setMeroWidth(3);
                table.addColumn(column);
            }
            addTable(table);
        
    }
   public String getData() {
        return m_strData;
    }

    public void setData(String strData) {
        m_strData = strData;
    }
    @Override public boolean isMeeting(){return true;}
    @Override public String getFileName(){return getName().replaceAll(" ", "_");}   // HTML needs _, SVG does not

 }
