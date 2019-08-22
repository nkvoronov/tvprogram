package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import com.nkvoronov.tvprogram.tvprogram.TVProgramDescription;

public class TVProgramDescriptionCursorWrapper extends CursorWrapper {

    public TVProgramDescriptionCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVProgramDescription getDescription() {
        int id = getInt(getColumnIndex(DescriptionTable.Cols.ID));
        int program = getInt(getColumnIndex(ScheduleDescriptionTable.Cols.SCHEDULE));
        String shortDesc = getString(getColumnIndex(DescriptionTable.Cols.SHORT));
        String fullDesc = getString(getColumnIndex(DescriptionTable.Cols.DESCRIPTION));
        String image = getString(getColumnIndex(DescriptionTable.Cols.IMAGE));
        String genres = getString(getColumnIndex(DescriptionTable.Cols.GENRES));
        String directors = getString(getColumnIndex(DescriptionTable.Cols.DIRECTORS));
        String actors = getString(getColumnIndex(DescriptionTable.Cols.ACTORS));
        String country = getString(getColumnIndex(DescriptionTable.Cols.COUNTRY));
        String year = getString(getColumnIndex(DescriptionTable.Cols.YEAR));
        String rating = getString(getColumnIndex(DescriptionTable.Cols.RATING));

        TVProgramDescription description = new TVProgramDescription(id, program, shortDesc);
        description.setDescription(fullDesc);
        description.setImage(image);
        description.setGenres(genres);
        description.setDirectors(directors);
        description.setActors(actors);
        description.setCountry(country);
        description.setYear(year);
        description.setRating(rating);
        return description;
    }
}
