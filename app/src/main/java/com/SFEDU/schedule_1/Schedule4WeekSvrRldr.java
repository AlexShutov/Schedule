package com.SFEDU.schedule_1;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord.ScheduleRecordBuilder;

import android.R.layout;
import android.os.Environment;

public class Schedule4WeekSvrRldr 
{
	
	public Schedule4WeekSvrRldr(){
		Init();
	}
	public Schedule4WeekSvrRldr(String dirName){
		Init();
		SetParentDir(dirName);
	}
	public void SetParentDir(String dirName) {
		if (dirName == null)
			return;
		File r = Environment.getExternalStorageDirectory();
		r = new File(r, "/" + dirName);
		r.mkdirs();
		m_ParentDir = r;		
		// теперь начинаем отсчитывать записи заново
		m_SavingCount = 0;
	}
	
	public void SaveWeekSchedule(Schedule4Week s4w) 
			throws IllegalArgumentException,
			IllegalStateException,
			ScheduleKeepReloadException
	{
		if (s4w == null) {
			throw new IllegalArgumentException("Null reference");
		}
		
		// должна быть доступна запись на диск
		MemoryCardFileManager memCardMgr = new MemoryCardFileManager();
		if (!memCardMgr.IsExternalAvailible() && !memCardMgr.IsExternalWriteable()) {
			throw new IllegalStateException("File storage isn't ready");
		}		
		// если путь к директории не был установлен, то записываем в корень
		if (m_ParentDir == null) {
			m_ParentDir = Environment.getExternalStorageDirectory();
		}
		
		File dir = null;
		try {
			if (!m_ParentDir.exists())
				m_ParentDir.createNewFile();
			
			String d = null;		// Признак расписания(имя подпапки)
			if (s4w.GetTag() == null) {
				d = "Schedule_" + ++m_SavingCount;
			} else {
				d = s4w.GetTag();
			}
			
			dir = new File(m_ParentDir, d);
			dir.mkdirs();
						
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Directory doesn't exist and"
					+ " can't be created");
		}
				
		for (String dayName : Schedule4Week.GetDayNamesList())
		{
			Schedule s = null;
			try {	// даже если нет расписания для нужного дня, продолжаем, 
				s = s4w.GetDaySchedule(dayName);	// т.к могут быть для других дней
			} catch (IllegalArgumentException iae) {
				continue;
			}
			String p = dir.getPath() +"/" + dayName + ".xml";
			// не хорошо, но для более "тонкой настройки"- не в корень, как думал
			m_schSaverReloader.GetFileMgr().SetFileName(p, false);
			m_schSaverReloader.SaveSchedule(s);
		}
	}
	
	public Schedule4Week ReloadSchedule4Week(String tag)
	throws IllegalArgumentException,
			IllegalStateException,
			ScheduleKeepReloadException
	{
		Schedule4Week result = new Schedule4Week();
		
		// должна быть доступна запись на диск
		MemoryCardFileManager memCardMgr = new MemoryCardFileManager();
		if (!memCardMgr.IsExternalAvailible())
		{
			throw new IllegalStateException("File storage isn't ready");
		}
		
		if (!m_ParentDir.exists()) {
			throw new IllegalArgumentException("Parent directory doesn't exist");
		}
		
		File schDir = new File(m_ParentDir, "/" + tag);
		if (!schDir.exists())
			throw new IllegalArgumentException("directory: "+ schDir.getPath() + "doesn't exist");
		
		// смотрим все файлы в папке
		File[] files = schDir.listFiles();
		for (File d : files) {
			if (!d.isDirectory()) {	// и выбираем только директории
				// получаем имя файла, и если оно- название дня и удаляем расширение .xml
				String dayName = d.getName();
				// удаляем точку с расширением
				dayName = dayName.substring(0, dayName.length() - 4);
				// проверяем, что имя файла допустимо
				if (Schedule4Week.GetDayNamesList().contains(dayName)) 
				{
					String fileName = d.getPath();
					m_schSaverReloader.GetFileMgr().SetFileName(fileName, false);
					try {
						Schedule s = m_schSaverReloader.ReloadSchedule();
						result.PutDaySchedule(dayName, s);
					} catch(Exception e) {	// файл пуст или повреждён, возврщаем пустое
						result.PutDaySchedule(dayName, new Schedule()); // расписание
					}
				}				
			}
		}	
		return result;
	}	
		
	protected void Init()
	{
		m_ParentDir = null;
		m_sch4w = null;
		
		m_SavingCount = 0;
		
		// имя файла будет присвоено при сохранении/загрузке
		m_schSaverReloader = new XMLScheduleSaverReloader();
	}
		
	XMLScheduleSaverReloader m_schSaverReloader;
	
	// каждому расписанию присваивается строка (SetTag). 
	// если не присваивается, оно записывается в папку "Schedule_" + cnt
	private int m_SavingCount;
	private Schedule4Week m_sch4w;
	private File m_ParentDir;
}
