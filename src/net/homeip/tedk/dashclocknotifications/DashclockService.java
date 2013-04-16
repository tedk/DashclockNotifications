package net.homeip.tedk.dashclocknotifications;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.net.Uri;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class DashclockService extends DashClockExtension {
	
	public static class NotificationInfo {
		public String time;
		public String app;
		public String text;
		public int num;
		public int icon;
		public PendingIntent intent;
		public NotificationInfo(String time, String app, String text, int num, int icon, PendingIntent intent) {
			this.time = time;
			this.app = app;
			this.text = text;
			this.num = num;
			this.icon = icon;
			this.intent = intent;
		}
		@Override
		public boolean equals(Object that) {
			if(that == null)
				return false;
			if(this == that)
				return true;
			if(this.getClass() != that.getClass())
				return false;
			
			NotificationInfo ni = (NotificationInfo) that;
			if(this.app.equals("Messaging") || ni.app.equals("Messaging"))
				return false;
			return this.app.equals(ni.app);
		}
	}
	
	private static List<NotificationInfo> notifications = new LinkedList<NotificationInfo>();
	private static Set<DashclockService> widgets = new HashSet<DashclockService>();
	
	public synchronized static void addNotification(NotificationInfo ni) {
		int index = notifications.indexOf(ni);
		if(index >= 0 && index < notifications.size()) {
			notifications.remove(index);
		}
		notifications.add(0, ni);
		if(notifications.size() > 50) {
			notifications.remove(50);
		}
		updateWidgets();
	}
	
	private synchronized static NotificationInfo getNotification(int num) {
		return num >= notifications.size() ? null : notifications.get(num);
	}
	
	public synchronized static void clearNotifications() {
		notifications.clear();
		updateWidgets();
	}
	
	public static void updateWidgets() {
		for(DashclockService ds : widgets) {
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
	
	public synchronized void updateWidget() {
		NotificationInfo ni = getNotification(widgetNum);
		if(ni == null) {
			publishUpdate(new ExtensionData()
    		.visible(false));
		} else {
			String body = ni.text;
			publishUpdate(new ExtensionData()
        		.visible(true)
        		.icon(ni.icon)
        		.status(ni.text)
        		.expandedTitle(ni.app)
        		.expandedBody(body)
        		.clickIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("dashclocknotifications://" + ni.intent.getCreatorPackage()))));
        	// TODO fix icon
		}
	}
	
}
