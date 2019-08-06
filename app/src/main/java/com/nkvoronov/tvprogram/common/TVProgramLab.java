package com.nkvoronov.tvprogram.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;

public class TVProgramLab {
    private static TVProgramLab sTVProgramLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public TVProgramLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TVProgramBaseHelper(mContext).getWritableDatabase();
    }

    public static TVProgramLab get(Context context) {
        if (sTVProgramLab == null) {
            sTVProgramLab = new TVProgramLab(context);
        }
        return sTVProgramLab;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }
}
