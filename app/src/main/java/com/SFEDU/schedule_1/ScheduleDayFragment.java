package com.SFEDU.schedule_1;

import java.util.List;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class ScheduleDayFragment extends ListFragment {
	public static final String TAG = "DAY_FRAGMENT";
	
	//touch callback
	interface OnItemClickListener {
		public void OnClick(int recordPosition, ScheduleDayFragment sender);
	}
	
	public ScheduleDayFragment() {
		mDaySchedule = new Schedule();
	}
	public ScheduleDayFragment(Schedule s) {
		if (s == null) {
			throw new RuntimeException("Null argument reference");
		}
		mDaySchedule = s;
	} 
	
	public void setNewData(Schedule s) {
		if (s == null) {
			return;
		}
		mDaySchedule = s;
		List<ScheduleRecord> recs = mDaySchedule.GetRecordsList();
		mAdapter = new ScheduleAdapter(getActivity(), recs);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<ScheduleRecord> recs = mDaySchedule.GetRecordsList();
		mAdapter = new ScheduleAdapter(getActivity(), recs);
		setListAdapter(mAdapter);	
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (mClickListener != null) {
			mClickListener.OnClick(position, this);
		}
	}
	
	
	public void SetOnItemClickListener(OnItemClickListener listener) {
		mClickListener = listener;
	}
	
	// used for item removal
	private ScheduleDayFragment.OnItemClickListener mClickListener;
	// schedule to show
	private Schedule mDaySchedule;
	private ScheduleAdapter mAdapter;
	

}
