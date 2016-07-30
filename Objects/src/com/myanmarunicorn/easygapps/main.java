package com.myanmarunicorn.easygapps;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.myanmarunicorn.easygapps", "com.myanmarunicorn.easygapps.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.myanmarunicorn.easygapps", "com.myanmarunicorn.easygapps.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.myanmarunicorn.easygapps.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 23;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 30;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _btninstall_click() throws Exception{
com.datasteam.b4a.system.superuser.SuShell _su = null;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.phone.Phone _p = null;
String _apilevel = "";
anywheresoftware.b4a.objects.collections.List _packages = null;
anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _textwriter = null;
anywheresoftware.b4a.objects.collections.Map _colpackage = null;
String _packagename = "";
anywheresoftware.b4a.phone.PackageManagerWrapper _pm = null;
anywheresoftware.b4a.objects.collections.List _installedpackages = null;
String _filesource = "";
String _dirtarget = "";
String _filetarget = "";
 //BA.debugLineNum = 34;BA.debugLine="Sub btnInstall_Click";
 //BA.debugLineNum = 35;BA.debugLine="Dim su As SuShell";
_su = new com.datasteam.b4a.system.superuser.SuShell();
 //BA.debugLineNum = 36;BA.debugLine="If su.DeviceRooted Then";
if (_su.DeviceRooted()) { 
 //BA.debugLineNum = 37;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 38;BA.debugLine="parser.Initialize(File.ReadString(File.DirAssets";
_parser.Initialize(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"packages.json"));
 //BA.debugLineNum = 39;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 40;BA.debugLine="Dim p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 41;BA.debugLine="Dim APILevel As String = p.SdkVersion";
_apilevel = BA.NumberToString(_p.getSdkVersion());
 //BA.debugLineNum = 42;BA.debugLine="Dim Packages As List = root.Get(APILevel)";
_packages = new anywheresoftware.b4a.objects.collections.List();
_packages.setObject((java.util.List)(_root.Get((Object)(_apilevel))));
 //BA.debugLineNum = 43;BA.debugLine="If Packages.IsInitialized Then";
if (_packages.IsInitialized()) { 
 //BA.debugLineNum = 44;BA.debugLine="ProgressDialogShow2(\"Installing...\", False)";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow2(mostCurrent.activityBA,"Installing...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 45;BA.debugLine="su.ExecuteMultiple(Array As String(\"chmod 0777";
_su.ExecuteMultiple(processBA,new String[]{"chmod 0777 /data/local/tmp","rm -r /data/local/tmp/*"}).WaitForCompletion();
 //BA.debugLineNum = 46;BA.debugLine="File.Copy(File.DirAssets, \"busybox\", \"/data/loc";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"busybox","/data/local/tmp","busybox");
 //BA.debugLineNum = 47;BA.debugLine="Dim TextWriter As TextWriter";
_textwriter = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 48;BA.debugLine="TextWriter.Initialize(File.OpenOutput(\"/data/lo";
_textwriter.Initialize((java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.File.OpenOutput("/data/local/tmp","install.sh",anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 49;BA.debugLine="TextWriter.WriteList(Array As String(\"export PA";
_textwriter.WriteList(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"export PATH=/data/local/tmp:$PATH","busybox mount -o remount,rw /system"}));
 //BA.debugLineNum = 50;BA.debugLine="For Each colPackage As Map In Packages";
_colpackage = new anywheresoftware.b4a.objects.collections.Map();
final anywheresoftware.b4a.BA.IterableList group27 = _packages;
final int groupLen27 = group27.getSize();
for (int index27 = 0;index27 < groupLen27 ;index27++){
_colpackage.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(group27.Get(index27)));
 //BA.debugLineNum = 51;BA.debugLine="Dim PackageName As String = colPackage.Get(\"Pa";
_packagename = BA.ObjectToString(_colpackage.Get((Object)("PackageName")));
 //BA.debugLineNum = 52;BA.debugLine="Dim pm As PackageManager";
_pm = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Dim InstalledPackages As List = pm.GetInstalle";
_installedpackages = new anywheresoftware.b4a.objects.collections.List();
_installedpackages = _pm.GetInstalledPackages();
 //BA.debugLineNum = 54;BA.debugLine="If InstalledPackages.IndexOf(PackageName) = -1";
if (_installedpackages.IndexOf((Object)(_packagename))==-1) { 
 //BA.debugLineNum = 55;BA.debugLine="Dim FileSource As String = colPackage.Get(\"Fi";
_filesource = BA.ObjectToString(_colpackage.Get((Object)("FileSource")));
 //BA.debugLineNum = 56;BA.debugLine="Dim DirTarget As String = colPackage.Get(\"Dir";
_dirtarget = BA.ObjectToString(_colpackage.Get((Object)("DirTarget")));
 //BA.debugLineNum = 57;BA.debugLine="Dim FileTarget As String = colPackage.Get(\"Fi";
_filetarget = BA.ObjectToString(_colpackage.Get((Object)("FileTarget")));
 //BA.debugLineNum = 58;BA.debugLine="File.Copy(File.DirAssets, FileSource, \"/data/";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_filesource,"/data/local/tmp",_filetarget);
 //BA.debugLineNum = 59;BA.debugLine="TextWriter.WriteList(Array As String(\"cat /da";
_textwriter.WriteList(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"cat /data/local/tmp/"+_filetarget+" > "+_dirtarget+"/"+_filetarget,"chown root.root "+_dirtarget+"/"+_filetarget,"chmod 0644 "+_dirtarget+"/"+_filetarget}));
 };
 }
;
 //BA.debugLineNum = 62;BA.debugLine="TextWriter.Close";
_textwriter.Close();
 //BA.debugLineNum = 63;BA.debugLine="su.ExecuteMultiple(Array As String(\"chmod 0755";
_su.ExecuteMultiple(processBA,new String[]{"chmod 0755 /data/local/tmp/busybox","chmod 0755 /data/local/tmp/install.sh","sh /data/local/tmp/install.sh","rm -r /data/local/tmp/*","reboot"}).WaitForCompletion();
 //BA.debugLineNum = 64;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 }else {
 //BA.debugLineNum = 66;BA.debugLine="Msgbox(\"Device Is Not Supported.\", \"Error\")";
anywheresoftware.b4a.keywords.Common.Msgbox("Device Is Not Supported.","Error",mostCurrent.activityBA);
 };
 }else {
 //BA.debugLineNum = 69;BA.debugLine="Msgbox(\"Root Access Is Required.\", \"Easy GApps\")";
anywheresoftware.b4a.keywords.Common.Msgbox("Root Access Is Required.","Easy GApps",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="End Sub";
return "";
}
}
