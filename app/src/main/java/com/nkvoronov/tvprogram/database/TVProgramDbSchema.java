package com.nkvoronov.tvprogram.database;

public class TVProgramDbSchema {
    public static Object ConfigsTable;
    public static Object ChannelsTable;
    public static Object ChannelsFavoritesTable;
    public static Object SchedulesTable;
    public static Object ChannelsAllTable;

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
            public static final String CHANNEL_INDEX = "channel_index";
            public static final String NAME = "channel_name";
            public static final String ICON = "channel_icon";
            public static final String LANG = "channel_lang";
            public static final String UPD_CHANNEL = "upd_channel";
        }
    }

    public static final class ChannelsFavoritesTable {
        public static final String TABLE_NAME = "fav_channels";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL_INDEX = "channel_index";
        }
    }

    public static final class SchedulesTable {
        public static final String TABLE_NAME = "schedule";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL = "channel";
            public static final String CATEGORY = "category";
            public static final String START = "starting";
            public static final String END = "ending";
            public static final String TITLE = "title";
            public static final String TIME_TYPE = "timetype";
            public static final String FAVORITE = "favorite";
        }
    }

    public static final class ChannelsAllTable {
        public static final String TABLE_NAME = "tv_channels";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL_INDEX = "cindex";
            public static final String CHANNEL_NAME = "cname";
            public static final String CHANNEL_ICON = "cicon";
            public static final String LANG = "clang";
            public static final String FAVORITE = "favorite";
        }
    }
}
