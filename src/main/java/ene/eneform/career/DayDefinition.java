/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.smartform.factory.SmartformRaceFactory;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.Pair;

import java.util.List;

/**
 *
 * @author Simon
 */
public class DayDefinition extends CareerDefinition {
    
    private String m_strData = "";   // meeting data

    public DayDefinition(int nMeeting, String strName)
    {
        super(String.valueOf(nMeeting), strName, "day");
        m_strData = String.valueOf(nMeeting);
        
        // position="no" display_row_titles="yes" display_column_titles="no"
        suppressColumnTitles();
        suppressRowTitles();
    }

    @Override public void expand(ENEStatement statement)
    {
            String strData = getData();    // meeting_id|title
            int nMeeting = Integer.parseInt(strData);
            List<Pair<String,Integer>> alRaces = SmartformRaceFactory.getMeetingRaceIds(statement, nMeeting);
            m_strName = m_strName + "(" + SmartformRaceFactory.getMeetingTitle(statement, nMeeting) + ")";
            CareerTable table = new CareerTable(1);
            // always two rows 3+3, 4+3, 4+4
            int nFirstRow = (int) Math.ceil(alRaces.size()/2.0d);
            int nRows = 0;
            int nCols = 1;
            CareerRow currentRow = new CareerRow(1, "", 1);
            
            for(int i = 0; i < alRaces.size(); i++)
            {
                if (i == nFirstRow)
                {
                    table.addRow(currentRow);
                    currentRow = new CareerRow(2, "", 1);
                    nCols = 1;
                }
                CareerRow.CareerCell cell = currentRow.new CareerCell(nCols, nCols, 1);
                int nRace = alRaces.get(i).getElement1();
                String strKey = "SF" + nRace;
                String strRaceName = CareerEnvironment.getInstance().getCareerRaceName(strKey);
                CareerRow.CareerCell.CareerRace crace = cell.new CareerRace("SF", strRaceName);
                crace.setContent(String.valueOf(nRace));
                cell.addRace(crace);
                currentRow.addCell(cell);
                nCols++;
            }
            table.addRow(currentRow);
            for(int i = 1;i <= nFirstRow; i++)
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
    @Override public String getFileName(){return m_strId;}
 }
