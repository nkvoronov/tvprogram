package com.nkvoronov.tvprogram.tvprogram;

import java.util.List;
import java.util.ArrayList;
import android.content.ContentValues;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import com.nkvoronov.tvprogram.database.TVCategoryCursorWrapper;

public class TVProgramCategoriesList {
    private List<TVProgramCategory> mData;
    private TVProgramDataSource mDataSource;

    public TVProgramCategoriesList(TVProgramDataSource dataSource) {
        mData = new ArrayList<>();
        mDataSource = dataSource;
    }

    public List<TVProgramCategory> getData() {
        return mData;
    }

    public TVProgramCategory get(int position) {
        return getData().get(position);
    }

    public TVProgramCategory getForId(int id) {
        TVProgramCategory programCategory = null;
        for (int i = 0; i < size(); i++) {
            if (get(i).getId() == id) {
                programCategory = get(i);
                break;
            }
        }
        return programCategory;
    }

    public int size() {
        return getData().size();
    }

    public void clear() {
        getData().clear();
    }

    public void add(TVProgramCategory programCategory) {
        getData().add(programCategory);
    }

    public void loadFromDB() {
        clear();

        TVCategoryCursorWrapper cursor = new TVCategoryCursorWrapper(mDataSource.getDatabase().query(CategoryTable.TABLE_NAME,
                null,
                CategoryTable.Cols.ID + ">0",
                null,
                null,
                null,
                CategoryTable.Cols.ID
        ));
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TVProgramCategory category = cursor.getCategory();
                add(category);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    public void saveToDB() {
        for (TVProgramCategory programCategory : getData()) {
            mDataSource.getDatabase().insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(programCategory));
        }
    }

    public static ContentValues getContentProgramCategoryValues(TVProgramCategory category) {
        ContentValues values = new ContentValues();
        values.put(CategoryTable.Cols.ID, category.getId());
        values.put(CategoryTable.Cols.NAME, category.getName());
        values.put(CategoryTable.Cols.DICTIONARY, category.getDictionary());
        values.put(CategoryTable.Cols.COLOR, category.getColor());
        return values;
    }
}
