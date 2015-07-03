package com.SFEDU.schedule_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class Schedule4Week {

	public static final String TAG_WEEK_SCHEDULE = "WEEK_SCHEDULE";
	
	
	public static final String ms_Mon = "DAY_1";
	public static final String ms_Tue = "DAY_2";
	public static final String ms_Wed = "DAY_3";
	public static final String ms_Thu = "DAY_4";
	public static final String ms_Fri = "DAY_5";
	public static final String ms_Sat = "Day_6";
	public static final String ms_Sun = "Day_7";
	
	// для удобства
	private static List<String> ms_DayNames;
	static {
		ms_DayNames = new ArrayList<String>();
		ms_DayNames.add(ms_Mon);
		ms_DayNames.add(ms_Tue);
		ms_DayNames.add(ms_Wed);
		ms_DayNames.add(ms_Thu);
		ms_DayNames.add(ms_Fri);
		ms_DayNames.add(ms_Sat);
		ms_DayNames.add(ms_Sun);
	}
	
	
	public static List<String> GetDayNamesList() { return ms_DayNames; }
	
	public static Schedule4Week GetEmptySchedule() {
		if (m_emptySchedule == null)
			m_emptySchedule = new Schedule4Week();
		return m_emptySchedule;
	}
	
	// чтобы каждый раз не создавать
	private static Schedule4Week m_emptySchedule = null;
	
	
	// расписание на неделю  заполнено пустыми расписаниями
	// для каждого дня
	public Schedule4Week() {
		// вдруг появятся другие дни
		m_WeekSchedule = new HashMap<String, Schedule>();
			
		m_WeekSchedule.put(ms_Mon, new Schedule());
		m_WeekSchedule.put(ms_Tue, new Schedule());
		m_WeekSchedule.put(ms_Wed, new Schedule());
		m_WeekSchedule.put(ms_Thu, new Schedule());
		m_WeekSchedule.put(ms_Fri, new Schedule());
		m_WeekSchedule.put(ms_Sat, new Schedule());
		m_WeekSchedule.put(ms_Sun, new Schedule());
		
	}
		
	// аксессоры
	public void PutDaySchedule(String dayName, Schedule schedule) throws IllegalArgumentException 
	{
		if (schedule == null || !ms_DayNames.contains(dayName) ) {
			throw new IllegalArgumentException("Wrong day name");
		}
		
		m_WeekSchedule.remove(dayName);
		m_WeekSchedule.put(dayName, schedule);
	}
	
	public Schedule GetDaySchedule(String dayName) throws IllegalArgumentException
	{
		if (!ms_DayNames.contains(dayName)) {
			throw new IllegalArgumentException("Wrong day name");
		}
		Schedule s =  m_WeekSchedule.get(dayName);
		if (s == null) {
			s = new Schedule();
		}
		return s;
	}
	
	
	public void SetTag(String tag ) { m_Tag = tag; }
	public String GetTag() { return m_Tag; }
	
	// название расписания (имя папки сохранения)
	private String m_Tag;
	
	private Map<String, Schedule> m_WeekSchedule;
	
}
