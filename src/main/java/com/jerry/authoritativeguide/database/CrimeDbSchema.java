package com.jerry.authoritativeguide.database;

/**
 * Created by Jerry on 2017/1/5.
 */

public class CrimeDbSchema {

    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String SUSPECT_PHONE = "suspectPhone";

        }
    }
}
