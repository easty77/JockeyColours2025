/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.smartform.bos.SmartformEnvironment;
import ene.eneform.utils.ENEStatement;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Simon
 */
public abstract class CareerDefinition
{

    protected String m_strId;     // unique identifier, could be multiple designs for same horse
    protected String m_strName;   // horse's name
    protected String m_strType = "";   // standard, meeting
    protected List<CareerTable> m_tables = new ArrayList<CareerTable>();

    protected String m_strFormat="1-2-3";
    protected boolean m_bShowPosition=false;
    protected boolean m_bDisplayRowTitle = true;
    protected boolean m_bDisplayColumnTitle = true;
    protected boolean m_bHorizontalRowTitle = false;

    private String m_strOwner = "";   // horse's owner
    private String m_strColours = "";   // horse's colours
    private Pedigree m_pedigree = null;
    private Calendar m_calFoalingDate;

    public abstract void expand(ENEStatement statement);
    public abstract boolean isMeeting();
    public abstract String getFileName();
    
    public int getNrTables()
    {
        return m_tables.size();
    }
    public Iterator<CareerTable> getTableIterator()
    {
        return m_tables.iterator();
    }
    public int getTableCount()
    {
        return m_tables.size();
    }
    public int getTotalRows(int nFullHeight)
    {
        // each row counts for 2, as can have 1.5 height rows, when 2 consecutive double rows
        Iterator<CareerTable> iter = getTableIterator();
        int nTotalRows = 0;      // each row counts for 2, as can have 1.5 height rows, when 2 consecutive double rows
        while(iter.hasNext())
        {
            CareerTable table = iter.next();
            Iterator<CareerRow> rowIter = table.getRowIterator();
            CareerRow previousRow = null;
            CareerRow row = null;
            int nSeasons = table.getNrRows();
            int nPixelsRowHeight = 0;
            while(rowIter.hasNext())
            {
                previousRow = row;
                row = rowIter.next();
                int nRowHeight = row.getHeight();
                if ((nSeasons > 3) && (nRowHeight == 2) && (nPixelsRowHeight == nFullHeight * nRowHeight) && (previousRow != null && !cellClash(previousRow, row)))
                {
                    nTotalRows -= 1;
                }
                nPixelsRowHeight = nFullHeight * nRowHeight;
                nTotalRows+=(2 * nRowHeight);
            }
        }
        return nTotalRows;
    }
    public CareerDefinition(String strId, String strName, String strType)
    {
        m_strId = strId;
        m_strName = strName;
        m_strType = strType;
    }
    public String getId()
    {
        return m_strId;
    }
    public String getName()
    {
        return m_strName;
    }
    public String getOwner()
    {
        return m_strOwner;
    }
    public String getColours()
    {
        return m_strColours;
    }
    public String getFormat()
    {
        return m_strFormat;
    }
    public Pedigree getPedigree()
    {
        return m_pedigree;
    }
    public boolean getShowPosition()
    {
        return m_bShowPosition;
    }
    public void setShowPosition()
    {
        m_bShowPosition = true;
    }
    public void setPedigree(Pedigree pedigree)
    {
        m_pedigree = pedigree;
    }
    public void setFormat(String strFormat)
    {
        m_strFormat = strFormat;
    }
    public void setOwner(String strOwner)
    {
        m_strOwner = strOwner;
    }
    public String getType() {
        return m_strType;
    }

