package com.mikroskil.android.qattend.db.model;

import android.content.ContentValues;

import com.mikroskil.android.qattend.db.Contract;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;

@ParseClassName("Ticket")
public class ParseTicket extends ParseObject {

    public String getParticipant() {
        return getParseUser(Contract.Ticket.COL_PARTICIPANT).getObjectId();
    }

    public void setParticipant(ParseMember member) {
        put(Contract.Ticket.COL_PARTICIPANT, member);
    }

    public String getParticipateTo() {
        return getParseObject(Contract.Ticket.COL_PARTICIPATE_TO).getObjectId();
    }

    public void setParticipateTo(ParseEvent event) {
        put(Contract.Ticket.COL_PARTICIPATE_TO, event);
    }

    public boolean isVerified() {
        return getBoolean(Contract.Ticket.COL_VERIFIED);
    }

    public void setVerified(boolean verified) {
        put(Contract.Ticket.COL_VERIFIED, verified);
    }

    public ContentValues getContentValues() {
        SimpleDateFormat formatter = new SimpleDateFormat(Contract.DATE_TIME_FORMAT);
        ContentValues values = new ContentValues();
        if (getObjectId() != null) values.put(Contract.Ticket.COL_OBJ_ID, getObjectId());
        values.put(Contract.Ticket.COL_PARTICIPANT, getParticipant());
        values.put(Contract.Ticket.COL_PARTICIPATE_TO, getParticipateTo());
        values.put(Contract.Ticket.COL_VERIFIED, isVerified());
        if (getCreatedAt() != null) values.put(Contract.Event.COL_CREATED_AT, formatter.format(getCreatedAt()));
        if (getUpdatedAt() != null) values.put(Contract.Event.COL_UPDATED_AT, formatter.format(getUpdatedAt()));
        return values;
    }

}
