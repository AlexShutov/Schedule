package com.SFEDU.schedule_1;

import android.R.integer;

// описывает ошибки сохранения записи

public class ScheduleKeepReloadException extends Exception {

	
	
	private int m_RecordNo;
	private String m_Description;
	
	public ScheduleKeepReloadException(int recordNo, String desc) {
		SetRecordNo(recordNo);
		SetDescription(desc);
	}

	public void SetRecordNo(int recordNo) {
		m_RecordNo = recordNo;
	}
	
	public void SetDescription(String desc) {
		m_Description = desc;
	}
	
	public int GetRecordNo() { return m_RecordNo; }
	
	@Override
	public String getMessage() {
		
		StringBuilder sb = new StringBuilder();
		if (getCause() != null) {
			sb.append(getCause().getMessage());
		}
		 
		sb.append("Eror in record NO: " + m_RecordNo + " Description: " + m_Description);
		return sb.toString();
	}
	
	
}