    public void setColours(String strColours)
    {
        m_strColours = strColours;
    }
    public void setFoalingDate(Calendar calFoalingDate) {
        this.m_calFoalingDate = calFoalingDate;
    }
    public Calendar getFoalingDate() {
        return m_calFoalingDate;
    }
    public String getFormattedFoalingDate()
    {
       return getFormattedFoalingDate(SmartformEnvironment.getInstance().getShortDateFormat());
    }
    public String getFormattedFoalingDate(DateFormat fmtDate)
    {
       if(m_calFoalingDate != null)
            return fmtDate.format(m_calFoalingDate.getTime());
        else
            return "";
    }
    public void addTable(CareerTable table)
    {
        m_tables.add(table);
    }
    public void suppressColumnTitles()
    {
        m_bDisplayColumnTitle = false;
    }
    public void suppressRowTitles()
    {
        m_bDisplayRowTitle = false;
    }
    public void horizontalRowTitles()
    {
        m_bHorizontalRowTitle = true;
    }
    public boolean hasColumnTitles()
    {
        return m_bDisplayColumnTitle;
    }
    public boolean hasRowTitles()
    {
        return m_bDisplayRowTitle;
    }
    public boolean hasHorizontalRowTitles()
    {
        return m_bHorizontalRowTitle;
    }
       public int getWidth()
        {
            int nTotalWidth = 0;
            Iterator<CareerTable> iter = m_tables.iterator();
            while(iter.hasNext())
            {
                int nTableWidth = iter.next().getWidth();
                if (nTableWidth > nTotalWidth)
                    nTotalWidth = nTableWidth;
            }
            
            return nTotalWidth; 
        }
       public int getHeight()
        {
            int nTotalHeight = 0;
            Iterator<CareerTable> iter = m_tables.iterator();
            while(iter.hasNext())
            {
                int nTableHeight = iter.next().getHeight();
                nTotalHeight += nTableHeight;
            }
            
            return nTotalHeight; 
        }
    public String analyse()
    {
        String strContent = "";
        strContent += "Career: " + getName() + "\n";
        strContent += "#Tables: " + getNrTables() + "\n";
        Iterator<CareerTable> iter = getTableIterator();
        while(iter.hasNext())
        {
            strContent += iter.next().analyse();
        }
        
        return strContent;
    }
    public class CareerTable
    {
        private ArrayList<Integer> m_alColumnWidths = new ArrayList<Integer>();
        private ArrayList<CareerColumn> m_alColumns = new ArrayList<CareerColumn>();
        private ArrayList<CareerRow> m_alRows = new ArrayList<CareerRow>();
        private int m_nColumns = 0;
        private int m_nTableId;
        
        
        public CareerTable(int nTableId)
        {
            m_nTableId = nTableId;
        }
        public int getId()
        {
            return m_nTableId;
        }
    public String analyse()
    {
        String strContent = "";
        strContent += "Table: " + getId() + "\n";
        strContent += "#Columns: " + getNrColumns() + "\n";
        Iterator<CareerColumn> citer = getColumnIterator();
        while(citer.hasNext())
        {
            strContent += citer.next().analyse();
        }
        strContent += "#Rows: " + getNrRows() + "\n";
        Iterator<CareerRow> riter = getRowIterator();
        while(riter.hasNext())
        {
            strContent += riter.next().analyse();
        }
        
        return strContent;
    }
        public Iterator<CareerRow> getRowIterator()
        {
            return m_alRows.iterator();
        }

