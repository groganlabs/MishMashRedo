package com.groganlabs.mishmash;

import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class MishMashDB extends SQLiteOpenHelper {
	public static final int latestVersion = 1;
	
	public static final int JUMBLE_MASK =1;
	public static final int CRYPTO_MASK = 2;
	public static final int DROP_MASK = 4;
	
	public static final String DB_NAME = "mishMashDb";
	
	private static final String GAME_TABLE = "phrase";
	private static final String GAME_TABLE_ID = "phrase_id";
	private static final String COL_COMPLETED = "completed";
	private static final String COL_ELIGABLE = "eligable_for";
	private static final String COL_GAME = "phrase";
	
	private static final String PACK_TABLE = "game_pack";
	private static final String PACK_ID	= "id";
	private static final String PACK_PURCHASED = "purchased";
	
	private static final String ACTIVE_GAME_TABLE = "active_game";
	private static final String ACTIVE_GAME_ID = "id";
	private static final String ACTIVE_GAME_GAME_ID = "game_id";
	private static final String ACTIVE_GAME_GAME = "game_mask";
	private static final String ACTIVE_GAME_ANSWER = "user_answer";
	private static final String ACTIVE_GAME_SOLUTION = "solution";
	private static final String ACTIVE_GAME_PUZZLE = "puzzle";
	
	private static final String GAME_TABLE_CREATE = "create table " + GAME_TABLE + 
			"(" + GAME_TABLE_ID + " integer primary key, " +
			COL_ELIGABLE + " integer default 7, " + 
			COL_COMPLETED + " integer default 0, " +
			COL_GAME + " varchar);";
	
	private static final String PACK_TABLE_CREATE = "create table " + PACK_TABLE +
			"(" + PACK_ID + " integer primary key, " +
			PACK_PURCHASED + " integer);";
	
	private static final String ACTIVE_TABLE_CREATE = "create table " + ACTIVE_GAME_TABLE +
			"(" + ACTIVE_GAME_ID + " integer primary key, " +
			ACTIVE_GAME_GAME_ID + " integer, " + 
			ACTIVE_GAME_GAME + " varchar, " +
			ACTIVE_GAME_ANSWER + " varchar, " +
			ACTIVE_GAME_SOLUTION + " varchar, " + 
			ACTIVE_GAME_PUZZLE + " varchar);";
	
	private Context mContext;

	public MishMashDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(GAME_TABLE_CREATE);
		db.execSQL(PACK_TABLE_CREATE);
		db.execSQL(ACTIVE_TABLE_CREATE);
		
		String[] games = mContext.getResources().getStringArray(R.array.defaultSolutions);
		ContentValues values = new ContentValues();
		
		for(int ii = 0; ii < games.length; ii++) {
			values.put(COL_GAME, games[ii]);
			db.insert(GAME_TABLE, null, values);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/**
	 * gets a game, any game. Uses the app settings to determine
	 * game reuse options
	 * @param mask Any of the static MASK vars available
	 * @return game, or null
	 */
	public boolean getGame(Game game) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		//TODO: use the game's properties to determine whether we need a random game or pack
		// also, need to figure out which packs to use
		SQLiteDatabase db = getWritableDatabase();
		String[] cols = {COL_GAME, GAME_TABLE_ID};
		
		StringBuilder where = new StringBuilder();
		where.append("(" + COL_ELIGABLE + " & " + game.getGameType() + ") = " + game.getGameType());
		where.append(" AND " + GAME_TABLE_ID + " NOT IN (SELECT " + ACTIVE_GAME_GAME_ID + " FROM " +
				ACTIVE_GAME_TABLE + ")");
		//if we aren't reusing or replaying games, add COL_COMPLETED  = 0
		if(!sharedPref.getBoolean("PREF_REUSE", true) && !sharedPref.getBoolean("PREF_REPLAY", false))
			where.append(" AND " + COL_COMPLETED + " = 0");
		//else if we are reusing but not replaying games,
		else if(sharedPref.getBoolean("PREF_REUSE", true) && !sharedPref.getBoolean("PREF_REPLAY", false))
			where.append(" AND (" + COL_COMPLETED + " & " + game.getGameType() + ") != " + game.getGameType());
		//if we're reusing and replaying, it doesn't matter 
		
		Log.d("db", "Where: "+where.toString());
		Cursor res = db.query(GAME_TABLE, cols, where.toString(), null, null, null, null);
		if(res.getCount() == 0)
			return false;
		
		Random rand = new Random();
		res.moveToPosition(rand.nextInt(res.getCount()));
		
		game.setSolution(res.getString(0));
		game.setGameId(res.getInt(1));
		
		return true;
	}
	
	public void removeActiveGame(Game game) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(ACTIVE_GAME_TABLE, ACTIVE_GAME_GAME_ID + " = " + game.getGameId(), null);
	}

	public void markGameWon(Game game) {
		SQLiteDatabase db = getWritableDatabase();
		String query = "update " + GAME_TABLE + " set " + COL_COMPLETED + " = " + 
				"(" + COL_COMPLETED + " | " + game.getGameType() + ") " + 
				" where " + GAME_TABLE_ID + " = " + game.getGameId();
		Log.d("db", "SQL: " + query);
		db.execSQL(query);
		
		//for debugging
		/*String[] columns = {GAME_TABLE_ID, COL_GAME, COL_COMPLETED};
		Cursor res = db.query(GAME_TABLE, columns, where, null, null, null, null);
		if(res.getCount() > 0) {
			res.moveToFirst();
			Log.d("db", "gameId: " + res.getString(0));
			Log.d("db", "completed: " + res.getString(2));
		}*/
		
	}
}
