package com.kerneldc.education.studentNotesService.repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.Student_;
import com.kerneldc.education.studentNotesService.domain.resultTransformer.StudentBasicResultTtransformer;
import com.kerneldc.education.studentNotesService.domain.resultTransformer.StudentGrpahResultTtransformer;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;


public class StudentRepositoryImpl implements StudentRepositoryCustom, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private JpaContext jpaContext;

	private EntityManager entityManager;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		entityManager = jpaContext.getEntityManagerByManagedType(Student.class);
	}
	/* (non-Javadoc)
	 * @see com.kerneldc.education.studentNotesService.repository.StudentRepositoryCustom#getStudentById(java.lang.Long)
	 */
	@Override
	public Student getStudentById(Long id) {
		EntityGraph<?> graph = entityManager.getEntityGraph("Student.noteSet");
		Map<String,Object> hints = new HashMap<>();
//		hints.put("javax.persistence.fetachgraph", graph);
		hints.put("javax.persistence.loadgraph", graph);
		return entityManager.find(Student.class, id, hints);
	}
	
	@Override
	public Student getStudentByIdWithGradeList(Long id) {
		EntityGraph<?> graph = entityManager.getEntityGraph("Student.gradeSet");
		Map<String,Object> hints = new HashMap<>();
		hints.put("javax.persistence.loadgraph", graph);
		return entityManager.find(Student.class, id, hints);
	}
	
	@Override
	public Student getStudentByIdWithNoteListAndGradeList(Long id) {
		EntityGraph<?> graph = entityManager.getEntityGraph("Student.noteSetAndGradeSet");
		Map<String,Object> hints = new HashMap<>();
		hints.put("javax.persistence.loadgraph", graph);
		return entityManager.find(Student.class, id, hints);
	}
	private List<Student> getStudents(List<Long> studentIds) {
    	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Student> criteriaQuery = builder.createQuery(Student.class);
    	Root<Student> student = criteriaQuery.from(Student.class);
    	Order firstNameOrder = builder.asc(student.get(Student_.firstName));
    	Order lastNameOrder = builder.asc(student.get(Student_.lastName));
    	criteriaQuery.select(student).distinct(true).orderBy(firstNameOrder, lastNameOrder);

    	if (studentIds != null) criteriaQuery.where(student.get(Student_.id).in(studentIds));
    	
    	TypedQuery<Student> typedQuery = entityManager.createQuery(criteriaQuery);
//    	typedQuery.setHint("javax.persistence.fetchgraph", entityManager.getEntityGraph("Student.noteList"));
    	typedQuery.setHint("javax.persistence.loadgraph", entityManager.getEntityGraph("Student.noteSetAndGradeSet"));
    	List<Student> students = typedQuery.getResultList();
		
		return students;		
	}
	
	@Override
	public List<Student> getAllStudents() {
		return getStudents(null);
	}

	@Override
	@Transactional
	public Set<Student> getLatestActiveStudents(int limit) {

		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
			"select\r\n" + 
			"	s.id student_id,\r\n" + 
			"	s.first_name,\r\n" + 
			"	s.last_name,\r\n" + 
			"	/*s.grade,*/\r\n" + 
			"	s.version student_version,\r\n" + 
			"	n.id note_id,\r\n" + 
			"	n.timestamp,\r\n" + 
			"	n.text,\r\n" + 
			"	n.version note_version\r\n" + 
			"from student s join student_note sn on s.id = sn.student_id\r\n" + 
			"			   join note n on sn.note_id = n.id\r\n" + 
			"where s.id in	(\r\n" + 
			"				select student_id\r\n" + 
			"					from ( \r\n" + 
			"						select sn.student_id, max(n.timestamp) \r\n" + 
			"						from student_note sn join note n on sn.note_id = n.id \r\n" + 
			"						group by sn.student_id \r\n" + 
			"						order by max(n.timestamp) desc \r\n" + 
			"						limit :limit \r\n" + 
			"						)\r\n" + 
			"				)\r\n" + 
			"order by first_name, last_name, timestamp"
		);
		query.addRoot("s", Student.class)
	         .addProperty("id", "student_id")
	         .addProperty("firstName", "first_name")
	         .addProperty("lastName", "last_name")
	         //.addProperty("grade", "grade")
	         .addProperty("version", "student_version");
        query.addFetch("n", "s", "noteSet")
	         .addProperty("key", "student_id")
	         .addProperty("element", "note_id")
	         .addProperty("element.id", "note_id")
	         .addProperty("element.timestamp", "timestamp")
	         .addProperty("element.text", "text")
	         .addProperty("element.version", "note_version");
        query.setParameter("limit", limit);
        
 		@SuppressWarnings("unchecked")
		List<Object[]> result = query.list();
 		// Use LinkedHashSet so as to preserve the order
 		LinkedHashSet<Student> students = result.stream().map(o->(Student)o[0]).collect(Collectors.toCollection(LinkedHashSet::new));
 		return students;
	}

	@Override
	@Transactional
	public Set<Student> getLatestActiveStudents(String username, int limit) {

		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
			"select * from user_preference_graph\r\n" + 
			" where username = :username\r\n" + 
			"   and student_id in ( \r\n" + 
			"		select student_id \r\n" + 
			"		  from (  \r\n" + 
			"			select student_id, max(timestamp)  \r\n" + 
			"			  from user_preference_graph\r\n" + 
			"			 group by student_id  \r\n" + 
			"			 order by max(timestamp) desc  \r\n" + 
			"  			 limit :limit\r\n" + 
			"			)) \r\n" + 
			" order by first_name, last_name, timestamp"
		);
		query.addRoot("s", Student.class)
	         .addProperty("id", "student_id")
	         .addProperty("firstName", "first_name")
	         .addProperty("lastName", "last_name")
	         //.addProperty("grade", "old_grade")
	         .addProperty("version", "student_version");
        query.addFetch("n", "s", "noteSet")
	         .addProperty("key", "student_id")
	         .addProperty("element", "note_id")
	         .addProperty("element.id", "note_id")
	         .addProperty("element.timestamp", "timestamp")
	         .addProperty("element.text", "text")
	         .addProperty("element.version", "note_version");
        query.addFetch("g", "s", "gradeSet")
	        .addProperty("key", "student_id")
	        .addProperty("element", "grade_id")
	        .addProperty("element.id", "grade_id")
	        .addProperty("element.student", "grade_student_id")
	        .addProperty("element.schoolYear", "grade_school_year_id")
	        .addProperty("element.grade", "grade")
	        .addProperty("element.version", "grade_version");
        
        query.setParameter("username", username);
        query.setParameter("limit", limit);
        
 		@SuppressWarnings("unchecked")
		List<Object[]> result = query.list();
 		// Use LinkedHashSet so as to preserve the order
 		LinkedHashSet<Student> students = result.stream().map(o->(Student)o[0]).collect(Collectors.toCollection(LinkedHashSet::new));
 		return students;
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	@Transactional
//	public Set<Student> getStudentsByTimestampRange(Timestamp fromTimestamp, Timestamp toTimestamp) {
//		Session session = entityManager.unwrap(Session.class);
//		SQLQuery query = session.createSQLQuery(
//			"select \n" + 
//			"	s.id student_id, \n" + 
//			"	s.first_name, \n" + 
//			"	s.last_name, \n" + 
//			"	/*s.grade,*/ \n" + 
//			"	s.version student_version, \n" + 
//			"	n.id note_id, \n" + 
//			"	n.timestamp, \n" + 
//			"	n.text, \n" + 
//			"	n.version note_version \n" + 
//			"from student s join student_note sn on s.id = sn.student_id \n" + 
//			"   join note n on sn.note_id = n.id \n" + 
//			"where n.timestamp between :fromTimestamp and :toTimestamp \n" +
//			"order by first_name, last_name, timestamp\n" 
//		);
//		query.addRoot("s", Student.class)
//	        .addProperty("id", "student_id")
//	        .addProperty("firstName", "first_name")
//	        .addProperty("lastName", "last_name")
//	        //.addProperty("grade", "grade")
//	        .addProperty("version", "student_version");
//		query.addFetch("n", "s", "noteSet")
//	        .addProperty("key", "student_id")
//	        .addProperty("element", "note_id")
//	        .addProperty("element.id", "note_id")
//	        .addProperty("element.timestamp", "timestamp")
//	        .addProperty("element.text", "text")
//	        .addProperty("element.version", "note_version");
//		query.setParameter("fromTimestamp", fromTimestamp);
//		query.setParameter("toTimestamp", toTimestamp);
// 		
//		List<Object[]> result = query.list();
// 		// Use LinkedHashSet so as to preserve the order
// 		LinkedHashSet<Student> students = result.stream().map(o->(Student)o[0]).collect(Collectors.toCollection(LinkedHashSet::new));
// 		return students;
//	}

	public List<Student> getStudentsByListOfIds(List<Long> studentIds) {
		return getStudents(studentIds);
	}
	
	@Override
	@Transactional
	public SchoolYear getStudentsByUsernameInUserPreference(String username) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
				"select * "
				+ "from user_preference_graph "
				+ "where username = :username "
				+ "order by first_name, last_name, timestamp"
		);
			query.addRoot("sy", SchoolYear.class)
				.addProperty("id", "school_year_id")
				.addProperty("schoolYear", "school_year")
				.addProperty("startDate", "start_date")
				.addProperty("endDate", "end_date")
				.addProperty("version", "school_year_version");
			query.addFetch("s", "sy", "studentSet")
				.addProperty("key", "school_year_id")
				.addProperty("element", "student_id")
				.addProperty("element.id", "student_id")
				.addProperty("element.firstName", "first_name")
				.addProperty("element.lastName", "last_name")
				//.addProperty("element.grade", "grade")
				.addProperty("element.version", "student_version");
	        query.addFetch("n", "s", "noteSet")
				.addProperty("key", "student_id")
				.addProperty("element", "note_id")
				.addProperty("element.id", "note_id")
				.addProperty("element.timestamp", "timestamp")
				.addProperty("element.text", "text")
				.addProperty("element.version", "note_version");
	        query.addFetch("g", "s", "gradeSet")
		        .addProperty("key", "student_id")
		        .addProperty("element", "grade_id")
		        .addProperty("element.id", "grade_id")
		        .addProperty("element.student", "grade_student_id")
		        .addProperty("element.schoolYear", "grade_school_year_id")
		        .addProperty("element.grade", "grade")
		        .addProperty("element.version", "grade_version");
	        query.setParameter("username", username);
	 		@SuppressWarnings("unchecked")
			List<Object[]> result = query.list();
	 		LOGGER.debug("result.size(): {}", result.size());
			LOGGER.debug("result: {}", result);
	 		// Use LinkedHashSet so as to preserve the order
	 		LinkedHashSet<SchoolYear> schoolYears = result.stream()
	 				.map(o->(SchoolYear)o[0])
	 				.collect(Collectors.toCollection(LinkedHashSet::new));
	 		return schoolYears.iterator().next();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<StudentDto> getStudentDtosInSchoolYear(Long schoolYearId) {
		Session session = entityManager.unwrap(Session.class);
		return session
				.createSQLQuery("select s.id, s.first_name as firstName, s.last_name as lastName, /*s.grade,*/ s.version\n"
						+ "  from student s\n" + " where exists (select 1\n"
						+ "                 from student_school_year ssy\n"
						+ "                where ssy.student_id = s.id and ssy.school_year_id = :school_year_id)\n"
						+ " order by first_name, last_name")
				.addScalar("id", StandardBasicTypes.LONG).addScalar("firstName").addScalar("lastName")
				.addScalar("version", StandardBasicTypes.LONG).setParameter("school_year_id", schoolYearId)
				.setResultTransformer(new AliasToBeanResultTransformer(StudentDto.class)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<StudentDto> getStudentDtosNotInSchoolYear(Long schoolYearId) {
		Session session = entityManager.unwrap(Session.class);
		return session
				.createSQLQuery("select s.id, s.first_name as firstName, s.last_name as lastName, /*s.grade,*/ s.version\n"
						+ "  from student s\n" + " where not exists (select 1\n"
						+ "                 from student_school_year ssy\n"
						+ "                where ssy.student_id = s.id and ssy.school_year_id = :school_year_id)\n"
						+ " order by first_name, last_name")
				.addScalar("id", StandardBasicTypes.LONG).addScalar("firstName").addScalar("lastName")
				.addScalar("version", StandardBasicTypes.LONG).setParameter("school_year_id", schoolYearId)
				.setResultTransformer(new AliasToBeanResultTransformer(StudentDto.class)).list();
	}
	
//	@Override
//	@Transactional
//	public List<StudentUiDto> getStudentsByUsername(String username) {
//		Session session = entityManager.unwrap(Session.class);
//		SQLQuery query = session.createSQLQuery(
//				"select * "
//				+ "from STUDENT_GRAPH "
//				+ "where username = :username "
//				+ "order by first_name, last_name, timestamp"
//		);
//		query.setParameter("username", username);
//		setDataTyes(query);
//		query.setResultTransformer(new StudentGrpahResultTtransformer()/*new StudentUiDtoResultTtransformer()*/);
//		@SuppressWarnings("unchecked")
//		List<StudentUiDto> result = query.list();
// 		LOGGER.debug("result.size(): {}", result.size());
//		LOGGER.debug("result: {}", result);
//		return result; //squery.list();
//	}

	@Override
	@Transactional
	public List<StudentUiDto> getStudentsInSchoolYear(Long schoolYearId) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
				"select s.id, s.first_name, s.last_name, s.version\n" + 
				"from student s join student_school_year ssy on s.id = ssy.student_id join school_year sy on ssy.school_year_id = sy.id\n" + 
				"where sy.id = :schoolYearId\n" + 
				"order by first_name, last_name"
		);
		query.setParameter("schoolYearId", schoolYearId);
		setGetStudentsInSchoolYear2DataTyes(query);
		query.setResultTransformer(new StudentBasicResultTtransformer());
		@SuppressWarnings("unchecked")
		List<StudentUiDto> result = query.list();
 		LOGGER.debug("result.size(): {}", result.size());
		return result; //squery.list();
	}

	@Override
	@Transactional
	public List<StudentUiDto> getStudentsNotInSchoolYear(Long schoolYearId) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
				"select s.id, s.first_name, s.last_name, s.version from student s\n" + 
				"minus\n" + 
				"select s.id, s.first_name, s.last_name, s.version \n" + 
				"from student s join student_school_year ssy on s.id = ssy.student_id join school_year sy on ssy.school_year_id = sy.id \n" + 
				"where sy.id = :schoolYearId \n" + 
				"order by first_name, last_name"
		);
		query.setParameter("schoolYearId", schoolYearId);
		setGetStudentsInSchoolYear2DataTyes(query);
		query.setResultTransformer(new StudentBasicResultTtransformer());
		@SuppressWarnings("unchecked")
		List<StudentUiDto> result = query.list();
 		LOGGER.debug("result.size(): {}", result.size());
		return result; //squery.list();
	}
	
	@Override
	@Transactional
	public List<StudentUiDto> getStudentsByTimestampRange(Long schoolYearId, Timestamp fromTimestamp, Timestamp toTimestamp) {
		LOGGER.debug("fromTimestamp: {}, toTimestamp: {}", fromTimestamp, toTimestamp);
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
				"select * "
				+ "from STUDENT_GRAPH "
				+ "where school_year_id = :schoolYearId "
				+ "  and timestamp >= :fromTimestamp and timestamp <= :toTimestamp "
				+ "order by first_name, last_name, timestamp"
		);
		query.setParameter("schoolYearId", schoolYearId);
		query.setParameter("fromTimestamp", fromTimestamp);
		query.setParameter("toTimestamp", toTimestamp);
		setStudentGraphDataTyes(query);
		query.setResultTransformer(new StudentGrpahResultTtransformer()/*new StudentUiDtoResultTtransformer()*/);
		@SuppressWarnings("unchecked")
		List<StudentUiDto> result = query.list();
 		LOGGER.debug("result.size(): {}", result.size());
		//LOGGER.debug("result: {}", result);
		return result; //squery.list();
	}

	@Override
	@Transactional
	public List<StudentUiDto> getStudentsBySchoolYearIdAndListOfIds(Long schoolYearId, List<Long> studentIds) {
		LOGGER.debug("schoolYearId: {}, studentIds: {}", schoolYearId, studentIds);
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
				"select * "
				+ "from STUDENT_GRAPH "
				+ "where school_year_id = :schoolYearId "
				+ "  and student_id in (:studentIds) "
				+ "order by first_name, last_name, timestamp"
		);
		query.setParameter("schoolYearId", schoolYearId);
		query.setParameterList("studentIds", studentIds);
		setStudentGraphDataTyes(query);
		query.setResultTransformer(new StudentGrpahResultTtransformer()/*new StudentUiDtoResultTtransformer()*/);
		@SuppressWarnings("unchecked")
		List<StudentUiDto> result = query.list();
 		LOGGER.debug("result.size(): {}", result.size());
		return result;
	}

	@Override
	@Transactional
	public List<StudentUiDto> getStudentGraphBySchoolYear(Long schoolYearId) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
				"select * "
				+ "from STUDENT_GRAPH "
				+ "where school_year_id = :schoolYearId "
				+ "order by first_name, last_name, timestamp"
		);
		query.setParameter("schoolYearId", schoolYearId);
		setStudentGraphDataTyes(query);
		query.setResultTransformer(new StudentGrpahResultTtransformer());
		@SuppressWarnings("unchecked")
		List<StudentUiDto> result = query.list();
 		LOGGER.debug("result.size(): {}", result.size());
		//LOGGER.debug("result: {}", result);
		return result; //squery.list();
	}

	private void setDataTyes(SQLQuery query) {
		query.addScalar("username", StandardBasicTypes.STRING).addScalar("student_id", StandardBasicTypes.LONG)
				.addScalar("first_name", StandardBasicTypes.STRING).addScalar("last_name", StandardBasicTypes.STRING)
				.addScalar("grade_id", StandardBasicTypes.LONG).addScalar("grade", StandardBasicTypes.STRING)
				.addScalar("grade_version", StandardBasicTypes.LONG).addScalar("note_id", StandardBasicTypes.LONG)
				.addScalar("timestamp", StandardBasicTypes.TIMESTAMP).addScalar("text", StandardBasicTypes.STRING)
				.addScalar("note_version", StandardBasicTypes.LONG).addScalar("school_year_id", StandardBasicTypes.LONG)
				.addScalar("school_year", StandardBasicTypes.STRING).addScalar("start_date", StandardBasicTypes.DATE)
				.addScalar("end_date", StandardBasicTypes.DATE)
				.addScalar("school_year_version", StandardBasicTypes.LONG)
				.addScalar("student_version", StandardBasicTypes.LONG);
	}
	private void setStudentGraphDataTyes(SQLQuery query) {
		query.addScalar("student_id", StandardBasicTypes.LONG)
				.addScalar("first_name", StandardBasicTypes.STRING).addScalar("last_name", StandardBasicTypes.STRING)
				.addScalar("grade_id", StandardBasicTypes.LONG).addScalar("grade", StandardBasicTypes.STRING)
				.addScalar("grade_version", StandardBasicTypes.LONG).addScalar("note_id", StandardBasicTypes.LONG)
				.addScalar("timestamp", StandardBasicTypes.TIMESTAMP).addScalar("text", StandardBasicTypes.STRING)
				.addScalar("note_version", StandardBasicTypes.LONG).addScalar("school_year_id", StandardBasicTypes.LONG)
				.addScalar("school_year", StandardBasicTypes.STRING).addScalar("start_date", StandardBasicTypes.DATE)
				.addScalar("end_date", StandardBasicTypes.DATE)
				.addScalar("school_year_version", StandardBasicTypes.LONG)
				.addScalar("student_version", StandardBasicTypes.LONG);
	}
	private void setGetStudentsInSchoolYear2DataTyes(SQLQuery query) {
		query.addScalar("id", StandardBasicTypes.LONG).addScalar("first_name", StandardBasicTypes.STRING)
				.addScalar("last_name", StandardBasicTypes.STRING).addScalar("version", StandardBasicTypes.LONG);
	}
}