        public void addColumn(CareerColumn column)
        {
            int nOffset = 0;
            Iterator<CareerColumn> iter = m_alColumns.iterator();
            while(iter.hasNext())
            {
                nOffset += iter.next().getCellWidth();
            }
            column.setOffset(nOffset);
            m_alColumns.add(column);
            m_nColumns += column.getColSpan();
            for(int i = 0; i < column.getColSpan(); i++)
            {
                m_alColumnWidths.add(column.getMeroWidth());
            }
        }
        public void addRow(CareerRow row)
        {
            m_alRows.add(row);
        }
        public Iterator<CareerColumn> getColumnIterator()
        {
            return m_alColumns.iterator();
        }
        public int getNrColumns()
        {
            return m_nColumns;
        }
        public int getNrRows()
        {
            return m_alRows.size();
        }
        public CareerColumn getColumn(int nColumn)
        {
            return m_alColumns.get(nColumn - 1); 
        }
        public CareerColumn getColumnByPosition(int nPosition)
        {
            int nCurrentColumn = 0;
            for(int i = 0; i < m_alColumns.size(); i++)
            {
                CareerColumn column = m_alColumns.get(i);
                if (nCurrentColumn + column.getColSpan() >= nPosition)
                    return m_alColumns.get(i);
                nCurrentColumn+=column.getColSpan();
            }
            
            return null;
        }
        public int getCellWidth(int nColumn, int nColSpan, boolean bZeroColumn)
        {
            int nTotalWidth = 0;
            if (nColSpan == 0)
            {
                nTotalWidth = getSingleCellWidth(m_alColumnWidths.get(nColumn - 1))/2;
            }
            else if (bZeroColumn)
            {
                nTotalWidth = (3 * CareerSVGFactory.SVG_MERO_123_WIDTH) + CareerSVGFactory.SVG_MERO_123_WIDTH_EXTRA;
            }
             else
            {
                for(int j = nColumn - 1; j < nColumn + nColSpan - 1; j++)
                {
                    if (j < m_alColumnWidths.size())
                        nTotalWidth += getSingleCellWidth(m_alColumnWidths.get(j));
                }
            }
            return nTotalWidth;
        }
        private int getSingleCellWidth(int nMeroWidth)
        {
                return (CareerSVGFactory.SVG_MERO_123_WIDTH * nMeroWidth + CareerSVGFactory.SVG_MERO_123_WIDTH_EXTRA);                
        }
        public int getWidth()
        {
            int nTotalWidth = 0;
            Iterator<CareerColumn> iter = m_alColumns.iterator();
            while(iter.hasNext())
            {
                nTotalWidth += iter.next().getCellWidth();
            }
            nTotalWidth -= CareerSVGFactory.SVG_MERO_123_WIDTH_EXTRA;    // no need for space after last cell
            
            return nTotalWidth; 
        }
        public int getHeight()
        {
            int nTotalHeight = 0;
            Iterator<CareerRow> iter = m_alRows.iterator();
            while(iter.hasNext())
            {
                nTotalHeight += iter.next().getHeight();
            }
            return nTotalHeight; 
        }
       public ArrayList<Integer> getRowSeparators(int nRowHeight)
        {
            ArrayList<Integer> aRowSeparators = new ArrayList<Integer>();
            aRowSeparators.add(-10);  // top line
            Iterator<CareerRow> iter = m_alRows.iterator();
            int nTotalHeight = 0;
            while(iter.hasNext())
            {
                CareerRow row = iter.next();
               nTotalHeight += row.getHeight();
               aRowSeparators.add(nTotalHeight * nRowHeight - 10);  // bit of blank space at bottom so raise line a bit
            }
            
            return aRowSeparators; 
        }
       public ArrayList<Integer> getColumnSeparators(int nOriginOffset)
        {
            ArrayList<Integer> aColumnSeparators = new ArrayList<Integer>();
            aColumnSeparators.add(nOriginOffset);
            Iterator<CareerColumn> iter = m_alColumns.iterator();
            int nTotalWidth = 0;
            while(iter.hasNext())
            {
                CareerColumn column = iter.next();
               nTotalWidth += column.getCellWidth();
               aColumnSeparators.add(nTotalWidth + nOriginOffset);
            }
            
            return aColumnSeparators; 
        }
    }
    public class CareerRow
    {
        private String m_strTitle = "";
        private ArrayList<CareerCell> m_alCells = new ArrayList<CareerCell>();
        private int m_nRowId;
        private String m_strName= null;
        private int m_nHeight; 
        
        
        public CareerRow(int nRowId, String strTitle, int nHeight)
        {
            m_nRowId = nRowId;
            m_strTitle = strTitle;
            m_nHeight = nHeight;
        }
        public int getId()
        {
            return m_nRowId;
        }
        public int getCellCount()
        {
            return m_alCells.size();
        }
        public String getName()
        {
            return m_strName;
        }
        public void setName(String strName)
        {
            m_strName = strName;
        }
        public String getTitle()
        {
            return m_strTitle;
        }
        public String getShortTitle()
        {
            // remove subtitle
            int nIndex = m_strTitle.indexOf("|");
            if (nIndex > 0)
                return m_strTitle.substring(0, nIndex);
            else
                return m_strTitle;
        }
        public void addCell(CareerCell cell)
        {
            m_alCells.add(cell);
        }
        public CareerCell getCell(int nCell)
        {
            // cells have different widths
            if ((nCell > 0) && (nCell <= m_alCells.size()))
                return m_alCells.get(nCell - 1);
            
            return null;
        }
        public Iterator<CareerCell> getCellIterator()
        {
            return m_alCells.iterator();
        }
        public int getHeight()
        {
            return m_nHeight;
        }
   public String analyse()
    {
        String strContent = "";
        strContent += "Row Id: " + getId() + "\n";
        Iterator<CareerCell> iter = getCellIterator();
        while(iter.hasNext())
        {
            strContent += iter.next().analyse();
        }
        
        return strContent;
    }
         public class CareerCell
        {
            private int m_nColSpan;
            private int m_nRowSpan=1;
            private String m_strCell = "";
            private int m_nRowId;
            private int m_nColumnId;
            private int m_nFontSize = 0;
            private String m_strFontStyle = null;
            private String m_strFontName = null;
            private String m_strAlign = null;
            private String m_strJockeyColours="";
            private ArrayList<CareerRace> m_alRaces = new ArrayList<CareerRace>();
            
