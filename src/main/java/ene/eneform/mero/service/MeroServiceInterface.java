package ene.eneform.mero.service;

import java.awt.*;

public interface MeroServiceInterface
{
    String parseDescription(String strDescription);
    void generateSVG(String strDefinition, String strDirectory, String strFileName, String strBackgroundColour, Point capOrigin, boolean bCompress);
}
