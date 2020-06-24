package com.basicphones.contacts.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.basicphones.contacts.database.GroupDbSchema.GroupTable;


public class GroupBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "groupBase.db";

    public GroupBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + GroupTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                GroupTable.Cols.UUID + ", " +
                GroupTable.Cols.NAME +
                ")"
        );


        db.execSQL("create table " + GroupDbSchema.ContactGroupTable.NAME + "(" +
                " _id integer primary key not null, " +
                GroupDbSchema.ContactGroupTable.Cols.UUID + ", " +
                GroupDbSchema.ContactGroupTable.Cols.ContactID + ", " +
                "foreign key (" + GroupDbSchema.ContactGroupTable.Cols.UUID + ") " +
                "references " + GroupDbSchema.GroupTable.NAME + "(" + GroupDbSchema.GroupTable.Cols.UUID + ") " +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
