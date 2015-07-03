package com.SFEDU.schedule_1;

import java.util.Calendar;
import java.util.Date;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;
import com.SFEDU.schedule_1.Schedule.ScheduleRecord.ScheduleRecordBuilder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class ScheduleEditRecordFragment extends Fragment {
	public static final String TAG = "ADD_NEW_RECORD_SCREEN";
	
	interface OnEditResult {
		void OnSuccess(ScheduleRecord newRecord);
		void OnCancel();
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	} 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.add_record_screen_layout, container, false);
	
		return v;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// binding to UI
		m_positionField = (EditText) view.findViewById(R.id.positionField);
		
		m_beginTimePicker = (TimePicker) view.findViewById(R.id.begTimePicker);
		m_endTimePicker = (TimePicker) view.findViewById(R.id.endTimePicker);
		m_beginTimePicker.setIs24HourView(true);
		m_endTimePicker.setIs24HourView(true);
		m_subjectName = (EditText) view.findViewById(R.id.subjectNameField);
		m_teacherFirstName = (EditText) view.findViewById(R.id.teacherFirstNameField);
		m_teacherLastName = (EditText) view.findViewById(R.id.teacherLastNameField);
		m_teacherMiddleName = (EditText) view.findViewById(R.id.teacherMiddleNameField);
		m_lessonType = (EditText) view.findViewById(R.id.lessonTypeField);
		m_roomDescription = (EditText) view.findViewById(R.id.roomDescField);
		m_addButton = (Button) view.findViewById(R.id.addButton);
		m_cancelButton = (Button) view.findViewById(R.id.buttonCancel);
				
		m_addButton.setOnClickListener(m_AddButtonClickHandler);
		m_cancelButton.setOnClickListener(m_CancelButtonClickHandler);		
		
		 
	}
	

	private void ShowText(String s)
	{
		Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
	}
	

	/**
	 * checks whether all fields is filled
	 */
	private boolean validateInput() {
		String recordPos = m_positionField.getText().toString();
		if (recordPos.equals("")) {
			showToastHint(R.string.add_rec_hint_record_no);
			return false;
		}
		String subjectName = m_subjectName.getText().toString();
		if (subjectName.equals("")) {
			showToastHint(R.string.add_rec_hint_subject_name);
			return false;
		}
		String teacherFN = m_teacherFirstName.getText().toString();
		if (teacherFN.equals("")) {
			showToastHint(R.string.add_rec_hint_teacher_fn);
			return false;
		}
		String teacherLN = m_teacherLastName.getText().toString();
		if (teacherLN.equals("")) {
			showToastHint(R.string.add_rec_hint_teacher_ln);
			return false;
		}
		String teacherMN = m_teacherMiddleName.getText().toString();
		if (teacherMN.equals("")) {
			showToastHint(R.string.add_rec_hint_teacher_mn);
			return false;
		}
		String lessonType = m_lessonType.getText().toString();
		if (lessonType.equals("")) {
			showToastHint(R.string.add_rec_hint_lesson_type);
			return false;
		}
		String roomDesc = m_roomDescription.getText().toString();
		if (roomDesc.equals("")) {
			showToastHint(R.string.add_rec_hint_room_no);
			return false;
		}
		
		return true;
	}
	private void showToastHint(int stringID) {
		String msg = getString(stringID);
		Toast.makeText(getActivity(), msg , Toast.LENGTH_SHORT).show();
	}
	
	private final OnClickListener m_AddButtonClickHandler = new OnClickListener() {
		
		
		@Override
		public void onClick(View v) {
			
			if (!validateInput()) {
				return;
			}
			
			// получаем значения полей и сохраняем их в намерении
			ScheduleRecordBuilder rb = new ScheduleRecordBuilder();
			rb.BuildNewRecord();
			
			String recordPos = m_positionField.getText().toString();
			rb.SetRecordPosition(Integer.valueOf(recordPos));
			
			int begHours = m_beginTimePicker.getCurrentHour();
			int begMinutes = m_beginTimePicker.getCurrentMinute();
			int endHours = m_endTimePicker.getCurrentHour();
			int endMinutes = m_endTimePicker.getCurrentMinute();
			
			
			rb.SetBeginHour(begHours);
			rb.SetBeginMinute(begMinutes);
			rb.SetEndHour(endHours);
			rb.SetEndMinute(endMinutes);
			
			// название предмета
			String subjectName = m_subjectName.getText().toString();
			rb.SetSubjectName(subjectName);
			
			// фио преподавателя
			String teacherFN = m_teacherFirstName.getText().toString();
			rb.SetTeacherFirstName(teacherFN);
			String teacherLN = m_teacherLastName.getText().toString();
			rb.SetTeacherLastName(teacherLN);
			String teacherMN = m_teacherMiddleName.getText().toString();
			rb.SetTeacherMiddleName(teacherMN);
			// тип занятия
			String lessonType = m_lessonType.getText().toString();
			rb.SetLessonType(lessonType);
			// номер комнаты
			String roomDesc = m_roomDescription.getText().toString();
			rb.SetRoomDescription(roomDesc);
			// устанавливаем результат
			if (mBtnsHandler != null) {
				mBtnsHandler.OnSuccess(rb.GetBuiltRecord());
			}			
		}
	};
	
	
	// отмена добавления занятия
	private final OnClickListener m_CancelButtonClickHandler = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String msg = getString(R.string.user_cancel_adding);
				ShowText(msg);
				if (mBtnsHandler != null) {
					mBtnsHandler.OnCancel();
				}				
			}
		};
	
	public void SetButtonsTouchHandler(OnEditResult hnd) {
		mBtnsHandler = hnd;
	}

	private ScheduleEditRecordFragment.OnEditResult mBtnsHandler;

	private EditText m_positionField;
	private TimePicker m_beginTimePicker;
	private TimePicker m_endTimePicker;
	private EditText m_subjectName;
	private EditText m_teacherFirstName;
	private EditText m_teacherLastName;
	private EditText m_teacherMiddleName;
	private EditText m_lessonType;
	private EditText m_roomDescription;
	
	private Button m_addButton;
	private Button m_cancelButton;
	
}
