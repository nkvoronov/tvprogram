package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleDescription;

public class TVScheduleDescriptionCursorWrapper extends CursorWrapper {

    public TVScheduleDescriptionCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVScheduleDescription getDescription() {
        int id = getInt(getColumnIndex(DescriptionTable.Cols.ID));
        int schedule = getInt(getColumnIndex(ScheduleDescriptionTable.Cols.SCHEDULE));
        String title = getString(getColumnIndex(DescriptionTable.Cols.TITLE));
        String desc = getString(getColumnIndex(DescriptionTable.Cols.DESCRIPTION));
        String image = getString(getColumnIndex(DescriptionTable.Cols.IMAGE));
        String genres = getString(getColumnIndex(DescriptionTable.Cols.GENRES));
        String directors = getString(getColumnIndex(DescriptionTable.Cols.DIRECTORS));
        String actors = getString(getColumnIndex(DescriptionTable.Cols.ACTORS));
        String country = getString(getColumnIndex(DescriptionTable.Cols.COUNTRY));
        String year = getString(getColumnIndex(DescriptionTable.Cols.YEAR));
        String rating = getString(getColumnIndex(DescriptionTable.Cols.RATING));
        String type = getString(getColumnIndex(DescriptionTable.Cols.TYPE));
        int catalog = getInt(getColumnIndex(DescriptionTable.Cols.CATALOG));

        TVScheduleDescription description = new TVScheduleDescription(id, schedule, desc);
        description.setTitle(title);
        description.setImage(image);
        description.setGenres(genres);
        description.setDirectors(directors);
        description.setActors(actors);
        description.setCountry(country);
        description.setYear(year);
        description.setRating(rating);
        description.setType(type);
        description.setIdCatalog(catalog);
        return description;
    }
}
