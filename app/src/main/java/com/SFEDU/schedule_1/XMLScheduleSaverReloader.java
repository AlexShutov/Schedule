package com.SFEDU.schedule_1;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import android.R.bool;
import android.R.id;
import android.R.integer;
import android.R.layout;
import android.content.Entity.NamedContentValues;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.StaticLayout;
import android.util.Log;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;
import com.SFEDU.schedule_1.Schedule.ScheduleRecord.ScheduleRecordBuilder;

public class XMLScheduleSaverReloader
	implements IScheduleKeeperReloader
{
	
	// Название корневого элемента
	private static final String TAG_SCHEDULE = "SCHEDULE";
	private static final String TAG_SCHEDULE_RECORD = "RECORD";
	
	public class XMLRecordSaverReloader 
		implements IScheduleRecordKeeperReloader
	{	
		// представляем строку расписания как текстовую строку
		@Override
		public void SaveRecord(ScheduleRecord r) throws ScheduleKeepReloadException
		{
			if (r == null) {
				return;
			}
		}
		
		/**
		 * unneccessary in here
		 */
		@Override
		public ScheduleRecord ReloadRecord() throws ScheduleKeepReloadException {
			return null;
		}

	}
		
	

	
	
	

	// строитель поддерева записи расписания
	// сохраняет запись расписания, при использовании DOM- документ должен быть
	// создан. Реализация в XMLScheduleSaverReloader позволяет сохранять единственную запись
	// к существующему дереву, при этом удаляя и перезаписывая снова файл.
	// данный строитель же просто создаём DOM- дерево
	protected static class RecordDOMTreeBuilder
		implements IScheduleRecordKeeperReloader
	{
		

		@Override
		public void SaveRecord(ScheduleRecord sr) throws ScheduleKeepReloadException 
		{
			// создаём узел записи расписания
			m_Record = m_DOMDoc.createElement(TAG_SCHEDULE_RECORD);
			
			// сохраняем запись
			// номер записи, узел
			Element recordPosition = m_DOMDoc.createElement(ScheduleRecord.ms_RecordPosition);
			m_Record.appendChild(recordPosition);
			//значение
			Text posNo = m_DOMDoc.createTextNode(String.valueOf(sr.GetRecordPosition()));
			recordPosition.appendChild(posNo);
			
			// время начала
			Element beginTime = m_DOMDoc.createElement(ScheduleRecord.ms_BeginTime);
			// время в формате 8:30
			StringBuilder sb = new StringBuilder();
			sb.append(String.valueOf(sr.GetBeginHour()));
			sb.append(':');
			sb.append(String.valueOf(sr.GetBeginMinutes()));
			// текстовое поле времени
			Text begTimeText = m_DOMDoc.createTextNode(sb.toString());
			beginTime.appendChild(begTimeText);
			m_Record.appendChild(beginTime);
			
			// время конца
			Element endTime = m_DOMDoc.createElement(ScheduleRecord.ms_EndTime);
			sb = new StringBuilder();
			sb.append(String.valueOf(sr.GetEndHour()));
			sb.append(':');
			sb.append(String.valueOf(sr.GetEndMinutes()));
			Text endTimeText = m_DOMDoc.createTextNode(sb.toString());
			endTime.appendChild(endTimeText);
			m_Record.appendChild(endTime);
			
			// название предмета
			Element subjectName = m_DOMDoc.createElement(ScheduleRecord.ms_SubjectName);
			Text sName = m_DOMDoc.createTextNode(sr.GetSubjectName());
			subjectName.appendChild(sName);
			m_Record.appendChild(subjectName);
			
			// имя преподавателя
			Element teacherFN = m_DOMDoc.createElement(ScheduleRecord.ms_TeacherFirstName);
			Text tfn = m_DOMDoc.createTextNode(sr.GetTeacherFirstName());
			teacherFN.appendChild(tfn);
			m_Record.appendChild(teacherFN);
			
			// отчество преподавателя
			Element teacherMiddleName = m_DOMDoc.createElement(ScheduleRecord.ms_TeacherMiddleName);
			Text tmn = m_DOMDoc.createTextNode(sr.GetTeacherMiddleName());
			teacherMiddleName.appendChild(tmn);
			m_Record.appendChild(teacherMiddleName);
						
			// фамилия преподавателя
			Element teacherLastName = m_DOMDoc.createElement(ScheduleRecord.ms_TeacherLastName);
			Text tln = m_DOMDoc.createTextNode(sr.GetTeacherLastName());
			teacherLastName.appendChild(tln);
			m_Record.appendChild(teacherLastName);
			
			// описание предмета (лекция/практика)
			Element lessonType = m_DOMDoc.createElement(ScheduleRecord.ms_LessonType);
			Text lt = m_DOMDoc.createTextNode(sr.GetLessonType());
			lessonType.appendChild(lt);
			m_Record.appendChild(lessonType);
			
			// номер кабиннета
			Element roomDesc = m_DOMDoc.createElement(ScheduleRecord.ms_RoomDescription);
			Text rd = m_DOMDoc.createTextNode(sr.GetRoomDescription());
			roomDesc.appendChild(rd);
			m_Record.appendChild(roomDesc);			
			
		}
		@Override
		public ScheduleRecord ReloadRecord() throws ScheduleKeepReloadException 
		{
			
			ScheduleRecordBuilder rb = new ScheduleRecordBuilder();
			rb.BuildNewRecord();
			
			// запись- элемент, значения параметров- текстовое поле
			int i = -1;

			if (m_Node.getNodeType() == Node.ELEMENT_NODE) 
			{
				// у записи есть поля
				if (!m_Node.hasChildNodes()) {
					throw new ScheduleKeepReloadException(i, "record structure is corrupt");
				}
		
				NodeList fields = m_Node.getChildNodes();
				Node field = null;	// поле в записи
				String fieldValue;
				for (int j = 0; j < fields.getLength(); ++j)
				{
					field = fields.item(j);
					// записи, элементы, их значния- их текстовые потомки
					if (field.getNodeType() != Node.ELEMENT_NODE)
						continue;		
					
					// у поля только одно текстовое значение
					try {
						fieldValue = ((Text) field.getChildNodes().item(0)).getTextContent();
			
					} catch (Exception e) {
						throw new ScheduleKeepReloadException(i, 
								"Field doesn't have a text value");
					}
		
		
					if (field.getNodeName().equals(ScheduleRecord.ms_RecordPosition)) 
					{
						Integer recPos = Integer.valueOf(fieldValue);
						rb.SetRecordPosition(recPos);
					} 
					else
					if (field.getNodeName().equals(ScheduleRecord.ms_BeginTime)) 
					{
						try {
							String[] sT = fieldValue.split(":");
							Integer t = Integer.valueOf(sT[0]); // часы
							rb.SetBeginHour(t);
							t = Integer.valueOf(sT[1]); // минуты
							rb.SetBeginMinute(t);
				
						} catch (Exception e) {
							throw new ScheduleKeepReloadException(i, 
									"Invalid begin time format");
						}
					} 
					else
						if (field.getNodeName().equals(ScheduleRecord.ms_EndTime)) 
						{
							try {
								String[] sT = fieldValue.split(":");
								Integer t = Integer.valueOf(sT[0]); // часы
								rb.SetEndHour(t);
								t = Integer.valueOf(sT[1]); // минуты
								rb.SetEndMinute(t);
				
							} catch (Exception e) {
								throw new ScheduleKeepReloadException(i, 
										"Invalid begin time format");
							}
					} 
					else
					if (field.getNodeName().equals(ScheduleRecord.ms_SubjectName)) 
					{
						rb.SetSubjectName(fieldValue);
					}
					else
					if (field.getNodeName().equals(ScheduleRecord.ms_TeacherFirstName)) 
					{ 
						rb.SetTeacherFirstName(fieldValue);
					} 
					else
					if (field.getNodeName().equals(ScheduleRecord.ms_TeacherMiddleName)) 
					{
						rb.SetTeacherMiddleName(fieldValue);
					}
					if (field.getNodeName().equals(ScheduleRecord.ms_TeacherLastName)) 
					{
						rb.SetTeacherLastName(fieldValue);
					} 
					else
					if (field.getNodeName().equals(ScheduleRecord.ms_LessonType)) 
					{
						rb.SetLessonType(fieldValue);
					}
					else 
					if (field.getNodeName().equals(ScheduleRecord.ms_RoomDescription)) 
					{
						rb.SetRoomDescription(fieldValue);
					}
				}
			}
			
						
			return rb.GetBuiltRecord();
		}
		
		//устанавливает документ XML
		public void SetScheduleDOMDoc(Document doc) {
			m_DOMDoc = doc;
		}
		
		// нужно для исключения
		public void SetRowIndex(int index) {
			row_index = index;
		}
		
		// при загрузке записи сначала устанавливаем элемент в дереве, указываюший на эту
		// запись, а затем вызываем ReloadRecord(), чтобы получить запись
		public void SetCurrRecordTreeElement(Element elem) {
			m_Record = elem;
		}
		
		
		public void SetCurrNode(Node n) {
			m_Node = n;
		}

		public Element GetBuiltRecord() {
			return m_Record;
		}
		public boolean HasRecord() { return m_Record == null; } 
		
		
		private int row_index = 0;
		private Node m_Node;
		private Element m_Record;
		private Document m_DOMDoc;  
		
		

	}
	
	
	
	
	@Override
	public void SaveSchedule( Schedule s) 	throws ScheduleKeepReloadException 
	{
		if (s == null || s.GetRecordsList().isEmpty()) {
			return;	// нечего сохранять
		}
		
		if (m_createDocument) {	
			m_doc = m_db.newDocument();
		}
		
		m_root = m_doc.createElement(TAG_SCHEDULE);
		
		if (m_createDocument)
			m_doc.appendChild(m_root);
		
		
		// создаём строитель поддерева, представляющего запись в расписании
		RecordDOMTreeBuilder treeBuilder = new RecordDOMTreeBuilder();
		// сообщаем строителю о документе
		treeBuilder.SetScheduleDOMDoc(m_doc);
		// обходим все записи, 
		for (ScheduleRecord sr : s.GetRecordsList()) 
		{
			try 
			{
				//сохраняем запись
				treeBuilder.SaveRecord(sr);
				// добавляем сохранённую запись в дерево расписания
				m_root.appendChild(treeBuilder.GetBuiltRecord());
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				// получаем номер испорченной записи
				int pos = sr.GetRecordPosition();
				// сообщаем об исключении
				throw new ScheduleKeepReloadException(pos, e.getMessage());
			}		 
		}
		
		if (m_createDocument) {
			// записываем новый файл
			m_fileMgr.EraseFile();
			// для расписания на неделю не надо вызывать
			FlushTreeOnDisc();
		}
	}

	@Override
	public Schedule ReloadSchedule() throws ScheduleKeepReloadException 
	{		
		m_fileMgr.CloseFileSession();
		
		Schedule schedule = new Schedule();	
		try {
			
			if (m_createDocument) {			
				m_doc = m_db.newDocument();
				Source source = new StreamSource(m_fileMgr.OpenReadSession());
				Result result = new DOMResult(m_doc);
				m_t.transform(source, result);
				// получаем корневой элемент загруженного дерева (он всего один)
				m_root = (Element) m_doc.getChildNodes().item(0);
			}

			if (!m_root.getNodeName().equals(TAG_SCHEDULE)) {
				throw new ScheduleKeepReloadException(-1, "root node is corrupt ");
			} 
			
			if (m_root.hasChildNodes())
			{
				RecordDOMTreeBuilder rb = new RecordDOMTreeBuilder();
				
				// получаем узлы дерева, соответствующие записям
				NodeList records = m_root.getChildNodes();
				for (int i = 0; i < records.getLength(); ++i) {
					
					Node record = records.item(i);
					if (record.getNodeType() == Node.ELEMENT_NODE)
					{
						rb.SetCurrNode(record);
						rb.SetRowIndex(i);
						schedule.AddRecord(rb.ReloadRecord());	
							
					}
				}
			}
			
		}
		catch (TransformerException te) {
			te.printStackTrace();
			throw new ScheduleKeepReloadException(-1, "iosjfojsoef");
		}
		catch (IllegalAccessException e) { // нет доступа к файлу
			e.printStackTrace();
			//
		}
		finally {
			m_fileMgr.CloseFileSession();
		}
		
		m_doc = null;
		m_root = null;
		
		return schedule;	
	}
	
	
	
	

	@Override
	public void DropSavedSchedule() throws ScheduleKeepReloadException {
		// удаляем из памяти
		m_root = null;
		m_doc = null;
		// удаляем файл
		m_fileMgr.EraseFile();		
	}
		
	
	////////////////////////////////////////////////////////////////////
	
	public XMLScheduleSaverReloader() {
		Init();
	}
	public XMLScheduleSaverReloader(String fileName) {
		Init();
		SetFileName(fileName);
	}
	
	// начальная настройка
	protected boolean Init()
	{
		// полагаем, что сохраняется расписание не на неделю
		m_createDocument = true;
		boolean error_encountered = false;
					
		// нужно установить имя файла
		m_fileMgr = new MemoryCardFileManager();
		
		try 
		{
			m_dbf = DocumentBuilderFactory.newInstance();
			m_db = m_dbf.newDocumentBuilder();
			
			// создадим во время работы
			m_doc = null;
			
			m_tf = TransformerFactory.newInstance();
			// источник и приёмник зададим во время преобразования
			m_t = m_tf.newTransformer();
			// настраиваем преобразователь xml- дерева
			m_t.setOutputProperty(OutputKeys.METHOD, "xml");
			m_t.setOutputProperty(OutputKeys.INDENT, "yes");
			// чтобы можно было сохранять пробелы
			m_t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
				
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			error_encountered = true;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			error_encountered = true;
		}
						
		return error_encountered;
	}
	
	// сохраняет расписание, представленное m_doc на диск
	// в заданный файл
	protected void FlushTreeOnDisc()
	{
		if (m_doc == null) {
			return;
		}
		// удаляем файл и создаём новый
		m_fileMgr.EraseFile();
		try {
			// источник- данные в памяти, приёмник- файл
			Source source = new DOMSource(m_doc);
			Result result = new StreamResult(m_fileMgr.OpenWriteSession());
			//записываем дерево в файл с помощью заранее настроенного 
			// преобразователя
			m_t.transform(source, result);
						
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			m_fileMgr.CloseFileSession(); // закрываем файл
		}
	}
		
	public Document GetDoc() { return m_doc; }
	public MemoryCardFileManager GetFileMgr() { return m_fileMgr; }
	
	// сначала нужно установить имя файла, а затем сохранять/загружать
	public void SetFileName(String fileName) {
		m_fileMgr.SetFileName(fileName, true);
	}
	public void EraseFile() {
		m_fileMgr.EraseFile();
	}

	
	
	// управляет файлом сохранения
	private MemoryCardFileManager m_fileMgr;
	
	// когда нужно сохранить расписание на один день в файл, то 
	// нужно создать новый документ, если же надо сохранить расписание на неделю, \
	// то документ создаётся один раз
	private boolean m_createDocument;
	
	// что нужно для DOM- xml парсера и XSLT API
	private DocumentBuilderFactory m_dbf;
	private DocumentBuilder m_db;
	private TransformerFactory m_tf;
	private Transformer m_t;
	
	
	//представляет загруженное DOM-дерево расписания
	private Document m_doc;
	// корневой элемент xml- файла
	private Element m_root;
	
	// при сохранении/загрузке интерфейс предполагает работу с единичными\
	// записями. Позволяет  связывать записи с корневым узлом дерева
	private Element m_tempRecordElement;
	
	
}
