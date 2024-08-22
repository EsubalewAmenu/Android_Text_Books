package com.herma.apps.textbooks.common;

/*
 * Created by Esubalew Amenu on 04-Jan-19
 * Mobile +251 92 348 1783
 * Email esubalew.a2009@gmail.com/
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DB extends SQLiteOpenHelper {

	Context context;
	static String DB_NAME =  "books.hrm";
	SQLiteDatabase myDataBase;

	public DB(Context context) {
		super(context, DB_NAME, null, 2 /* This is the version of the database*/);
		this.context = context;

		myDataBase = this.getReadableDatabase();

		onCreate(myDataBase);

	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		System.out.println("called from db on create");
		boolean dbexist = checkdatabase();
		if (dbexist) {
			//System.out.println("Database exists");
			opendatabase();
		} else {
			System.out.println("Database doesn't exist");
			try {
				createdatabase();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		System.out.println("called from db on update");

		try {
			System.out.println("db delete is deleted");
			File file = file = new File(context.getFilesDir().getPath()+"/"  + DB_NAME);
			if (file.exists()) file.delete();


			createdatabase();
			Toast.makeText(context, "Application Updated! please restart the app", Toast.LENGTH_SHORT).show();

		} catch (Exception ds) {
			System.out.println("db delete is on exception " +ds);
		}

	}
	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	public void opendatabase() throws SQLException {
		String myPath = context.getFilesDir().getPath()+"/" + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		//SQLiteDatabase.OPEN_READONLY);
	}

	private boolean checkdatabase() {
		//SQLiteDatabase checkdb = null;
		boolean checkdb = false;
		try {
			String myPath = context.getFilesDir().getPath()+"/"+DB_NAME;
			File dbfile = new File(myPath);
			//checkdb = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
			checkdb = dbfile.exists();
		} catch(SQLiteException e) {
			System.out.println("Database doesn't exist");
		}
		return checkdb;
	}

	public void createdatabase() throws IOException {
		boolean dbexist = checkdatabase();
		if(dbexist) {
			//System.out.println(" Database exists.");
		} else {
			try {
				copydatabase();
			} catch(IOException e) {
				throw new Error("Error copying database" + e);
			}
		}
	}
	private void copydatabase() throws IOException {
		//Open your local db as the input stream
		InputStream myinput = context.getAssets().open(DB_NAME);

		//Open the empty db as the output stream
		OutputStream myoutput = new FileOutputStream(context.getFilesDir().getPath()+"/"+DB_NAME);

		// transfer byte to inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myinput.read(buffer))>0) {
			myoutput.write(buffer,0,length);
		}

		//Close the streams
		myoutput.flush();
		myoutput.close();
		myinput.close();
	}
///////////////////////////////////////////////////////////////////////////////////
public Cursor getSelect(String select, String from, String where) {
	Cursor cursor = myDataBase.rawQuery("SELECT "+select+" FROM "+from+" WHERE "+ where, null);
	return cursor;
}

	public long insert(String table, ContentValues contentValues) {
		return myDataBase.insert(table, null, contentValues);
	}
	public long update(String table, ContentValues contentValues, String whereCol, String whereVal) {
		return myDataBase.update(table, contentValues, whereCol+" = ?", new String[]{whereVal});
	}
	public boolean deleteData(String from, String where) {
		return myDataBase.delete(from, where, null) > 0;
	}
}