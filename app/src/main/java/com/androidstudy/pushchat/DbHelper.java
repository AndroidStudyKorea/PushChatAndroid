package com.androidstudy.pushchat;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper
{
	private static final String TAG = "DbHelper";
	
	private static final String DATABASE_NAME = "pushchat.db";
	private static final int DATABASE_VERSION = 1;

	public static final int TABLE_INDEX_AUTHOR			= 1;
	public static final int TABLE_INDEX_CREATED			= 2;
	public static final int TABLE_INDEX_CONTENT			= 3;
	public static final int TABLE_INDEX_MY_TALK			= 4;

	public DbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql;

		sql = "CREATE TABLE talk_log (id integer primary key autoincrement, " +
				"author text, created date, content text, my_talk boolean)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}
	
	public ArrayList<TalkModel> getLalkLog()
	{
		try
		{
			SQLiteDatabase db = getWritableDatabase();

			Cursor cursor = db.rawQuery("SELECT * FROM talk_log ORDER BY id ASC", null);
			if (cursor == null)
			{
				Log.e(TAG, "db.rawQuery() error!");
				db.close();
				return null;
			}
	
			ArrayList<TalkModel> list = new ArrayList<TalkModel>();
	
			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				TalkModel talk = new TalkModel();

				talk.author = cursor.getString(TABLE_INDEX_AUTHOR);
				talk.created = CalUtil.stringToDate(cursor.getString(TABLE_INDEX_CREATED));
				talk.content = cursor.getString(TABLE_INDEX_CONTENT);
				talk.my_talk = cursor.getInt(TABLE_INDEX_MY_TALK) > 0;

				list.add(talk);
				cursor.moveToNext();
			}
			cursor.close();
			db.close();
			
			return list;
		}
		catch (Exception e) {
			Log.e(TAG, e.toString());
			return null;
		}
	}

	public void clearTalkLog()
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM talk_log");
		db.close();
	}

	public void addTalk(TalkModel talk)
	{
		try
		{
			SQLiteDatabase db = getWritableDatabase();

			// 레코드 개수 얻기
			String[] selectColumns = { "count(id)" };
			Cursor cursor = db.query("talk_log", selectColumns, null, null, null, null, null);
			cursor.moveToFirst();
			long recCount = cursor.getLong(0);
			cursor.close();

			// 2000개 이상이면 1000개를 삭제
			if (recCount >= 2000)
			{
				cursor = db.rawQuery("SELECT id FROM talk_log ORDER BY id ASC LIMIT 1000", null);

				String sql = "";
				cursor.moveToFirst();
				while (!cursor.isAfterLast())
				{
					long id = cursor.getLong(cursor.getColumnIndex("id"));
					if (sql.length() != 0)
						sql += ",";
					sql += id;

					cursor.moveToNext();
				}
				cursor.close();

				sql = "DELETE FROM talk_log WHERE _id IN (" + sql + ")";

				cursor = db.rawQuery(sql, null);
				Log.d(TAG, "[" + sql + "] count:" + cursor.getCount());
				cursor.close();
			}

			ContentValues values = new ContentValues();
			values.put("author", talk.author);
			values.put("created", CalUtil.dateToString(talk.created));
			values.put("content", talk.content);
			values.put("my_talk", talk.my_talk);
			if (db.insert("talk_log", null, values) <= 0)
				Log.e(TAG, "db.insert() error!");

			db.close();
		}
		catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
}