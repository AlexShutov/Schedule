package com.SFEDU.schedule_1;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;

// что нужно для сохранения и загрузки расписания, а также одной записи расписания
interface IScheduleKeeperReloader 
{
	// сохранятель/загрузчик одной строки расписания
	interface IScheduleRecordKeeperReloader
	{
		// сохранить одну запись (каждый раз открыть хранилище)
		public void SaveRecord(ScheduleRecord r) throws ScheduleKeepReloadException;
		public ScheduleRecord ReloadRecord() throws ScheduleKeepReloadException;
	}

	
	// сохранить всё расписание (хранилище открывается один раз)
	void SaveSchedule( Schedule schedule) throws ScheduleKeepReloadException;
	Schedule ReloadSchedule() throws ScheduleKeepReloadException;
	
	
	// удаляет сохранённые данные (как расписание на неделю, так и на 1 день)
	void DropSavedSchedule() throws ScheduleKeepReloadException;
}


