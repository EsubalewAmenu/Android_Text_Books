package com.herma.apps.textbooks.common;

/*
 * Created by Esubalew Amenu on 04-Jan-19
 * Mobile +251 92 348 1783
 * Email esubalew.a2009@gmail.com/
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.herma.apps.textbooks.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DB extends SQLiteOpenHelper {

	private static String DB_PATH = "";//databases/";
	private static String DB_NAME = "";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	public DB(Context context, String db_name) {
		super(context, db_name, null, 2);
		DB_PATH = context.getFilesDir().getPath()+"/";
		DB_NAME = db_name;
		this.myContext = context;
	}

	// Creates a empty database
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database " + e);
			}
		}
	}

	public void writeDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getWritableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	// Check if the database already exist
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;

//			dbDec("75ad40ca6390421ca7a10dda3964b5eb");
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {
			// If database does't exist.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null;
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;

		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
				//SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		try {
			File file = file = new File(DB_PATH + DB_NAME);
			if (file.exists()) file.delete();
			System.out.println("db delete is deleted");
		} catch (Exception ds) {
			System.out.println("db delete is on exception " +ds);
		}
	}
///////////////////////////////////////////////////////////////////////////////////
public Cursor getSelect(String select, String from, String where) {
	Cursor cursor = myDataBase.rawQuery("SELECT "+select+" FROM "+from+" WHERE "+ where, null);
	return cursor;
}

//	public Cursor getUpdate(String table, String set, String where) {
//		Cursor cursor = myDataBase.rawQuery("UPDATE "+table+" SET "+set+" WHERE "+ where, null);
//		return cursor;
//	}
//
//	public Cursor getInsertPrize(String date, String price) {
//		Cursor cursor = myDataBase.rawQuery("INSERT INTO prize (`date`,`price`) VALUES ('"+date+"', '"+price+"')", null);
//		return cursor;
//	}
//
//	public Cursor doExcute(String command){
//		return myDataBase.rawQuery(command, null);
//	}
//	public String[][] getSelectArray(String select, String from, String where){
//		Cursor c = myDataBase.rawQuery("SELECT "+select+" FROM "+from+" WHERE "+ where, null);
//		String arrayString[][] = new String[c.getCount()][7];
//		int i = 0;
//		if (c.moveToFirst()) {
//			do{
//				arrayString[i][0] = c.getString(0);
//				arrayString[i][1] = c.getString(1);
//				arrayString[i][2] = c.getString(2);
//				arrayString[i][3] = c.getString(3);
//				arrayString[i][4] = c.getString(4);
//				arrayString[i][5] = c.getString(5);
//				arrayString[i][6] = c.getString(6);
//				i++;
//			} while (c.moveToNext());
//		}
//		return arrayString;
//	}

//	public boolean dbDec(String p) {
//		try {
//			byte[] salt = {69, 121, 101, 45, 62, 118, 101, 114, 69, 121, 101, 45, 62, 118, 101, 114};
//
//			SecretKeyFactory factory = SecretKeyFactory
//					.getInstance("PBKDF2WithHmacSHA1");
//			String fullPassword = p+"ibooks.hrm";
//			KeySpec keySpec = new PBEKeySpec(fullPassword.toCharArray(), salt, 65536,
//					256);
//			SecretKey tmp = factory.generateSecret(keySpec);
//			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
//
//			// file decryption
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//
//			IvParameterSpec ivspec = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
//			cipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
//			InputStream fis = myContext.getResources().openRawResource(R.raw.ibooks);
////			FileInputStream fis = new FileInputStream(myContext.getFilesDir() + "/ibooks");
//			FileOutputStream fos = new FileOutputStream(myContext.getFilesDir() + "/books.hrm");
//			byte[] in = new byte[64];
//			int read;
//			while ((read = fis.read(in)) != -1) {
//				byte[] output = cipher.update(in, 0, read);
//				if (output != null)
//					fos.write(output);
//			}
//
//			byte[] output = cipher.doFinal();
//			if (output != null)
//				fos.write(output);
//			fis.close();
//			fos.flush();
//			fos.close();
//		}catch (Exception lkj) { System.out.println("doenc exp " + lkj);}
//		return true;
//	}
}