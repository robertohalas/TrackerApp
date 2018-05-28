package smart.tuke.sk.makac.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class UsersDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "account.db";
    public static final String CONTACTS_TABLE_NAME = "users";
    private static final int DATABASE_VERSION = 1;
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_AVATAR = "avatar";
    public static final String CONTACTS_COLUMN_CURRENT = "current";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_WEIGHT = "weight";

    public UsersDatabase(Context context)
    {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table users " +
                        "(id integer primary key, name text, avatar blob, current integer, weight text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean insertUser  (String name, String weight, byte[] avatar)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("current", 1);

        db.update("users", contentValues, null, null);

        contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("weight", weight);
        contentValues.put("current", 1);
        contentValues.put("avatar", avatar);
        db.insert("users", null, contentValues);
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users where current = 1", null );
        return res;
    }

    public Cursor getDataId(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users where id = ?", new String[] { Integer.toString(id) } );
        return res;
    }

    public Cursor getUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateUser (Integer id, String name, String weight, byte[] avatar)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("weight", weight);
        contentValues.put("avatar", avatar);

        db.update("users", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean changeCurrent (Integer id){

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("current", 0);
            db.update("users", contentValues, null, null);
            contentValues = new ContentValues();
            contentValues.put("current", 1);
            db.update("users", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteUser (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("users",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getNames()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select name from users", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
