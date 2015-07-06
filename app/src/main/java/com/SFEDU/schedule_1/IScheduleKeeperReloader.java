package com.SFEDU.schedule_1;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;

/**
 * Interface for saving and reloading an entire schedule and also one schedule record
 */
interface IScheduleKeeperReloader 
{
	/**
	 * works with one record
	 */
	interface IScheduleRecordKeeperReloader
	{
		public void SaveRecord(ScheduleRecord r) throws ScheduleKeepReloadException;
		public ScheduleRecord ReloadRecord() throws ScheduleKeepReloadException;
	}

	/**
	 * manages the all schedule
	 */
	void SaveSchedule( Schedule schedule) throws ScheduleKeepReloadException;
	Schedule ReloadSchedule() throws ScheduleKeepReloadException;

	/**
	 * removes saved data
	 */
	void DropSavedSchedule() throws ScheduleKeepReloadException;
}


