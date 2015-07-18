package com.androidstudy.pushchat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalUtil
{
	public static Date stringToDate(String str)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			return format.parse(str);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static String dateToString(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			return format.format(date);
		}
		catch (Exception e) {
			return null;
		}
	}
}