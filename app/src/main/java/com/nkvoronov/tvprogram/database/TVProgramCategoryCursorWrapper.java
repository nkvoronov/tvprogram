package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvprogram.TVProgramCategory;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.CategoryTable;

public class TVProgramCategoryCursorWrapper extends CursorWrapper {

    public TVProgramCategoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVProgramCategory getCategory() {
        int id = getInt(getColumnIndex(CategoryTable.Cols.ID));
        String name = getString(getColumnIndex(CategoryTable.Cols.NAME));
        String dictionary = getString(getColumnIndex(CategoryTable.Cols.DICTIONARY));
        String color = getString(getColumnIndex(CategoryTable.Cols.COLOR));

        TVProgramCategory category = new TVProgramCategory(id, name, dictionary, color);
        return category;
    }
}
