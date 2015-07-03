package com.SFEDU.schedule_1;

import java.util.Calendar;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TestActivity extends Activity 
 implements ScheduleDayFragment.OnItemClickListener,
 			OEWeekSaverBase.OnCompletionCallback, 
 			ScheduleEditRecordFragment.OnEditResult
{
	/**
	 * acquires current weekday index, 0- monday, 6- sunday,
	 * used to set default day of week
	 */
	private static int GetCurrentWeekdayIndex() {
		Calendar c = Calendar.getInstance();
		// sunday is origin
		int wd = c.get(Calendar.DAY_OF_WEEK) -2;
		if (wd == -1) { // sunday
			wd = 6;
		}   
		return wd;
	}     
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		initSpinnerNCHeckbox();
		
		// initialize schedule keeper
		KeeperFragment keeper = KeeperFragment.getInstance(getFragmentManager());
		// if keeper has no schedules (launch, not rotation), create them
		keeper.getSchedule(true);
		keeper.getSchedule(false);
		 
		// ??? When I press back button, activity terminates, but KeeperFragment
		// supposed to be in a memory. But it gets deleted too, so when minimized app
		// is brought to the front, list has no records. Workaround: reload
		// records every creation, even though they're should stay in memory
		
		// create new saver/reloader
		mSaver = new OEWeekFileSaver(getApplicationContext());
		//if (!KeeperFragment.IsAlreadyInitialized(getFragmentManager())) {	
		if (true) {
			mSaver.restoreWeek(this, true);
			mSaver.restoreWeek(this, false);
		}		
		// set current day of week as default 
		// when rotated, state is loaded from saved Bundle, this works
		// only first time
		int wd = GetCurrentWeekdayIndex();
		m_daysPicker.setSelection(wd);
		m_WeekDayIndex = wd;
		 
		updateCurrentDayReference();
		showDayWeek(mCurrentDay);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.menu_add_item:
			Toast.makeText(this, "Add item is selected", Toast.LENGTH_SHORT).show();
			addNewRecord();
			break;
		default:
			return false;
		}
		return true;
	}
	
	/**
	 * callback for new record screen button touch events
	 */
	@Override
	public void OnSuccess(ScheduleRecord newRecord) {
		mCurrentDay.AddRecord(newRecord);
		updateCurrentDayReference();
		showDayWeek(mCurrentDay);
		
		// data changed, save it 
		saveChangedWeek();
		//Toast.makeText(getApplicationContext(), "Ok Button", Toast.LENGTH_SHORT).show();
	} 
	/**
	 * use cancelled new schedule adding, 
	 * so just display current day schedule
	 */
	@Override
	public void OnCancel() {
		showDayWeek(mCurrentDay);
	}
	
	
	/**
	 * part, which is responsible for spinner and check box-
	 * variables and init method
	 */
	private String[] m_daysNames;
	private String[] m_oddEven;
	private Spinner m_daysPicker;
	private CheckBox m_oddityCheckBox;
	
	private boolean m_IsOddWeek;
	private int m_WeekDayIndex;
	
	
	/**
	 * initializes spinner and even/odd week checkbox
	 */
	AdapterView.OnItemSelectedListener m_dayOfWeekPickerHandler;
	protected void initSpinnerNCHeckbox()
	{		
		m_daysNames = getResources().getStringArray(R.array.weekDaysNames);
		// spinner setup
		m_dayOfWeekPickerHandler = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// save current spinner value
				m_WeekDayIndex = position;
				OnWeekDayChecked(position, m_daysNames[position]);
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
		// spinner setup
		m_daysPicker = (Spinner) findViewById(R.id.mainFrame);
		m_daysPicker.setOnItemSelectedListener(m_dayOfWeekPickerHandler);
	
	   	ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, m_daysNames);
		
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_daysPicker.setAdapter(arrayAdapter);
		
		// odd/even checkbox setup
		m_oddityCheckBox = (CheckBox) findViewById(R.id.weekOddityCB);
		m_oddEven = getResources().getStringArray(R.array.oddEvenWeek);
		m_oddityCheckBox.setChecked(false);
		m_oddityCheckBox.setText(m_oddEven[0]);
		m_oddityCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {	
					buttonView.setText(m_oddEven[1]);
				}
				else {
					buttonView.setText(m_oddEven[0]);
				}
				m_IsOddWeek = isChecked;
				// even if chosen
				OnOddEvenWeekChecked(isChecked);
			}
		});		
	}
	 
	/**
	 * updates current day reference based on spinner and checkbox values
	 */
	private void updateCurrentDayReference() {
		KeeperFragment f = KeeperFragment.getInstance(getFragmentManager());
		String currDayName = Schedule4Week.GetDayNamesList().get(m_WeekDayIndex);
		mCurrentDay= f.getSchedule(m_IsOddWeek).GetDaySchedule(currDayName);
	}
	
	/**
	 * creates new list fragments and puts it into the frame
	 */
	private void showDayWeek(Schedule s) {
		ScheduleDayFragment day = new ScheduleDayFragment(s);
		// this screen handles touches
		day.SetOnItemClickListener(this);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.mainFragment, day);
		ft.commit();	
	}
	
	/**
	 * displays new add record fragment
	 */
	private void addNewRecord() {
		ScheduleEditRecordFragment addFragment = new ScheduleEditRecordFragment();
		// set this activity as a button touch callback
		addFragment.SetButtonsTouchHandler(this);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.mainFragment, addFragment);
		ft.commit();
	}
	
	/**
	 * saves changed week
	 */
	private void saveChangedWeek() {
		KeeperFragment f = KeeperFragment.getInstance(getFragmentManager());
		mSaver.saveWeek(f.getSchedule(m_IsOddWeek), this, m_IsOddWeek);
	}
	
	/**
	 * removes selected record from current day schedule and
	 * redraws view
	 */
	public void removeRecord(int recordNo) {
		Toast.makeText(getApplicationContext(), "Remove item No " + recordNo, Toast.LENGTH_SHORT).show();
		mCurrentDay.GetRecordsList().remove(recordNo);
		saveChangedWeek();
		showDayWeek(mCurrentDay); 
	}
	
	/**
	 * schedule load/save completion handlers, updates ui and stores loaded
	 * schedule in keeper fragment
	 */
	@Override
	public void OnSaveComplete(boolean isOddWeek) {
		//Toast.makeText(getApplicationContext(), "schedule saved", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void OnLoadComplete(Schedule4Week s, boolean isOddWeek) {
		// save current schedule in keeper faragment
		KeeperFragment f = KeeperFragment.getInstance(getFragmentManager());
		f.setSchedule(s, isOddWeek);
		// current week loaded
		if (isOddWeek == m_IsOddWeek) {
			updateCurrentDayReference();
			showDayWeek(mCurrentDay);
		}
	}

	/**
	 *  list handler, used to add new schedule
	 */
	@Override
	public void OnClick(int recordPosition, ScheduleDayFragment sender) {
		showPopupMenu(recordPosition,  sender);
		//String msg = "Item No " + (recordPosition + 1) + " is touched";
		//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	

	/**
	 * creates popup menu from xml, installs handler and shows menu
	 */
	private void showPopupMenu(final int recordPos,  ScheduleDayFragment sender)
	{
		PopupMenu popup = new PopupMenu(this, sender.getView());
		popup.inflate(R.menu.popup_menu);
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.popup_add_new_record:
					addNewRecord();
					break;
				case R.id.popup_remove_record:
					removeRecord(recordPos);
					break;
				default:
					return false;
				}
				return true; 
			}
		});
		popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {}
		});
		popup.show();
	}
	  
	/**
	 * processes spinner and checkbox events	
	 * different week is selected, we need to updater current week reference
	 * and display it
	 */
	private void OnOddEvenWeekChecked(boolean isOdd) {
		//Toast.makeText(getApplicationContext(), "Is odd?: " + isOdd, Toast.LENGTH_SHORT).show();
		updateCurrentDayReference();
		showDayWeek(mCurrentDay);
	}
	
	/**
	 * different day of week is selected
	 */
	private void OnWeekDayChecked(int dayIndex, String dayName) {
		//String msg = "Weekday selected: " + Schedule4Week.GetDayNamesList().get(dayIndex);
		//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		updateCurrentDayReference();
		showDayWeek(mCurrentDay);
	}
	
	
	
	private Schedule mCurrentDay;
	/**
	 * schedule saver to file (db)
	 */
	OEWeekSaverBase mSaver;
	
	
}
