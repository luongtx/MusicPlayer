package com.example.mymusicapp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAccountHelper {
    public static final String KEY_ROWID ="_id";
    public static final String KEY_NAME ="person_name";
    public static final String KEY_PASS ="_pass";


    private static final String DATABASE_NAME = "AccountDB";
    private static final String DATABASE_TABLE = "ContactsTable";
    private static final int DATABASE_VERSION = 1;

    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public DBAccountHelper(Context context)
    {
        ourContext = context;
    }

    private class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper (Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            /*
            CREATE TABLE ContactsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT,
                person_name TEXT NOT NULL, _cell TEXT NOT NULL;
             */
            String sqlCode = "CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_NAME + " TEXT NOT NULL, " +
                    KEY_PASS + " TEXT NOT NULL);";
            db.execSQL(sqlCode);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }

    }
    public DBAccountHelper open() throws SQLException
    {
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;

    }
    public void close()
    {
        ourHelper.close();
    }
    public long createEntry(String name, String pass)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_PASS,pass);
        return ourDatabase.insert(DATABASE_TABLE,null, cv);
    }
    public  long updateEntry(String name, String pass)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_PASS,pass);
        return  ourDatabase.update(DATABASE_TABLE, cv, KEY_NAME + "=?", new String[]{name});
    }
    public boolean CheckSignUp(String name)
    {
        String [] columns = new String[] {KEY_ROWID, KEY_NAME, KEY_PASS};
        Cursor c= ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
                null, null);

        Boolean check =true;
        int iRowID = c.getColumnIndex(KEY_ROWID);
        int iName = c.getColumnIndex(KEY_NAME);
        int iPass = c.getColumnIndex(KEY_PASS);

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            if(c.getString(iName).equals(name))
            {
                check = false;
                break;
            }
        }

        return check;
    }

    public boolean Login(String name, String pass)
    {
        String [] columns = new String[] {KEY_ROWID, KEY_NAME, KEY_PASS};
        Cursor c= ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
                null, null);

        Boolean login =false;
        int iRowID = c.getColumnIndex(KEY_ROWID);
        int iName = c.getColumnIndex(KEY_NAME);
        int iPass = c.getColumnIndex(KEY_PASS);

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            if(c.getString(iName).equals(name) && c.getString(iPass).equals(pass))
            {
                login = true;
            }
        }

        return login;
    }
}

