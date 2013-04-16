package net.homeip.tedk.dashclocknotifications;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class DashclockService extends DashClockExtension {
	
	public static class NotificationInfo {
		public String time;
		public String app;
		public String text;
		public String num;
		public int icon;
		public NotificationInfo(String time, String app, String text, String num, int icon) {
			this.time = time;
			this.app = app;
			this.text = text;
			this.num = num;
			this.icon = icon;
		}
	}
	
	private static List<NotificationInfo> notifications = new LinkedList<NotificationInfo>();
	private static Set<DashclockService> widgets = new HashSet<DashclockService>();
	
	public synchronized static void AddNotification(NotificationInfo ni)
	{
		notifications.add(0, ni);
		if(notifications.size() > 5) {
			notifications.remove(5);
		}
		updateWidgets();
	}
	
	public synchronized static NotificationInfo getNotification(int num)
	{
		return num >= notifications.size() ? null : notifications.get(num);
	}
	
	public static void updateWidgets(){
		for(DashclockService ds : widgets)
		{
			ds.updateWidget();
		}
	}
	
	private int widgetNum = -1;

	@Override
	protected void onInitialize(boolean isReconnect) {
		super.onInitialize(isReconnect);
		PackageManager pm = getPackageManager();
		try {
			ServiceInfo si = pm.getServiceInfo(new ComponentName(this.getApplicationContext(), this.getClass()), 0);
			String label = si.loadLabel(pm).toString();
			widgetNum = Integer.parseInt(label.substring(label.indexOf('#') + 1)) - 1;
		} catch (NameNotFoundException e) {
			//Log.e("DashclockService", "Could not get ServiceInfo", e);
		}	
		widgets.add(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		widgets.remove(this);
	}
	
	@Override
	protected void onUpdateData(int arg0) {
		updateWidget();
	}
	
	public synchronized void updateWidget()
	{
		NotificationInfo ni = getNotification(widgetNum);
		if(ni == null) {
			publishUpdate(new ExtensionData()
    		.visible(false));
		} else {
			publishUpdate(new ExtensionData()
        		.visible(true)
        		.icon(ni.icon)
        		.status(ni.app)
        		.expandedTitle(ni.app)
        		.expandedBody(ni.text));
			// TODO fix icon and add clickIntent
		}
	}
	
}
