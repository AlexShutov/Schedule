package com.SFEDU.schedule_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.R.bool;
import android.os.Environment;


public class MemoryCardFileManager {

	private File targetFile;
	private File rootDir;
	private boolean isMemCardReady;

	////////////////////////////////////////////////////////////
	
	// одновременно можно считывать или записывать в файл,
	// открыт только один поток одновременно
	private boolean isRWSessionInProgress;
	
	// потоки для чтения/ записи
	BufferedWriter writeStream;
	BufferedReader readStream;
	
	/////////////////////////////////////////////////////////////
	
	// состояние карты памяти, определяется один раз при создании
	// можно получить доступ к карте памяти
	private boolean externalAvailible;
	// можно записывать на карту памяти
	private boolean externalWriteable;
	
	/////////////////////////////////////////////////////////////

	public boolean IsMemCardReady() { return isMemCardReady; }
	
	
	MemoryCardFileManager() {
		externalAvailible = false;
		externalWriteable = false;
		isRWSessionInProgress = false;
		isMemCardReady = false;
		checkCardState();
	}
	
	/**
	 * fixed: check for storage state every time
	 */
	public boolean IsExternalAvailible() {
		checkCardState();
		return externalAvailible; 
	}
	public boolean IsExternalWriteable() {
		checkCardState();
		return externalWriteable; 
	}
	
	/**
	 * checks storage state: whether you can read from or write to it
	 */
	private void checkCardState() 
	{		
		isMemCardReady = true;
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// можно  и записывать, и считывать
			externalAvailible = true;
			externalWriteable = true;
		}
		else
		if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			externalAvailible = true;
		}
		else {
			// нет доступа к карте памяти
			isMemCardReady = false;
		}
	}
	
	public void EraseFile()
	{
		// удаляем файл и создаём новый
		targetFile.delete();
		try {
			targetFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			// создастся, если удалили
		}
	}
	
	
	// true- если файл на флешке без папок
	public void SetFileName(String fileName,
			boolean inRootDir) 
			throws IllegalStateException, IllegalArgumentException
	{
		if (!isMemCardReady) {
			throw new IllegalStateException("Memory card isn't ready");
		}
		
		if (inRootDir) {
			File r = Environment.getExternalStorageDirectory();
			targetFile = new File(r, fileName);
		} else {
			targetFile = new File(fileName);
		}
		
		if (targetFile.isDirectory()) {
			throw new IllegalArgumentException("file is required, not a directory");
		}
		
		// если файла нет, то создаём его
		if (!targetFile.exists()) {
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
		
	}
	
	// устанавливает флаг доступности и закрывает потоки
	public boolean CloseFileSession()
	{
		if (!isRWSessionInProgress) {
			return false;
		}
		if (writeStream != null) {
			try {
				writeStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			writeStream = null;
		}
		if (readStream != null) {
			try {
				readStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			readStream = null;
		}
		isRWSessionInProgress = false;
		return true;
	}
	
	public BufferedWriter OpenWriteSession() throws IllegalAccessException
	{
		
		
		// если файл занят, то сообщаем об этом
		if (isRWSessionInProgress || !externalWriteable) {
			throw new IllegalAccessException("can't write cause session is already in progress");
		}
		
		try {
			FileWriter fWriter = new FileWriter(targetFile, true);
			writeStream = new BufferedWriter(fWriter); 
		} catch (IOException e) { // не можем получить доступ к файлу (потому что
								  // потому что он занят
			throw new IllegalAccessException("Can't open write session: " +
						e.getMessage() );
		}	
		// устанавливаем флаг занятости
		isRWSessionInProgress = true;
		return writeStream;
	}
	
	public BufferedReader OpenReadSession() throws IllegalAccessException
	{
		// если файл занят, то сообщаем об этом
		if (isRWSessionInProgress || !externalAvailible) {
			throw new IllegalAccessException("can't read, because session is already in progress");
		}
	
		try {
			FileReader fReader = new FileReader(targetFile);
			readStream = new BufferedReader(fReader); 
		} catch (IOException e) { // не можем получить доступ к файлу (потому что
								  // потому что он занят
			throw new IllegalAccessException("Can't open write session: " +
						e.getMessage() );
		}	
		// устанавливаем флаг занятости
		isRWSessionInProgress = true;
		return readStream;
	}
	
	// чтобы сохранить строки расписания по очереди, нужно иметь ссылку на 
	// открытый файл
	public BufferedWriter GetWriteStream() { return writeStream; }
	public BufferedReader GetReadStream() { return readStream; }
	
	public boolean isWriting() { return isRWSessionInProgress && (GetReadStream() != null); }
	public boolean isReading() { return isRWSessionInProgress && (GetWriteStream() != null); }
	
	
}
