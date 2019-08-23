package com.nkvoronov.tvprogram.tvschedule;

import java.util.List;
import java.util.ArrayList;
import android.content.ContentValues;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import com.nkvoronov.tvprogram.database.TVScheduleCategoryCursorWrapper;

public class TVScheduleCategoriesList {
    private List<TVScheduleCategory> mData;
    private MainDataSource mDataSource;

    public TVScheduleCategoriesList(MainDataSource dataSource) {
        mData = new ArrayList<>();
        mDataSource = dataSource;
    }

    public List<TVScheduleCategory> getData() {
        return mData;
    }

    public TVScheduleCategory get(int position) {
        return getData().get(position);
    }

    public TVScheduleCategory getForId(int id) {
        TVScheduleCategory programCategory = null;
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

    public void add(TVScheduleCategory programCategory) {
        getData().add(programCategory);
    }

    public void loadFromDB() {
        clear();

        TVScheduleCategoryCursorWrapper cursor = new TVScheduleCategoryCursorWrapper(mDataSource.getDatabase().query(CategoryTable.TABLE_NAME,
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
                TVScheduleCategory category = cursor.getCategory();
                add(category);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    public void saveToDB() {
        for (TVScheduleCategory programCategory : getData()) {
            mDataSource.getDatabase().insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(programCategory));
        }
    }

    public static ContentValues getContentScheduleCategoryValues(TVScheduleCategory category) {
        ContentValues values = new ContentValues();
        values.put(CategoryTable.Cols.ID, category.getId());
        values.put(CategoryTable.Cols.NAME, category.getName());
        values.put(CategoryTable.Cols.DICTIONARY, category.getDictionary());
        return values;
    }
}
