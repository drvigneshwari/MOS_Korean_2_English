package com.example.mos_webscrap_01;

//region Libraries
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
//endregion

public class DBAdapterClass {

        myDbHelper dbHelper;
        public DBAdapterClass(Context context)
        {
            dbHelper = new myDbHelper(context);
        }


        //region Database CRUD
        public void insertData(String[] sWord)
        {
            try{
                SQLiteDatabase dbb = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                for(int i=0;i<sWord.length;i++) {
                    contentValues.put(myDbHelper.CONTENT_WORD, sWord[i]);
                    dbb.insert(myDbHelper.TABLE_NAME, null, contentValues);
                }

            }catch(Exception ex) {
                Log.e("Error in insertion", ex.toString());
            }
        }

        public String getData()
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String[] columns = {myDbHelper.CONTENT_ID,myDbHelper.CONTENT_WORD};
            Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                int cid =cursor.getInt(cursor.getColumnIndex(myDbHelper.CONTENT_ID));
                String name =cursor.getString(cursor.getColumnIndex(myDbHelper.CONTENT_WORD));
                buffer.append(cid+ "   " + name +" \n");
            }
            return buffer.toString();
        }
    //endregion


        //region Database Creation
    static class myDbHelper extends SQLiteOpenHelper
        {
            private static final String DATABASE_NAME = "MOS";    // Database Name
            private static final String TABLE_NAME = "MOS_Data";   // Table Name
            private static final int DATABASE_Version = 1;    // Database Version
            private static final String CONTENT_ID="_id";     // Column I (Primary Key)
            private static final String CONTENT_WORD = "NAME";    //Column II
            private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                    " ("+CONTENT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CONTENT_WORD+" VARCHAR(255));";
            private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
            private Context context;

            public myDbHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_Version);
                this.context=context;
            }

            public void onCreate(SQLiteDatabase db) {

                try {
                    db.execSQL(CREATE_TABLE);
                } catch (Exception e) {
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                try {

                    db.execSQL(DROP_TABLE);
                    onCreate(db);
                }catch (Exception e) {
                }
            }
        }
    //endregion

}
