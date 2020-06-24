package com.basicphones.contacts.database;

public class GroupDbSchema {

    public static final class GroupTable {
        public static final String NAME = "person_group";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
        }
    }

    public static final class ContactGroupTable {

        public static final String NAME = "contacts";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String ContactID = "contact";
        }
    }
}
