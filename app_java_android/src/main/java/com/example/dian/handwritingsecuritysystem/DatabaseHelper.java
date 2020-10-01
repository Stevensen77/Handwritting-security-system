package com.example.dian.handwritingsecuritysystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Asus on 7/26/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String IMAGE_ID = "id";
    public static final String IMAGE = "image";
    public static final String DATABASE_NAME = "Handwritting.db";
    public static final String TABLE_NAME = "training";

    public static final int DATABASE_VERSION = 1;
    public static final String IMAGES_TABLE = "ImagesTable";


    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAMA";
    public static final String COL_3 = "NO_HP";
    public static final String COL_4 = "KTP";
    public static final String COL_5 = "FITUR_A";
    public static final String COL_6 = "FITUR_B";
    public static final String COL_7 = "FITUR_C";
    public static final String COL_8 = "FITUR_D";
    public static final String COL_9 = "FITUR_E";
    public static final String COL_10 = "FITUR_F";
    public static final String COL_11= "FITUR_G";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAMA TEXT, NO_HP TEXT, KTP TEXT,FITUR_A REAL,FITUR_B REAL,FITUR_C REAL,FITUR_D REAL,FITUR_E REAL,FITUR_F REAL,FITUR_G REAL )");
        //  db.execSQL("create table " + IMAGES_TABLE + "(id INTEGER PRIMARY KEY, image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        // db.execSQL("DROP TABLE IF EXISTS "+IMAGES_TABLE);
        onCreate(db);
    }


    public boolean insertData( String nama, String no_hp, String ktp,double fitur_a,double fitur_b, double fitur_c, double fitur_d, double fitur_e, double fitur_f, double fitur_g) {
        SQLiteDatabase db = this.getWritableDatabase();

        //public boolean insertData(String id, String ktp) {
        //  SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // contentValues.put(COL_1,id);
        contentValues.put(COL_2,nama);
        contentValues.put(COL_3,no_hp);
        contentValues.put(COL_4,ktp);
        contentValues.put(COL_5,fitur_a);
        contentValues.put(COL_6,fitur_b);
        contentValues.put(COL_7,fitur_c);
        contentValues.put(COL_8,fitur_d);
        contentValues.put(COL_9,fitur_e);
        contentValues.put(COL_10,fitur_f);
        contentValues.put(COL_11,fitur_g);

        System.out.println("Isi content to insert:"+contentValues);

        long result = db.insert(TABLE_NAME,null ,contentValues);

        System.out.println("ISI RESULT ITU :"+result);

        if(result == -1)
            return false;
        else
            return true;



    }

    public void insertBitmap(String id, Bitmap bm)  {

        // Convert the image into byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] buffer=out.toByteArray();
        // Open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();
        // Start the transaction.
        db.beginTransaction();
        ContentValues values;


        values = new ContentValues();
        values.put(IMAGE_ID, id);
        values.put(IMAGE, buffer);

        // Insert Row
        long i = db.insert(IMAGES_TABLE, null, values);
        Log.i("Insert", i + "");
        // Insert into database successfully.
        db.setTransactionSuccessful();


    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);

        return res;
    }

    public boolean updateData(String id,String name,String surname,String marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,surname);
        contentValues.put(COL_4,marks);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

}
