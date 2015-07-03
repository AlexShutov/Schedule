package com.SFEDU.schedule_1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.AsyncTask;
import android.os.Handler;


public abstract class OEWeekSaverBase {
	
	interface OnCompletionCallback {
		public void OnSaveComplete(boolean isOddWeek);
		public void OnLoadComplete(Schedule4Week o, boolean isOddWeek);
	}
	
	class SaveAsyncTask extends AsyncTask<Schedule4Week, Void, Void> {
		private boolean mIsOdd;
		OnCompletionCallback mCallback;
		// setting details
		public SaveAsyncTask( boolean isOdd,
					OnCompletionCallback callback) {
			mIsOdd = isOdd;
			mCallback = callback;
		}
		
		@Override
		protected Void doInBackground(Schedule4Week... params) {
			Schedule4Week s = params[0];
			saveWeekSync(s, mIsOdd);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mCallback != null) {
				mCallback.OnSaveComplete(mIsOdd);
			}
		}
	}
	
	class LoadAsyncTask extends AsyncTask<Void, Schedule4Week, Void> {
		private boolean mIsOdd;
		OnCompletionCallback mCallback;
		public LoadAsyncTask( boolean isOdd, OnCompletionCallback callback) {
			mIsOdd = isOdd;
			mCallback = callback;
		}
		@Override
		protected Void doInBackground(Void... params) {
			Schedule4Week s = RestoreWeekSync(mIsOdd);
			publishProgress(s);
			return null;
		}
		@Override
		protected void onProgressUpdate(Schedule4Week... values) {
			if (mCallback != null) {
				mCallback.OnLoadComplete(values[0], mIsOdd);
			}
		}
	}
		
	public OEWeekSaverBase() {
		mThreadPool = Executors.newFixedThreadPool(1);
	}
	
	protected abstract void saveWeekSync(Schedule4Week oddWeekSchedule, boolean isOdd);
	protected abstract Schedule4Week RestoreWeekSync(boolean isOdd);
	
	
	public void saveWeek(final Schedule4Week s, final OnCompletionCallback callback, 
			final boolean isOdd) {
		SaveAsyncTask task = this.new SaveAsyncTask(isOdd, callback);
		task.executeOnExecutor(mThreadPool, s);
		 
	}
		
	public void restoreWeek(OnCompletionCallback callback, boolean isOdd) {
		LoadAsyncTask task = this.new LoadAsyncTask(isOdd, callback);
		task.executeOnExecutor(mThreadPool, (Void[]) null);
	}
	// to offload task from ui thread pool
	private ExecutorService mThreadPool;
}
