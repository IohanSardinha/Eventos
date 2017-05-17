package br.com.sardinha.iohan.eventos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iohan.soares on 17/05/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "eventos";
    // Contacts table name
    private static final String TABLE_NAME = "tabela_eventos";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITULO = "titulo";
    private static final String KEY_DATA = "data";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CRIA_TABELA = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)",TABLE_NAME,KEY_ID,KEY_TITULO,KEY_DATA);
        db.execSQL(CRIA_TABELA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLE_NAME));
        onCreate(db);
    }

    public void addShop(Evento evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITULO, evento.getTitulo());
        values.put(KEY_DATA, evento.getDataInicio());
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
    //http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/
    /*public Evento getShop(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID, KEY_TITULO, KEY_TITULO }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Evento contact = new Shop(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));// return shop
        return contact;
    }*/

}
