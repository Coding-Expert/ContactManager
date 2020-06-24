package com.basicphones.contacts;

import android.database.CursorWrapper;
import android.database.Cursor;
import com.basicphones.contacts.database.ContactDbSchema.PersonTable;
import com.basicphones.contacts.database.ContactDbSchema.PhoneTable;
import com.basicphones.contacts.database.ContactDbSchema.EmailTable;


public class ContactCursorWrapper extends CursorWrapper {

    public ContactCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getContactPersonUUID() {
        return getString(getColumnIndex(PersonTable.Cols.UUID));

    }

    public String getContactPersonName() {
        return getString(getColumnIndex(PersonTable.Cols.NAME));
    }

    public String getContactPhoneUUID() {
        return getString(getColumnIndex(PhoneTable.Cols.UUID));
    }

    public String getContactPhonePhone() {
        return getString(getColumnIndex(PhoneTable.Cols.PHONE));
    }

    public String getContactEmailUUID() {
        return getString(getColumnIndex(EmailTable.Cols.UUID));
    }

    public String getContactEmailEmail() {
        return getString(getColumnIndex(EmailTable.Cols.EMAIL));
    }

}
