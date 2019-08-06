package com.nkvoronov.tvprogram.database;

public class TVProgramDbSchema {
    public static Object ConfigsTable;
    public static Object ChannelsTable;
    public static Object ChannelsFavTable;

    public static final class ConfigsTable {
        public static final String TABLE_NAME = "configs";

        public static final class Cols {
            public static final String ID = "id";
            public static final String TYPE = "type";
            public static final String NAME = "name";
            public static final String VALUE = "value";
        }
    }

    public static final class ChannelsTable {
        public static final String TABLE_NAME = "channels";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL_INDEX = "channel_index";
            public static final String NAME = "original_name";
            public static final String ICON = "channel_icon";
            public static final String UPD_CHANNEL = "upd_channel";
        }
    }

    public static final class ChannelsFavTable {
        public static final String TABLE_NAME = "fav_channels";

        public static final class Cols {
            public static final String ID = "id";
            public static final String ID_CHANNEL = "id_channel";
            public static final String NAME = "user_name";
            public static final String CORRECTION = "correction";
            public static final String UPD_PROGRAM = "upd_program";
        }
    }

    public static final class ChannelsTvTable {
        public static final String TABLE_NAME = "tv_channels";

        public static final class Cols {
            public static final String ID = "id";
            public static final String CHANNEL_INDEX = "channel_index";
            public static final String ORIGINAL_NAME = "original_name";
            public static final String USER_NAME = "user_name";
            public static final String CHANNEL_ICON = "channel_icon";
            public static final String CORRECTION = "correction";
            public static final String FAVORITE = "favorite";
            public static final String UPD_PROGRAM = "upd_program";
        }
    }
}
