package com.androidstudy.pushchat;

import android.content.Context;
import android.util.DisplayMetrics;

public class DeviceUtil
{
	public static DisplayMetrics mDispMetrics;

	public static void init(Context context)
	{
	    // Screen density
	    mDispMetrics = context.getResources().getDisplayMetrics();
	}
}