package data;

import android.provider.BaseColumns;

/**
 * Created by John on 07.12.2016.
 */

public class CiceronContract {

    private CiceronContract () {
    }

    public static final class List implements BaseColumns {
        public static final String TABLE_NAME = "ListMain";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PLACE = "place";
        public final static String COLUMN_DESCRIBE = "describe";
    }

    public static final class Task implements BaseColumns {
        public static final String TABLE_NAME = "Tasks";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_LIST_ID = "list_id";
        public final static String COLUMN_TASK = "task";
        public final static String COLUMN_DONE = "done";
    }

    public static final class Place implements BaseColumns {
        public static final String TABLE_NAME = "Places";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PLACE = "place";
        public final static String COLUMN_LATITUDE = "latitude";
        public final static String COLUMN_LONGITUDE = "longitude";
    }
}
