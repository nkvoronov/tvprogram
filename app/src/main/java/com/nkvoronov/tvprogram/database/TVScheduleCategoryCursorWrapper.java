package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleCategory;
import com.nkvoronov.tvprogram.database.MainDbSchema.CategoryTable;

public class TVScheduleCategoryCursorWrapper extends CursorWrapper {

    public TVScheduleCategoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVScheduleCategory getCategory() {
        int id = getInt(getColumnIndex(CategoryTable.Cols.ID));
        String name = getString(getColumnIndex(CategoryTable.Cols.NAME));
        String dictionary = getString(getColumnIndex(CategoryTable.Cols.DICTIONARY));

        TVScheduleCategory category = new TVScheduleCategory(id, name, dictionary);
        return category;
    }
}
