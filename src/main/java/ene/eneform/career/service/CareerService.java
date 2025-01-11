package ene.eneform.career.service;

import ene.eneform.career.CareerDefinition;
import ene.eneform.career.CareerEnvironment;
import ene.eneform.career.CareerSVGFactory;
import ene.eneform.career.DayDefinition;
import ene.eneform.career.MeetingDefinition;
import ene.eneform.colours.database.JCEventsFactory;
import ene.eneform.mero.service.MeroService;
import ene.eneform.smartform.factory.SmartformRaceFactory;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.ExecuteURL;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {
    @Value("${ene.eneform.mero.SERVER_NAME}")
    private static String SERVER_NAME;

    private MeroService meroService;
    private CareerEnvironment careerEnvironment;

    public void generateCareers(ENEStatement statement, String[] astrHorses, boolean bText) {
        for (int i = 0; i < astrHorses.length; i++) {
            try {
                (new CareerSVGFactory(meroService, statement, careerEnvironment.getCareer(astrHorses[i]), bText)).generateCareer(true);
            } catch (IOException e) {
                // carry on to next
                System.out.println("generateCareers IOException: " + astrHorses[i]);
            }
        }
    }

    public void generateCareersHTML(ENEStatement statement, String[] astrHorses) {
        for (int i = 0; i < astrHorses.length; i++) {
            try {
                (new CareerSVGFactory(meroService, statement, careerEnvironment.getCareer(astrHorses[i]), true)).generateCareerHTML();
            } catch (IOException e) {
                // carry on to next
                System.out.println("generateCareersHTML IOException: " + astrHorses[i]);
            }
        }
    }

    public String generateCareer(ENEStatement statement, String strHorse) {
        String strReturnURL = null;
        CareerDefinition career = careerEnvironment.getCareer(strHorse);
        if (career != null) {
            try {
                (new CareerSVGFactory(meroService, statement, career, true)).generateCareer(false);
                String strDirectory = "horses";
                if (career.getType().equalsIgnoreCase("meeting"))
                    strDirectory = "meetings";
                String strServerName = SERVER_NAME;
                //ExecuteURL.openBrowser(strServerName + strDirectory + "/" + career.getName().replace(" ", "_") + ".html");
                strReturnURL = strServerName + strDirectory + "/" + career.getName().replace(" ", "_") + ".html";    // return url of updated page
            } catch (Exception e) {
                System.out.println("Error creating Career SVG for: " + strHorse);
                e.printStackTrace();
            }
        } else
            System.out.println("Career not found: " + strHorse);

        return strReturnURL;
    }

    public void generateMeetingUpdates(ENEStatement statement, String strName, int nMeeting) {
        CareerDefinition career = new DayDefinition(nMeeting, strName);
        try {
            (new CareerSVGFactory(meroService, statement, career, true)).generateCareer(false);
            String strDirectory = "meetings";
            String strServerName = SERVER_NAME;
            ExecuteURL.openBrowser(strServerName + strDirectory + "/" + career.getId() + ".html");
        } catch (Exception e) {
            System.out.println("Error creating Meeting Career SVG for: " + career.getName());
            e.printStackTrace();
        }
    }

    public void generateMeetingUpdates(ENEStatement statement, String strCourse, int nMonth, int nStartYear, int nEndYear) {
        for (int i = nStartYear; i <= nEndYear; i++) {
            CareerDefinition career = new MeetingDefinition(strCourse.replace("_", " ") + " " + i, strCourse + "|" + nMonth + "|" + i);
            try {
                (new CareerSVGFactory(meroService, statement, career, true)).generateCareer(false);
                String strDirectory = "meetings";
                String strServerName = SERVER_NAME;
                ExecuteURL.openBrowser(strServerName + strDirectory + "/" + career.getName().replace(" ", "_") + ".html");
            } catch (Exception e) {
                System.out.println("Error creating Meeting Career SVG for: " + career.getName());
                e.printStackTrace();
            }
        }

    }
    public JSONObject generateLatestCareerUpdates(ENEStatement statement)
    {
        int nDays = JCEventsFactory.getEventDayInterval(statement, "latest_career_generate");
        JSONObject obj = generateCareerUpdates(statement, nDays);
        JCEventsFactory.updateEventDate(statement, "latest_career_generate");
        return obj;
    }
    public JSONObject generateCareerUpdates(ENEStatement statement, int nDays)
    {
        if (nDays > 0)
        {
            ArrayList<String> alNames = SmartformRaceFactory.getDateRunnerNames(statement, nDays);
            return generateCareerUpdates(statement, alNames);
        }
        return new JSONObject();
    }

    public JSONObject generateCareerUpdates(ENEStatement statement, List<String> alNames) {
        JSONObject obj = new JSONObject();
        JSONArray pages = new JSONArray();
        for (int i = 0; i < alNames.size(); i++) {
            String strHorse = alNames.get(i).trim();
            String strReturnURL = generateCareer(statement, strHorse);
            if (strReturnURL != null)
                pages.put(strReturnURL);
        }
        if (pages.length() > 0)
            obj.put("pages", pages);

        return obj;
    }

    public void generateMeetingUpdates(ENEStatement statement, String strCourse, int nMonth, int nYear) {
        generateMeetingUpdates(statement, strCourse, nMonth, nYear, nYear);
    }
}