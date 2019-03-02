package com.mediaplayer.strings.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.mediaplayer.strings.beans.Track;
import com.mediaplayer.strings.utilities.Utilities;

import java.util.ArrayList;
import java.util.Iterator;

import static com.mediaplayer.strings.dao.MediaPlayerContract.DATABASE_NAME;
import static com.mediaplayer.strings.dao.MediaPlayerContract.DATABASE_VERSION;
import static com.mediaplayer.strings.utilities.MediaLibraryManager.populateTrackInfoList;
import static com.mediaplayer.strings.utilities.MediaPlayerConstants.LOG_TAG_EXCEPTION;
import static com.mediaplayer.strings.utilities.MediaPlayerConstants.LOG_TAG_SQL;
import static com.mediaplayer.strings.utilities.SQLConstants.*;
import static java.lang.String.valueOf;

class MediaPlayerDBHelper extends SQLiteOpenHelper {
    private Context context;

    MediaPlayerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteStatement insertStmt = null;
        ArrayList<Track> trackList;
        Iterator<Track> trackIterator;
        Track track;
        int c, tracksInserted = 0;

        try {
            Log.d(LOG_TAG_SQL, SQL_CREATE_TRACKS);
            db.execSQL(SQL_CREATE_TRACKS);

            Log.d(LOG_TAG_SQL, SQL_CREATE_PLAYLISTS);
            db.execSQL(SQL_CREATE_PLAYLISTS);

            Log.d(LOG_TAG_SQL, SQL_CREATE_PLAYLIST_DETAIL);
            db.execSQL(SQL_CREATE_PLAYLIST_DETAIL);

            //Creating default playlist 'Favourites'
            insertStmt = db.compileStatement(SQL_INSERT_PLAYLIST);

            insertStmt.bindLong(1, PLAYLIST_INDEX_FAVOURITES);
            insertStmt.bindString(2, PLAYLIST_TITLE_FAVOURITES);
            insertStmt.bindLong(3, ZERO);
            insertStmt.bindLong(4, ZERO);
            insertStmt.bindString(5, Utilities.getCurrentDate());

            Log.d(LOG_TAG_SQL, insertStmt.toString());
            insertStmt.execute();

            //Fetching tracks from storage
            trackList = populateTrackInfoList(context);

            //Inserting tracks in table 'Tracks'
            if(trackList != null && !trackList.isEmpty()) {
                insertStmt = db.compileStatement(SQL_INSERT_TRACK);
                trackIterator = trackList.iterator();

                while(trackIterator.hasNext()) {
                    track = trackIterator.next();
                    c = ONE;

                    insertStmt.bindString(c++, track.getTrackTitle());
                    insertStmt.bindLong(c++, track.getTrackIndex());
                    insertStmt.bindString(c++, track.getFileName());
                    insertStmt.bindLong(c++, track.getTrackDuration());
                    insertStmt.bindLong(c++, track.getFileSize());
                    insertStmt.bindString(c++, track.getAlbumName());
                    insertStmt.bindString(c++, track.getArtistName());
                    insertStmt.bindBlob(c++, track.getAlbumArt());
                    insertStmt.bindString(c++, track.getTrackLocation());
                    insertStmt.bindLong(c++, track.isFavSw());
                    insertStmt.bindString(c, Utilities.getCurrentDate());

                    Log.d(LOG_TAG_SQL, insertStmt.toString());

                    try {
                        insertStmt.executeInsert();
                        ++tracksInserted;
                    } catch(SQLException sqle) {
                        Log.e(LOG_TAG_EXCEPTION, sqle.getMessage());
                        //Utilities.reportCrash(sqle);
                    }
                }

                Log.d("Tracks added to library", valueOf(tracksInserted));
            }
        } catch(Exception e) {
            Log.e(LOG_TAG_EXCEPTION, e.getMessage());
            //Utilities.reportCrash(e);
        } finally {
            if(insertStmt != null) {
                insertStmt.close();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
