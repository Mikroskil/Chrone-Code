package com.mikroskil.android.qattend.db.model;

import android.content.ContentValues;

import com.mikroskil.android.qattend.db.Contract;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;

@ParseClassName("Membership")
public class ParseMembership extends ParseObject {

    public ParseMembership() {}

    public String getApplicantFrom() {
        return getParseUser(Contract.Membership.COL_APPLICANT_FROM).getObjectId();
    }

    public void setApplicantFrom(ParseUser user) {
        put(Contract.Membership.COL_APPLICANT_FROM, user);
    }

    public String getApplyTo() {
        return getParseObject(Contract.Membership.COL_APPLY_TO).getObjectId();
    }

    public void setApplyTo(ParseOrganization org) {
        put(Contract.Membership.COL_APPLY_TO, org);
    }

    public boolean getApproved() {
        return getBoolean(Contract.Membership.COL_APPROVED);
    }

    public void setApproved(boolean approved) {
        put(Contract.Membership.COL_APPROVED, approved);
    }

    public ContentValues getContentValues() {
        SimpleDateFormat formatter = new SimpleDateFormat(Contract.DATE_TIME_FORMAT);
        ContentValues values = new ContentValues();
        values.put(Contract.Membership.COL_OBJ_ID, getObjectId());
        values.put(Contract.Membership.COL_APPLICANT_FROM, getApplicantFrom());
        values.put(Contract.Membership.COL_APPLY_TO, getApplyTo());
        values.put(Contract.Membership.COL_APPROVED, getApproved());
        values.put(Contract.Membership.COL_CREATED_AT, formatter.format(getCreatedAt()));
        values.put(Contract.Membership.COL_UPDATED_AT, formatter.format(getUpdatedAt()));
        return values;
    }

}
