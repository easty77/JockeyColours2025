/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import ene.eneform.utils.wls.ReportColumn;
import ene.eneform.utils.wls.ReportTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author simon
 */
public class JSONUtils {

    public static final int JSON = 2;
    public static final int ARRAY = 3;
    //private static String sm_strQueryDir = "C:\\Users\\simon\\Documents\\horses\\query\\";
    //private static String sm_strOutputDir = "C:\\Users\\simon\\Documents\\NetBeansProjects\\JockeyColours\\web\\json\\data\\";
    public static final int DATATABLE = 1;
    
    public static String getJSONAttributeString(JSONObject obj, String strAttribute)
    {
        String strValue = "";
        if (obj.has(strAttribute))
            strValue = (String) obj.get(strAttribute);
        
        return strValue;
    }
    public static JSONObject parse(String strJSON)
    {
        // use setLenient for non-perfect JSON
        JSONObject obj = null;
            obj = new JSONObject(strJSON);

        return obj;
    }
    public static JSONObject parseJSONStringObject(String strContent) throws IOException
    {
        // can be case to JSONObject, JSONArray

        StringReader reader = new StringReader(strContent);
        JSONTokener tokener = new JSONTokener(reader);
        JSONObject jsonData = new JSONObject(tokener);
        reader.close();
        return jsonData;
    }
    public static JSONObject parseJSONFileObject(String strFilename) throws FileNotFoundException, IOException
    {
        // can be case to JSONObject, JSONArray

        FileReader reader = new FileReader(strFilename);
        JSONTokener tokener = new JSONTokener(reader);
        JSONObject jsonData = new JSONObject(tokener);
        reader.close();
        return jsonData;
    }
    public static JSONArray parseJSONFileArray(String strFilename) throws FileNotFoundException, IOException
    {
        // can be case to JSONObject, JSONArray

        FileReader reader = new FileReader(strFilename);
        JSONTokener tokener = new JSONTokener(reader);
        JSONArray jsonData = new JSONArray(tokener);
        reader.close();
        return jsonData;
    }
    public static void reportJSON2File(ENEStatement statement, String strQuery, String strOutputFile, int nMaxRecords, int nOutputType) {
        PrintWriter out = null;
        try {
            String strOutput = "";
            JSONObject data = reportJSON(statement, strQuery, nMaxRecords, nOutputType);
            strOutput = data.toString();
            out = new PrintWriter(strOutputFile);
            out.println(strOutput);
        } catch (FileNotFoundException e) {
        }  finally {
            if (out != null) {
                out.close();
            }
        }
    }
    public static String reportJSONFile2File(ENEStatement statement, String strInputFile, JSONObject params, String strOutputFile, int nMaxRecords, int nOutputType, Class c) {
        PrintWriter out = null;
        try {
            String strQuery = processQueryFile(strInputFile, true, params, c);
            if (strQuery != null)
            {
                JSONObject data = reportJSON(statement, strQuery, nMaxRecords, nOutputType);
                String strOutput = data.toString();
                out = new PrintWriter(strOutputFile);
                out.println(strOutput);
                return "Output written to: " + strOutputFile;
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return "Error";
    }
    public static String processQueryFile(String strInputFile, boolean bFileExternal, JSONObject params, Class c)
    {
        System.out.println("processQueryFile: " + strInputFile);
        String strQuery = null;
        try 
        {
            if (bFileExternal)
            {
                strQuery = "";
                FileInputStream fstream = new FileInputStream(strInputFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null)   {
                    // Print the content on the console
                    if (strQuery.indexOf("-- ") != 0)   // eliminate comments
                        strQuery += (strLine + '\n');
                }

                //byte[] encoded = Files.readAllBytes(Paths.get(strInputFile));
                //strQuery = new String(encoded);
            }
            else
            {
                // inside Java package
                InputStream is = FileUtils.loadFile(strInputFile, c);
                if (is != null)
                    strQuery = StringUtils.convertInputStream(is);
                else
                    System.out.println("Empty stream");
            }
            if (strQuery != null)
            {
                Iterator<String> iter = params.keySet().iterator();
                while (iter.hasNext()) 
                {
                    String strKey = iter.next();
                    strQuery = strQuery.replace("${" + strKey.toUpperCase() + "}", params.get(strKey).toString());
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        return strQuery;
    }

    public static int updateFile(ENEStatement statement, String strInputFile, boolean bFileExternal, JSONObject params, Class c) 
    {
        String strContent = processQueryFile(strInputFile, bFileExternal, params, c);
        if (strContent != null)
        {
            int nUpdates = statement.executeUpdate(strContent);
            return nUpdates;
        }
         else
            return 0;
    }
    public static JSONObject reportJSONFile(ENEStatement statement, String strInputFile, JSONObject params, int nMaxRecords, int nOutputType, Class c) 
    {
        String strContent = processQueryFile(strInputFile, true, params, c);
        if (strContent != null)
            return reportJSON(statement, strContent, nMaxRecords, nOutputType);
        else
            return null;
     }

    public static JSONArray reportJSONArray(ENEStatement statement, String strQuery, int nMaxRecords) 
    {
        JSONObject obj = reportJSON(statement, strQuery, nMaxRecords, JSON);
        
        return (JSONArray) obj.get("data");
    }

    public static JSONObject reportJSON(ENEStatement statement, String strQuery, int nMaxRecords, int nOutputType) {
        System.out.println("reportJSON: " + nOutputType);
        System.out.println(strQuery);
        JSONObject report = new JSONObject(); // WLSDataTable object
        ReportTable reportTable = null;
        try {
            reportTable = new ReportTable(statement, strQuery, "", nMaxRecords, "yyyy-MM-dd", "HH:mm", false, "no");
            reportTable.initialise(false);
            System.out.println("#Rows: " + reportTable.getNrRows());
            if (nOutputType == DATATABLE) {
                JSONArray columns = new JSONArray();
                for (int i = 1; i <= reportTable.getNrColumns(); i++) {
                    ReportColumn column = reportTable.getColumn(i);
                    JSONObject colobj = new JSONObject();
                    colobj.put("code", column.getCode());
                    colobj.put("title", column.getTitle());
                    colobj.put("type", column.getType());
                    columns.put(colobj);
                }
                report.put("columndata", columns);
            }
            JSONArray rows;
            if (nOutputType == ARRAY) {
                rows = reportTable.getJSONReportArrays();
            } else {
                rows = reportTable.getJSONReportRows(false);    // nOutputType == DATATABLE
            }
            if (nOutputType == DATATABLE) {
                report.put("rowdata", rows);
            } else {
                report.put("data", rows);
            }
            return report;
        } catch (SQLException e) {
            System.out.println("reportJSON: " + e.getMessage());
        } finally {
            try {
                reportTable.cleanup();
            } catch (SQLException e) {
            }
        }
        return report;
    }
/*
    public static void selectJSONFile(ENEStatement statement, String strInputFile, String strOutputFile, JSONObject params, int nMaxRecords, boolean bDatatable) {
        PrintWriter out = null;
        String strOutput = selectJSONFile(statement, strInputFile, params, nMaxRecords, bDatatable);
        try {
            out = new PrintWriter(strOutputFile);
            out.println(strOutput);
        } catch (FileNotFoundException e) {
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    public static String selectJSONFile(ENEStatement statement, String strFileName, JSONObject params, int nMaxRecords, boolean bDefault) {
        // astrValues is set of values of form MONTH_OFFSET=0  - the tag ${MONTH_OFFSET} will be replaced by 0
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(strFileName));
            String strContent = new String(encoded);
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()) {
                String strKey = iter.next();
                strContent = strContent.replace("${" + strKey.toUpperCase() + "}", params.get(strKey).toString());
            }
            return selectJSON(statement, strContent, nMaxRecords, bDefault);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String selectJSON(ENEStatement statement, String strQuery, int nMaxRecords, boolean bDatatable) {
        System.out.println("SELECT_JSON");
        System.out.println(strQuery);
        String strReport = "";
        ReportTable reportTable = null;
        try {
            reportTable = new ReportTable(statement, strQuery, "", 10000, "yyyy-MM-dd", "HH:mm", false, "no");
            reportTable.initialise(false);
            JSONArray rows = reportTable.getJSONReportRows(bDatatable); // bDatatable - force NULL values to be something
            strReport = rows.serialize(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reportTable != null) {
                    reportTable.cleanup();
                }
            } catch (SQLException e) {
            }
        }
        return strReport;
    } */
}
