/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.jockeycolours.controllers;

import ene.eneform.smartform.bos.SmartformEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Simon
 */
@Controller
@RequestMapping("/smartform")
public class SmartformController {
    @GetMapping("/resetEnvironment")
    public String resetEnvironment(ModelMap model) {
        SmartformEnvironment.getInstance().reset();
        model.put("message", "resetEnvironment");
        return "message";
    }

/*
protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
{
        String strOutputType = request.getParameter("output");
        String strDataType = request.getParameter("type");
        String strAction = request.getParameter("action");
        if("search".equalsIgnoreCase(strAction))
        {
            String strHorse = request.getParameter("horse");
            ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
            ArrayList<SmartformHorse> list = SmartformHorseFactory.findHorse(statement, strHorse);
            statement.close();

            String strSearchURL = "/horse/search.jsp";
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(strSearchURL);
            dispatcher.forward(request, response);
            return;
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String strContent ="";

            if("reset_smartform_environment".equalsIgnoreCase(strAction))
            {
                SmartformEnvironment.getInstance().reset();
                strContent="reset";
            }
            else if("insert_nonrunner".equalsIgnoreCase(strAction))
            {
                String strRace = request.getParameter("race_id");
                String strRunner = request.getParameter("runner_id");
                String strReason = request.getParameter("reason");
                int nRace;
                int nRunner;

                try
                {
                    nRace = Integer.parseInt(strRace);
                    nRunner = Integer.parseInt(strRunner);

                    ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
                    int nReturn = SmartformRunnerFactory.insertNonRunner(statement, nRace, nRunner, strReason);
                    statement.close();

                    strContent=String.valueOf(nReturn);
                }
                catch(NumberFormatException e)
                {
                    strContent = "Invalid data";
                }
            }
            else if("set_race_going".equalsIgnoreCase(strAction))
            {
                String strRace = request.getParameter("race_id");
                String strGoing = request.getParameter("going");
                int nRace;

                try
                {
                    nRace = Integer.parseInt(strRace);

                    ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
                    int nReturn = SmartformRaceFactory.setRaceGoing(statement, nRace, strGoing);
                    statement.close();

                    strContent=String.valueOf(nReturn);
                }
                catch(NumberFormatException e)
                {
                    strContent = "Invalid data";
                }
            }
            else if("set_meeting_going".equalsIgnoreCase(strAction))
            {
                String strMeeting = request.getParameter("meeting_id");
                String strGoing = request.getParameter("going");
                int nMeeting;

                try
                {
                    nMeeting = Integer.parseInt(strMeeting);

                    ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
                    int nReturn = SmartformRaceFactory.setMeetingGoing(statement, nMeeting, strGoing);
                    statement.close();

                    strContent=String.valueOf(nReturn);
                }
                catch(NumberFormatException e)
                {
                    strContent = "Invalid data";
                }
            }
            else if ("create_race_pdf".equalsIgnoreCase(strAction))
            {
                String strRace = request.getParameter("race_id");
                int nRace;

                try
                {
                    nRace = Integer.parseInt(strRace);

                    SmartformDay day = SmartformEnvironment.getInstance().getTodaysData();
                    SmartformRace race = SmartformEnvironment.getInstance().getSmartformRace(nRace);
                    // strContent = SmartformPDFFactory.createPDFRace((SmartformDailyRace)race);
                }
                catch(NumberFormatException e)
                {
                    strContent = "Invalid data";
                }
            }
            else if ("create_meeting_pdf".equalsIgnoreCase(strAction))
            {
                String strMeeting = request.getParameter("meeting_id");
                int nMeeting;

                try
                {
                    nMeeting = Integer.parseInt(strMeeting);

                    ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
                    ArrayList<SmartformRace> races = SmartformRaceFactory.createSmartformDailyRaces(statement, nMeeting);
                    statement.close();
                    // strContent = SmartformPDFFactory.createPDFRace((SmartformDailyRace)races.get(0));
                }
                catch(NumberFormatException e)
                {
                    strContent = "Invalid data";
                }

            }
            if ("xml".equalsIgnoreCase(strOutputType))
                response.setContentType("text/xml");
            else
                response.setContentType("text/html");

            out.print(strContent);
            out.flush();
        } finally { 
            out.close();
        }
    }
*/
}
