package com.SFEDU.schedule_1;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord.ScheduleRecordBuilder;

import android.R.integer;

public class Schedule 
{
	
	//содержит текстовые описания- ключи ячейки и значения
	public static class ScheduleRecord 
	{
		
		@SuppressWarnings("deprecation")
		protected ScheduleRecord() {
			m_BeginTime = new Date(0, 0, 0, 0, 0);
			m_EndTime = new Date(0, 0, 0, 0, 0);
		}
		
		// номер записи (урока)
		public static final String ms_RecordPosition = "RECORD_POSITION";		
		protected int m_RecordPosition;
		
		// время начала и конца
		public static final String ms_BeginTime = "RECORD_LESSON_BEGIN_TIME";
		protected Date m_BeginTime;
		
		public static final String ms_EndTime = "RECORD_LESSON_END_TIME";
		protected Date m_EndTime;
		
		// имя предмета
		public static final String ms_SubjectName = "RECORD_SUBJECT_NAME";
		protected String m_SubjectName;
		
		//ФИО преподавателя
		public static final String ms_TeacherFirstName = "RECORD_TEACHER_FIRST_NAME";
		protected String m_TeacherFirstName;
		
		public static final String ms_TeacherMiddleName = "RECORD_TEACHER_MIDDLE_NAME";
		protected String m_TeacherMiddleName;
		
		public static final String ms_TeacherLastName = "RECORD_TEACHER_LAST_NAME";
		protected String m_TeacherLastName;
		
		// тип урока (лек/сем)
		public static final String ms_LessonType = "RECORD_LESSON_TYPE";
		protected String m_LessonType;
		
		// описание комнаты (не инт)
		public static final String ms_RoomDescription = "RECORD_ROOM_DESCRIPTION";
		protected String m_RoomDescription;
		
		
		// аксесоры
		public int GetRecordPosition() { return m_RecordPosition; }
		public Date GetBeginTime() { return m_BeginTime; }
		public Date GetEndTime() { return m_EndTime; }
		@SuppressWarnings("deprecation")
		public int GetBeginHour() { return m_BeginTime.getHours(); }
		@SuppressWarnings("deprecation")
		public int GetBeginMinutes() { return m_BeginTime.getMinutes(); }
		@SuppressWarnings("deprecation")
		public int GetEndHour() { return m_EndTime.getHours(); }
		@SuppressWarnings("deprecation")
		public int GetEndMinutes() { return m_EndTime.getMinutes(); }
		
		
		public String GetSubjectName() { return m_SubjectName; }
		public String GetTeacherFirstName() { return m_TeacherFirstName; }
		public String GetTeacherMiddleName() { return m_TeacherMiddleName; }
		public String GetTeacherLastName() { return m_TeacherLastName; }
		public String GetLessonType() { return m_LessonType;}
		public String GetRoomDescription() { return m_RoomDescription;}
		
		
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('\n');
			sb.append(ScheduleRecord.ms_RecordPosition + " = " + GetRecordPosition());
			sb.append('\n');
			
			sb.append(ScheduleRecord.ms_BeginTime + " = " + GetBeginHour() + " : " + GetBeginMinutes());
			sb.append('\n');
			sb.append(ScheduleRecord.ms_EndTime + " = " + GetEndHour() + " : " + GetEndMinutes());
			sb.append('\n');
			sb.append(ScheduleRecord.ms_SubjectName + " = " + GetSubjectName());
			sb.append('\n');
			sb.append(ScheduleRecord.ms_TeacherFirstName + " = " + GetTeacherFirstName());
			sb.append('\n');
			sb.append(ScheduleRecord.ms_TeacherMiddleName + " = " + GetTeacherMiddleName());
			sb.append('\n');
			sb.append(ScheduleRecord.ms_TeacherLastName + " = " + GetTeacherLastName());
			sb.append('\n');
			sb.append(ScheduleRecord.ms_LessonType + " = " + GetLessonType());
			sb.append('\n');
			sb.append(ScheduleRecord.ms_RoomDescription + " = " + GetRoomDescription());
			sb.append('\n');
			return sb.toString();
		}



		// строитель для создания новой строчки расписания
		public static class ScheduleRecordBuilder
		{
			private ScheduleRecord m_Record2Build;
			
			public ScheduleRecordBuilder() {
				BuildNewRecord();
			}
			
			
			public void SetRecordPosition(int position) {
				m_Record2Build.m_RecordPosition = position;
			}
			
			@SuppressWarnings("deprecation")
			public void SetBeginHour(int hours) {
				m_Record2Build.m_BeginTime.setHours(hours);
			}
			
			@SuppressWarnings("deprecation")
			public void SetBeginMinute(int minutes) {
				m_Record2Build.m_BeginTime.setMinutes(minutes);
			}
			
			@SuppressWarnings("deprecation")
			public void SetEndHour(int hours) {
				m_Record2Build.m_EndTime.setHours(hours);
			}
			
			@SuppressWarnings("deprecation")
			public void SetEndMinute(int minutes) {
				m_Record2Build.m_EndTime.setMinutes(minutes);
			}
			
			public void SetSubjectName(String subjectName) {
				m_Record2Build.m_SubjectName = subjectName;
			}
			public void SetTeacherFirstName(String firstName) { m_Record2Build.m_TeacherFirstName = firstName; }
			public void SetTeacherMiddleName(String middleName) {m_Record2Build.m_TeacherMiddleName = middleName;}
			public void SetTeacherLastName(String lastName) {m_Record2Build.m_TeacherLastName = lastName; }
			public void SetLessonType(String lessonType) { m_Record2Build.m_LessonType = lessonType;}
			public void SetRoomDescription(String desc) {m_Record2Build.m_RoomDescription = desc; } 
			
			public void BuildNewRecord() {
				m_Record2Build = new ScheduleRecord();
				
				m_Record2Build.m_RecordPosition = 0;
				SetLessonType("UNKNOWN");
				SetTeacherFirstName("UNKNOWN");
				SetTeacherMiddleName("UNKNOWN");
				SetTeacherLastName("UNKNOWN");
				SetSubjectName("UNKNOWN");
				SetRoomDescription("UNKNOWN");				
			}
			
			public ScheduleRecord GetBuiltRecord() {
				ScheduleRecord r = m_Record2Build;
				m_Record2Build = null;
				return r;
			}
			
		}
	}
	
	
	
	public Schedule() {
		m_Schedule = new ArrayList<Schedule.ScheduleRecord>();
	}
	
	public Schedule(Schedule s2) {
		m_Schedule = new ArrayList<Schedule.ScheduleRecord>();
		for (ScheduleRecord sr : s2.GetRecordsList()) {
			m_Schedule.add(sr);
		}
	}
	
	public void AddRecord(ScheduleRecord sr) {
		m_Schedule.add(sr);
	}
	
	
	
	
	public List<ScheduleRecord> GetRecordsList() { return m_Schedule; }
		
	private List<ScheduleRecord> m_Schedule;
}

