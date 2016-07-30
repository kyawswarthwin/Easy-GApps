package com.myanmarunicorn.easygapps.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_main{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 1;BA.debugLine="btnInstall.Top = 50%y - (btnInstall.Height / 2)"[main/General script]
views.get("btninstall").vw.setTop((int)((50d / 100 * height)-((views.get("btninstall").vw.getHeight())/2d)));

}
}