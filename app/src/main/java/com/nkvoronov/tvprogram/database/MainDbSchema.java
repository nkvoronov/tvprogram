package com.nkvoronov.tvprogram.database;

public class MainDbSchema {
    public static Object ConfigsTable;
    public static Object ChannelsTable;
    public static Object CategoryTable;
    public static Object SchedulesTable;
    public static Object DescriptionTable;
    public static Object ChannelsFavoritesTable;
    public static Object SchedulesFavoritesTable;
    public static Object ScheduleDescriptionTable;

    public static final class ConfigsTable {
        public static final String TABLE_NAME = "configs";

        public static final class Cols {
            public static final String ID = "id";
            public static final String COUNT_DAYS = "count_days";
            public static final String FULL_DESC = "full_desc";
        }
    }

    public static final class ChannelsTable {
        public static final String TABLE_NAME = "channels";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL = "channel";
            public static final String NAME = "name";
            public static final String ICON = "icon";
            public static final String LANG = "lang";
            public static final String UPDATED = "updated";
            public static final String FAVORITE = "favorite";
        }
    }

    public static final class ChannelsFavoritesTable {
        public static final String TABLE_NAME = "favorites_channels";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL = "channel";
        }
    }

    public static final class CategoryTable {
        public static final String TABLE_NAME = "category";

        public static final class Cols {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String DICTIONARY = "dictionary";
        }
    }

    public static final class DescriptionTable {
        public static final String TABLE_NAME = "description";

        public static final class Cols {
            public static final String ID = "id";
            public static final String DESCRIPTION = "description";
            public static final String IMAGE = "image";
            public static final String GENRES = "genres";
            public static final String DIRECTORS = "directors";
            public static final String ACTORS = "actors";
            public static final String COUNTRY = "country";
            public static final String YEAR = "year";
            public static final String RATING = "rating";
        }
    }

    public static final class ScheduleDescriptionTable {
        public static final String TABLE_NAME = "schedule_description";

        public static final class Cols {
            public static final String ID = "id";
            public static final String SCHEDULE = "schedule";
            public static final String DESCRIPTION = "description";
        }
    }

    public static final class SchedulesTable {
        public static final String TABLE_NAME = "schedule";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL = "channel";
            public static final String NAME = "name";
            public static final String FAVORITE_CHANNEL = "favorite_channel";
            public static final String CATEGORY = "category";
            public static final String STARTING = "starting";
            public static final String ENDING = "ending";
            public static final String TITLE = "title";
            public static final String TIME_TYPE = "time_type";
            public static final String FAVORITE = "favorite";
        }
    }

    public static final class SchedulesFavoritesTable {
        public static final String TABLE_NAME = "favorites_schedule";

        public static final class Cols {
            public static final String ID = "id";
            public static final String SCHEDULE = "schedule";
        }
    }
}
