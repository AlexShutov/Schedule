package com.SFEDU.schedule_1;

import java.util.ArrayList;
import java.util.List;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


// адаптер списка
public class ScheduleAdapter extends BaseAdapter 
{
	private LayoutInflater m_Inflater;
	private List<ScheduleRecord> m_Items = new ArrayList<ScheduleRecord>();
	
	public ScheduleAdapter(Context context,  List<ScheduleRecord> items)
	{
		m_Inflater = LayoutInflater.from(context);
		m_Items = items;
	}
	
	@Override
	public int getCount() {
		return m_Items.size();
	}

	@Override
	public Object getItem(int position) {
		return m_Items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//чтобы список прокручивался плавно, повторно используются
		// ссылки на его виджеты
		RecordViewHolder holder = null;
		
		if (convertView == null) 
		{ 
			
			convertView = m_Inflater.inflate(R.layout.schedule_record_list_item , null);
			holder = new  RecordViewHolder();
			holder.recordPosition = (TextView) convertView.findViewById(R.id.recordPosition);
			holder.lessonTime = (TextView) convertView.findViewById(R.id.recordTime);
			holder.subjectName = (TextView) convertView.findViewById(R.id.subjectName);
			holder.teacherName = (TextView) convertView.findViewById(R.id.teacherName);
			holder.lessonType = (TextView) convertView.findViewById(R.id.lessonType);
			holder.roomDesc = (TextView) convertView.findViewById(R.id.roomDesc);
	
			convertView.setTag(holder);
		}
		else {
			holder = (RecordViewHolder) convertView.getTag();
		}
		ScheduleRecord sr = m_Items.get(position);
		holder.recordPosition.setText(String.valueOf(sr.GetRecordPosition()));
		// время
		StringBuilder sb = new StringBuilder();
		sb.append(sr.GetBeginHour());
		sb.append(":");
		sb.append(sr.GetBeginMinutes());
		sb.append(" - ");
		sb.append(sr.GetEndHour());
		sb.append(":");
		sb.append(sr.GetEndMinutes());
		holder.lessonTime.setText(sb.toString());
		// название предмета
		holder.subjectName.setText(sr.GetSubjectName());
		// имя преподавателья
		sb = new StringBuilder();
		sb.append(sr.GetTeacherFirstName());
		sb.append(" ");
		sb.append(sr.GetTeacherMiddleName());
		sb.append(" ");
		sb.append(sr.GetTeacherLastName());
		holder.teacherName.setText(sb.toString());
		// лек/прак и номер комнаты
		holder.lessonType.setText(sr.GetLessonType());
		holder.roomDesc.setText(sr.GetRoomDescription());
			
		return convertView;
	}
	
	static class RecordViewHolder
	{
		TextView recordPosition;
		TextView lessonTime;
		TextView subjectName;
		TextView teacherName;
		TextView lessonType;
		TextView roomDesc;		
	}
	
}
