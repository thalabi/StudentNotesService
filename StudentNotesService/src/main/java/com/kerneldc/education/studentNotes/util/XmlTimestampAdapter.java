package com.kerneldc.education.studentNotes.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XmlTimestampAdapter extends XmlAdapter<String, Timestamp> {

	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Override
	public Timestamp unmarshal(String v) throws Exception {
		synchronized (timestampFormat) {
            return new Timestamp(timestampFormat.parse(v).getTime());
        }
	}

	@Override
	public String marshal(Timestamp v) throws Exception {
		synchronized (timestampFormat) {
            return timestampFormat.format(v);
        }
	}

}
