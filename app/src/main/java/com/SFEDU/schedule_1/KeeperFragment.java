package com.SFEDU.schedule_1;

import android.R.bool;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

/**
 * Singleton fragment for persistent schedule storing
 * requires fragment manager for searching for itself
 */
public class KeeperFragment extends Fragment {

	public static final String TAG = "KEEPER_FRAGMENT_TAG";
	
	public static KeeperFragment getInstance(FragmentManager fm) {
		Fragment f = fm.findFragmentByTag(KeeperFragment.TAG);
		KeeperFragment kf = null;
		if (f == null) {	// first run, not rotation
			kf = new KeeperFragment();
			fm.beginTransaction().add(kf, KeeperFragment.TAG).commit();
		} else {
			kf = (KeeperFragment) f;
		}
		return kf;
	}
	
	public static boolean IsAlreadyInitialized(FragmentManager fm) {
		Fragment f = fm.findFragmentByTag(KeeperFragment.TAG);
		return f == null;
	}
	
	protected KeeperFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	public void setSchedule(Schedule4Week s, boolean isOdd) {
		if (isOdd) {
			mOddWeek = s;
		} else {
			mEvenWeek = s;
		}
	}
	// if null return empty schedule
	public Schedule4Week getSchedule(boolean isOdd) { 
		if (isOdd) {
			if (mOddWeek == null) {
				mOddWeek = new Schedule4Week();
			}
			return mOddWeek;
		} else {
			if (mEvenWeek == null) {
				mEvenWeek = new Schedule4Week();
			}
			return mEvenWeek;
		}
	}
	
	private Schedule4Week mOddWeek;
	private Schedule4Week mEvenWeek;
	
}
