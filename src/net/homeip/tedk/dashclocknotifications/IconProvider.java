package net.homeip.tedk.dashclocknotifications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class IconProvider extends ContentProvider {
	
	public static final String AUTHORITY = "net.homeip.tedk.dashclocknotifications.iconprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return "image/bmp";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
		return new String[] { "image/png" };
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		String[] parts = uri.toString().split("/");
		String packageName = parts[parts.length - 2];
		int resourceId = Integer.parseInt(parts[parts.length - 1]);
		Context c = getContext();
		try {
			c = c.createPackageContext(packageName, 0);
		} catch (NameNotFoundException e) {
			Log.e("NotificationListenerService", "Could not load application context", e);
		}
		File temp = null;
		FileOutputStream fos = null;
		try {
			temp = File.createTempFile(packageName + "." + resourceId, ".png");
			temp.deleteOnExit();
			fos = new FileOutputStream(temp);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap iconBitmap = BitmapFactory.decodeResource(c.getResources(), resourceId, options);
			iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			return ParcelFileDescriptor.open(temp, ParcelFileDescriptor.MODE_READ_ONLY);
		} catch (Exception e) {
			Log.e("NotificationListenerService", "Could not load icon", e);
			return null;
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (Exception e) {
				}
			if (temp != null)
				temp.deleteOnExit();
		}
	}

}
