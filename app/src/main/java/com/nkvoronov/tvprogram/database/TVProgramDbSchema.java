package com.nkvoronov.tvprogram.database;

public class TVProgramDbSchema {
    public static Object ConfigsTable;
    public static Object ChannelsTable;
    public static Object CategoryTable;
    public static Object SchedulesTable;
    public static Object ChannelsFavoritesTable;
    public static Object SchedulesFavoritesTable;

    public static final class ConfigsTable {
        public static final String TABLE_NAME = "configs";

        public static final class Cols {
            public static final String ID = "id";
            public static final String COUNT_DAYS = "count_days";
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
            public static final String COLOR = "color";
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
            public static final String START = "starting";
            public static final String END = "ending";
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
