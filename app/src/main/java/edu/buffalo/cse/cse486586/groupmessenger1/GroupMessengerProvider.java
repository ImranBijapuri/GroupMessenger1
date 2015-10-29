package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;


import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 *
 * Please read:
 *
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 *
 * before you start to get yourself familiarized with ContentProvider.
 *
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 *
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

    private static final String AUTHORITY = "edu.buffalo.cse.cse486586.groupmessenger1.provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY );

    public static MySQLiteHelper db = null;

    //private android.content.Context context = this.context;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        db.insert(values);


        //db.setLocale(Locale.getDefault());
        //db.setLockingEnabled(true);



      /*  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setMessage("asa");
        AlertDialog alert11 = alertDialogBuilder.create();
        alert11.show();*/




        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {

        Log.d("in oncreate","0");
        db = new MySQLiteHelper(getContext());
        db.getWritableDatabase();
        Log.d("in aftercreate","0");
        // SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.buffalo.cse.cse486586.groupmessenger1/databases/test2.db",null);
        // db.execSQL("CREATE TABLE IF NOT EXISTS testtable(Username VARCHAR,Password VARCHAR);");
        // db.execSQL("INSERT INTO testtable VALUES('admin','admin');");
        // db.setVersion(1);

        // If you need to perform any one-time initialization task, please do it here.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        db = new MySQLiteHelper(getContext());
        Cursor c = db.query(selection);
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        Log.v("query", selection);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    public static class  MySQLiteHelper extends SQLiteOpenHelper {

        // Database Version
        private static final int DATABASE_VERSION = 1;
        // Database Name
        private static final String DATABASE_NAME = "Messenger.db";

        public MySQLiteHelper(Context context) {
            // Log.v("sa","Should be Created next step");
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.v("sa","Should be Created now");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // SQL statement to create messenger table
            String CREATE_MESSENGER_TABLE = "CREATE TABLE messenger ( " +
                    "key varchar(500) UNIQUE, " +
                    "value varchar(500))";

            // create messenger table
            db.execSQL(CREATE_MESSENGER_TABLE);
            // db.close();
        }

        public void insert(ContentValues values){
            Time now = new Time();
            now.setToNow();
            Log.v("Time" , now.toString());
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT or replace INTO messenger(key,value) VALUES('"+values.get("key")+"','"+values.get("value")+"');");


            long num = DatabaseUtils.queryNumEntries(db, "messenger");
            String count= Long.toString(num);
            Log.i("rowcount", count);
            // db.close();

        }



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop MESSENGER  table if existed
            db.execSQL("DROP TABLE IF EXISTS messenger");
            this.onCreate(db);
        }

        public Cursor query(String selection) {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery("SELECT key,value FROM messenger where key='"+selection + "'", null);

            // db.close();
            return c;

        }





    }
}