            public CareerCell(int nRowId, int nColumnId, int nColSpan, String strSource, String strTitle)
            {
                m_nRowId = nRowId;
                m_nColumnId = nColumnId;
                m_nColSpan = nColSpan;
            }
            public CareerCell(int nRowId, int nColumnId, int nColSpan)
            {
                this(nRowId, nColumnId, nColSpan, "", "");
            }
        public int getRowId()
        {
            return m_nRowId;
        }
        public int getColumnId()
        {
            return m_nColumnId;
        }
            public void setContent(String strCell)
            {
                m_strCell = strCell;
            }
            public void setFontName(String strFontName)
            {
                m_strFontName = strFontName;
            }
            public void setFontStyle(String strFontStyle)
            {
                m_strFontStyle = strFontStyle;
            }
            public void setJockeyColours(String strJockeyColours)
            {
                m_strJockeyColours = strJockeyColours;
            }
            public void setFontSize(int nFontSize)
            {
                m_nFontSize = nFontSize;
            }
            public void setRowSpan(int nRowSpan)
            {
                m_nRowSpan = nRowSpan;
            }
            public void setAlign(String strAlign)
            {
                m_strAlign = strAlign;
            }
            public String getContent()
            {
                return m_strCell;
            }
            public String getJockeyColours()
            {
                return m_strJockeyColours;
            }
            public String getFontStyle()
            {
                return m_strFontStyle;
            }
            public String getFontName()
            {
                return m_strFontName;
            }
            public String getAlign()
            {
                return m_strAlign;
            }
            public int getColSpan()
            {
                return m_nColSpan;
            }
            public int getRowSpan()
            {
                return m_nRowSpan;
            }
            public int getFontSize()
            {
                return m_nFontSize;
            }
            public String analyse()
             {
                 String strContent = "";
                 strContent += "Cell Coord: " + getRowId()+ "," + getColumnId() + "\n";

                 return strContent;
             }
        public void addRace(CareerRace race)
        {
            m_alRaces.add(race);
        }
        public Iterator<CareerRace> getRaceIterator()
        {
            return m_alRaces.iterator();
        }
        public int getRaceCount()
        {
            return m_alRaces.size();
        }
       public class CareerRace
        {
            private String m_strSource = "SF";  // SmartForm, could be overridden by RP or SL
            private String m_strTitle = "";
             private String m_strRace = "";
           
            public CareerRace(String strSource, String strTitle)
            {
                if (strSource != null)
                    m_strSource = strSource;
                if (strTitle != null)
                    m_strTitle = strTitle;
            }
            public String getSource()
            {
                return m_strSource;
            }
            public String getKey()
            {
                return m_strSource + m_strRace;     // unique identifier
            }
            public String getTitle()
            {
                return m_strTitle;
            }
            public void setContent(String strRace)
            {
                m_strRace = strRace;
            }
            public String getContent()
            {
                return m_strRace;
            }
        }
        }
    }
        public class CareerColumn
        {
            private String m_strColumn = "";
            private int m_nColSpan;
            private int m_nColumnId;
            private int m_nMeroWidth;       // number of Mero columns
            private int m_nOffset;      // x offset caused by previous columns

            public CareerColumn(int nColumnId, int nColSpan)
            {
                m_nColumnId = nColumnId;
                m_nColSpan = nColSpan;
            }
            public int getId()
            {
                return m_nColumnId;
            }
            public void setContent(String strColumn)
            {
                m_strColumn = strColumn;
            }
            public String getContent()
            {
                return m_strColumn;
            }
            public int getColSpan()
            {
                return m_nColSpan;
            }
            public int getOffset()
            {
                return m_nOffset;
            }
            public String analyse()
             {
                 String strContent = "";
                 strContent += "Column Id: " + getId() + "\n";

                 return strContent;
             }
            public int getMeroWidth()
            {
                return m_nMeroWidth;
            }
            public void setMeroWidth(int nMeroWidth)
            {
                m_nMeroWidth = nMeroWidth;
            }
            public void setOffset(int nOffset)
            {
                m_nOffset = nOffset;
            }
            public int getCellWidth()
            {
                    return m_nColSpan * (CareerSVGFactory.SVG_MERO_123_WIDTH * m_nMeroWidth + CareerSVGFactory.SVG_MERO_123_WIDTH_EXTRA + 5);                
            }
      }
   public class Pedigree
    {
        private String m_strGeneration0;
        private String[] m_astrGeneration1;
        private String[] m_astrGeneration2;
        private String[] m_astrGeneration3;
        
        public Pedigree(String strGeneration0, String strGeneration1, String strGeneration2, String strGeneration3)
        {
            m_strGeneration0 = strGeneration0;
            m_astrGeneration1 = strGeneration1.split(",");
            m_astrGeneration2 = strGeneration2.split(",");
            m_astrGeneration3 = strGeneration3.split(",");
        }
        public String getGeneration0()
        {
            return m_strGeneration0;
        }
        public String[] getGeneration1()
        {
            return m_astrGeneration1;
        }
        public String[] getGeneration2()
        {
            return m_astrGeneration2;
        }
        public String[] getGeneration3()
        {
            return m_astrGeneration3;
        }
    }

public static boolean cellClash(CareerRow previousRow, CareerRow currentRow)
{
    // to do: itertae by column not cell, as cells have different widths (test with Fascinating Rock May 2016)
    int nLength = previousRow.getCellCount();
    if (nLength > currentRow.getCellCount())
        nLength = currentRow.getCellCount();
    for(int i = 1; i <= nLength; i++)
    {
        if ((previousRow.getCell(i).getRaceCount() > 1) &&
                (currentRow.getCell(i).getRaceCount() > 1))
            return true;
    }
    return false;
}
}
