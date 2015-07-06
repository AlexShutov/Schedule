package com.SFEDU.schedule_1;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.SFEDU.schedule_1.Schedule.ScheduleRecord;
import com.SFEDU.schedule_1.Schedule.ScheduleRecord.ScheduleRecordBuilder;

public class XMLScheduleSaverReloader
	implements IScheduleKeeperReloader  {
	/**
	 * name of the root element
	 */
	private static final String TAG_SCHEDULE = "SCHEDULE";
	private static final String TAG_SCHEDULE_RECORD = "RECORD";
	
	public class XMLRecordSaverReloader 
		implements IScheduleRecordKeeperReloader  {
		/**
		 * Converts schedule record into String
		 */
		@Override
		public void SaveRecord(ScheduleRecord r) throws ScheduleKeepReloadException {
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

	/**
	 * The builder for the subtree of schedule record. Saves record. DOM document must be
	 * created before using it. Implementation in XMLScheduleSaver Reloader allows to save a single
	 * record in existing tree, removing and rewriting the file. This builder just creates DOM- tree.
	 */
	protected static class RecordDOMTreeBuilder
		implements IScheduleRecordKeeperReloader  {
		@Override
		public void SaveRecord(ScheduleRecord sr) throws ScheduleKeepReloadException  {
			/**
			 * Create a new node
			 */
			m_Record = m_DOMDoc.createElement(TAG_SCHEDULE_RECORD);

			/**
			 * save record, record no, the node
			 */
			Element recordPosition = m_DOMDoc.createElement(ScheduleRecord.ms_RecordPosition);
			m_Record.appendChild(recordPosition);
			/**
			 * value
			 */
			Text posNo = m_DOMDoc.createTextNode(String.valueOf(sr.GetRecordPosition()));
			recordPosition.appendChild(posNo);

			/**
			 * begin time
			 */
			Element beginTime = m_DOMDoc.createElement(ScheduleRecord.ms_BeginTime);
			/**
			 * time in format 8:30
			 */
			StringBuilder sb = new StringBuilder();
			sb.append(String.valueOf(sr.GetBeginHour()));
			sb.append(':');
			sb.append(String.valueOf(sr.GetBeginMinutes()));
			/**
			 * time text field
			 */
			Text begTimeText = m_DOMDoc.createTextNode(sb.toString());
			beginTime.appendChild(begTimeText);
			m_Record.appendChild(beginTime);

			/**
			 * end time
			 */
			Element endTime = m_DOMDoc.createElement(ScheduleRecord.ms_EndTime);
			sb = new StringBuilder();
			sb.append(String.valueOf(sr.GetEndHour()));
			sb.append(':');
			sb.append(String.valueOf(sr.GetEndMinutes()));
			Text endTimeText = m_DOMDoc.createTextNode(sb.toString());
			endTime.appendChild(endTimeText);
			m_Record.appendChild(endTime);

			/**
			 * subject name
			 */
			Element subjectName = m_DOMDoc.createElement(ScheduleRecord.ms_SubjectName);
			Text sName = m_DOMDoc.createTextNode(sr.GetSubjectName());
			subjectName.appendChild(sName);
			m_Record.appendChild(subjectName);

			/**
			 * teacher first name
			 */
			Element teacherFN = m_DOMDoc.createElement(ScheduleRecord.ms_TeacherFirstName);
			Text tfn = m_DOMDoc.createTextNode(sr.GetTeacherFirstName());
			teacherFN.appendChild(tfn);
			m_Record.appendChild(teacherFN);

			/**
			 * teacher middle name
			 */
			Element teacherMiddleName = m_DOMDoc.createElement(ScheduleRecord.ms_TeacherMiddleName);
			Text tmn = m_DOMDoc.createTextNode(sr.GetTeacherMiddleName());
			teacherMiddleName.appendChild(tmn);
			m_Record.appendChild(teacherMiddleName);

			/**
			 * teacher last name
			 */
			Element teacherLastName = m_DOMDoc.createElement(ScheduleRecord.ms_TeacherLastName);
			Text tln = m_DOMDoc.createTextNode(sr.GetTeacherLastName());
			teacherLastName.appendChild(tln);
			m_Record.appendChild(teacherLastName);

			/**
			 * lesson description
			 */
			Element lessonType = m_DOMDoc.createElement(ScheduleRecord.ms_LessonType);
			Text lt = m_DOMDoc.createTextNode(sr.GetLessonType());
			lessonType.appendChild(lt);
			m_Record.appendChild(lessonType);

			/**
			 * room No
			 */
			Element roomDesc = m_DOMDoc.createElement(ScheduleRecord.ms_RoomDescription);
			Text rd = m_DOMDoc.createTextNode(sr.GetRoomDescription());
			roomDesc.appendChild(rd);
			m_Record.appendChild(roomDesc);			
			
		}
		@Override
		public ScheduleRecord ReloadRecord() throws ScheduleKeepReloadException {
			ScheduleRecordBuilder rb = new ScheduleRecordBuilder();
			rb.BuildNewRecord();
			/**
			 * the record is element, params values- text field
			 */
			int i = -1;
			if (m_Node.getNodeType() == Node.ELEMENT_NODE)  {
				if (!m_Node.hasChildNodes()) {
					throw new ScheduleKeepReloadException(i, "record structure is corrupt");
				}
		
				NodeList fields = m_Node.getChildNodes();
				Node field = null;
				String fieldValue;
				for (int j = 0; j < fields.getLength(); ++j)
				{
					field = fields.item(j);
					/**
					 * records, elements, their values- text children
					 */
					if (field.getNodeType() != Node.ELEMENT_NODE)
						continue;
					try {
						fieldValue = ((Text) field.getChildNodes().item(0)).getTextContent();
			
					} catch (Exception e) {
						throw new ScheduleKeepReloadException(i, 
								"Field doesn't have a text value");
					}
		
		
					if (field.getNodeName().equals(ScheduleRecord.ms_RecordPosition)) {
						Integer recPos = Integer.valueOf(fieldValue);
						rb.SetRecordPosition(recPos);
					} 
					else
					if (field.getNodeName().equals(ScheduleRecord.ms_BeginTime))  {
						try {
							String[] sT = fieldValue.split(":");
							/**
							 * hours
							 */
							Integer t = Integer.valueOf(sT[0]);
							rb.SetBeginHour(t);
							/**
							 * minutes
							 */
							t = Integer.valueOf(sT[1]);
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

		/**
		 * sets XML document
		 * @param doc
		 */
		public void SetScheduleDOMDoc(Document doc) {
			m_DOMDoc = doc;
		}

		/**
		 * used for exception handling
		 * @param index
		 */
		public void SetRowIndex(int index) {
			row_index = index;
		}

		/**
		 * During reloading first set element in the tree, pointing at this record and then
		 * call ReloadRecord() to obtain record
		 * @param elem
		 */
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
	public void SaveSchedule( Schedule s) 	throws ScheduleKeepReloadException  {
		if (s == null || s.GetRecordsList().isEmpty()) {
			return;
		}
		if (m_createDocument) {	
			m_doc = m_db.newDocument();
		}
		m_root = m_doc.createElement(TAG_SCHEDULE);
		
		if (m_createDocument)
			m_doc.appendChild(m_root);

		/**
		 * create subtree biolder
		 */
		RecordDOMTreeBuilder treeBuilder = new RecordDOMTreeBuilder();
		treeBuilder.SetScheduleDOMDoc(m_doc);
		/**
		 * iterate over all records
		 */
		for (ScheduleRecord sr : s.GetRecordsList()) {
			try {
				/**
				 * try to save record
				 */
				treeBuilder.SaveRecord(sr);
				/**
				 * add saved record to schedule tree
				 */
				m_root.appendChild(treeBuilder.GetBuiltRecord());
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				/**
				 * obtain number of currupt record
				 */
				int pos = sr.GetRecordPosition();
				throw new ScheduleKeepReloadException(pos, e.getMessage());
			}		 
		}
		if (m_createDocument) {
			/**
			 * create a new file
			 */
			m_fileMgr.eraseFile();
			FlushTreeOnDisc();
		}
	}

	@Override
	public Schedule ReloadSchedule() throws ScheduleKeepReloadException {
		m_fileMgr.closeFileSession();
		Schedule schedule = new Schedule();	
		try {
			
			if (m_createDocument) {			
				m_doc = m_db.newDocument();
				Source source = new StreamSource(m_fileMgr.openReadSession());
				Result result = new DOMResult(m_doc);
				m_t.transform(source, result);
				/**
				 * get tree root
				 */
				m_root = (Element) m_doc.getChildNodes().item(0);
			}
			if (!m_root.getNodeName().equals(TAG_SCHEDULE)) {
				throw new ScheduleKeepReloadException(-1, "root node is corrupt ");
			}
			if (m_root.hasChildNodes())
			{
				RecordDOMTreeBuilder rb = new RecordDOMTreeBuilder();
				/**
				 * get tree nodes, corresponding to records
				 */
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
			throw new ScheduleKeepReloadException(-1, "Can't save of reload");
		}
		/**
		 * no file access
		 */
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		finally {
			m_fileMgr.closeFileSession();
		}
		m_doc = null;
		m_root = null;
		return schedule;	
	}

	/**
	 * clears memory references and erases file
	 * @throws ScheduleKeepReloadException
	 */
	@Override
	public void DropSavedSchedule() throws ScheduleKeepReloadException {
		m_root = null;
		m_doc = null;
		m_fileMgr.eraseFile();
	}

	public XMLScheduleSaverReloader() {
		Init();
	}
	public XMLScheduleSaverReloader(String fileName) {
		Init();
		SetFileName(fileName);
	}

	/**
	 * Initial setup
	 * @return boolean is everything ok
	 */
	protected boolean Init() {
		/**
		 * suppose, schedule isn't for a week
		 */
		m_createDocument = true;
		boolean error_encountered = false;
		/**
		 * need to set filename
		 */
		m_fileMgr = new MemoryCardFileManager();
		
		try 
		{
			m_dbf = DocumentBuilderFactory.newInstance();
			m_db = m_dbf.newDocumentBuilder();

			/**
			 * will be set during transform
			 */
			m_doc = null;
			
			m_tf = TransformerFactory.newInstance();
			/**
			 * source and receiver will be set during transform
			 */
			m_t = m_tf.newTransformer();
			/**
			 * customize xml- tree transformer
			 */
			m_t.setOutputProperty(OutputKeys.METHOD, "xml");
			m_t.setOutputProperty(OutputKeys.INDENT, "yes");
			/**
			 * add whitespace support
			 */
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

	/**
	 * Saves a schedule referenced by m_doc on disc in a given file
	 */
	protected void FlushTreeOnDisc()
	{
		if (m_doc == null) {
			return;
		}
		/**
		 * erase old file
		 */
		m_fileMgr.eraseFile();
		try {
			/**
			 * source is in a memory, receiver- the file
			 */
			Source source = new DOMSource(m_doc);
			Result result = new StreamResult(m_fileMgr.openWriteSession());
			/**
			 * write tree by customized transformer
			 */
			m_t.transform(source, result);
						
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			m_fileMgr.closeFileSession(); // закрываем файл
		}
	}
		
	public Document GetDoc() { return m_doc; }
	public MemoryCardFileManager GetFileMgr() { return m_fileMgr; }

	/**
	 * you need to set filename first, and then save(reload)
	 * @param fileName
	 */
	public void SetFileName(String fileName) {
		m_fileMgr.setFileName(fileName, true);
	}
	public void EraseFile() {
		m_fileMgr.eraseFile();
	}


	/**
	 * checks memcard availiability and opens streams
	 */
	private MemoryCardFileManager m_fileMgr;

	/**
	 * for saving single day new document needs to be created, for saving the week
	 * document are created just once for all days
	 */
	private boolean m_createDocument;

	/**
	 * used by DOM- perser and  XSLT API
	 */
	private DocumentBuilderFactory m_dbf;
	private DocumentBuilder m_db;
	private TransformerFactory m_tf;
	private Transformer m_t;

	/**
	 * loaded tree and root element
	 */
	private Document m_doc;
	private Element m_root;

	
}
