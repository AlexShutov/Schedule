package com.SFEDU.schedule_1;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.widget.Toast;

public class OEWeekFileSaver extends OEWeekSaverBase {
	
	Context mContext;
	Schedule4WeekSvrRldr mFileSaver;
	String mParentDirName = "";
	String mOddFolderName = "";
	String mEvenFolderName = "";
	
	public OEWeekFileSaver(Context c) {
		super();
		mContext = c;
		mFileSaver = new Schedule4WeekSvrRldr();
		Resources res = c.getResources();
		mParentDirName = res.getString(R.string.schedule_parent_dir_name);
		mFileSaver.SetParentDir(mParentDirName);
		
		mOddFolderName = res.getString(R.string.schedule_folder_name_odd);
		mEvenFolderName = res.getString(R.string.schedule_folder_name_even);
	}
	
	
 
	@Override
	protected void saveWeekSync(Schedule4Week s, boolean isOdd) {
		
		String tag = isOdd ? mOddFolderName : mEvenFolderName;
		eraseSchDir(tag);
		s.SetTag(tag);
		try 
		{
			mFileSaver.SaveWeekSchedule(s);
		} 
		catch (IllegalArgumentException e) {
			// schedule is corrupt, create a new one and try again
			s = new Schedule4Week();
			try {
				mFileSaver.SaveWeekSchedule(s);
			} catch (Exception ex) {
				throw new RuntimeException(ex.getCause());
			}
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace(); // just exit, can't retry
			return;
		} catch (ScheduleKeepReloadException e) {
			e.printStackTrace();
			return;	
		}
	}
	
	
	@Override
	protected Schedule4Week RestoreWeekSync(boolean isOdd) {
		Schedule4Week s = null;
		String tag = isOdd ? mOddFolderName : mEvenFolderName;
		mFileSaver.SetParentDir(mParentDirName);
		try {
			s = mFileSaver.ReloadSchedule4Week(tag);
			// exceptions in worker thread is not allowed
		} catch (IllegalArgumentException e) { 
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (ScheduleKeepReloadException e) {
			e.printStackTrace();
		}
		if (s == null) {
			s = new Schedule4Week();
		}
		return s;
	}
	/**
	 * wipes out directory containing saved schedules (odd, week in this app)
	 */
	private void eraseSchDir(String tag) {
		String parent = mContext.getString(R.string.schedule_parent_dir_name);
		parent = Environment.getExternalStorageDirectory().getPath() + "/" + parent 
				+ "/" + tag;
		File f = new File(parent);
		eraseR(f);
		
		File[] files = f.listFiles();
		if (files == null) {
			return;
		}
		for (File tmp : files) { //  erase empty dirs
			tmp.delete();
		}
	}
	private void eraseR(File f) {
		if (f == null) {
			return;
		}
		if (f.isFile()) {
			f.delete();
		} else { // directory
			File[] files = f.listFiles();
			if (files == null) { 
				f.delete();
				return;
			}
			for (File tmp : files) {
				eraseR(tmp);
			}
		}
	}
	
}
