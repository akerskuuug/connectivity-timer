package com.connectivitymanager.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Tools {
	public static void set3GEnabled(Context context, boolean enabled) {
		ConnectivityManager cnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Method dataMtd;
		try {

			// Set mobile data connection according to the
			// user's request
			dataMtd = ConnectivityManager.class.getDeclaredMethod(
					"setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(cnMgr, enabled);

		} catch (SecurityException e) {
			Log.e("Tools.set3GEnabled() SE", e.getLocalizedMessage());
		} catch (NoSuchMethodException e) {
			Log.e("Tools.set3GEnabled() NSME", e.getLocalizedMessage());
		} catch (IllegalArgumentException e) {
			Log.e("Tools.set3GEnabled() IArE", e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			Log.e("Tools.set3GEnabled() IAcE", e.getLocalizedMessage());
		} catch (InvocationTargetException e) {
			if (e != null && e.getLocalizedMessage() != null) {
				Log.e("Tools.set3GEnabled() ITE", e.getLocalizedMessage());
			}
		}

	}
}
