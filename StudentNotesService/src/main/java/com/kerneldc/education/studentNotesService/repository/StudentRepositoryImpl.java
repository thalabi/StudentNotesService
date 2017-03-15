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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.Student_;


public class StudentRepositoryImpl implements StudentRepositoryCustom, InitializingBean {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

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
		EntityGraph<?> graph = entityManager.getEntityGraph("Student.noteList");
		Map<String,Object> hints = new HashMap<>();
//		hints.put("javax.persistence.fetachgraph", graph);
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
    	typedQuery.setHint("javax.persistence.loadgraph", entityManager.getEntityGraph("Student.noteList"));
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

		/*		
		Query q = entityManager.createNativeQuery(
			"select s.*, sn.*, n.* from student s join student_note sn on s.id = sn.student_id join note n on sn.note_id = n.id where s.id in (\r\n" + 
			"select student_id from (\r\n" + 
			"select sn.student_id, max(n.timestamp)\r\n" + 
			"from student_note sn join note n on sn.note_id = n.id\r\n" + 
			"group by sn.student_id\r\n" + 
			"order by max(n.timestamp) desc\r\n" + 
			"limit :limit\r\n" + 
			")\r\n" + ")", Student.class);
		q.setParameter("limit", limit);
		@SuppressWarnings("unchecked")
		List<Student> students = (List<Student>)q.getResultList();
		LOGGER.debug("students.size(): {}", students.size());
	    for (Student s: students) {
	    	LOGGER.debug("s.getNoteList().size(): {}", s.getNoteList().size());
	    	for (Note n: s.getNoteList())
	    		;
	    }
	    return students;
*/	    
		
		Session session = entityManager.unwrap(Session.class);
//		SQLQuery query = session.createSQLQuery(
//				"select s.*, n.*, sn.* from student s join student_note sn on s.id = sn.student_id join note n on sn.note_id = n.id where s.id in (\r\n" + 
//				"select student_id from (\r\n" + 
//				"select sn.student_id, max(n.timestamp)\r\n" + 
//				"from student_note sn join note n on sn.note_id = n.id\r\n" + 
//				"group by sn.student_id\r\n" + 
//				"order by max(n.timestamp) desc\r\n" + 
//				"limit 5\r\n" + 
//				")\r\n" + ")"				);
//		query.addEntity("s", Student.class);
//		query.addFetch("n", "s", "noteList");
//		List<Student> lo = (List<Student>)query.list();
//		LOGGER.debug("lo.size(): {}", lo.size());
//		return lo;

		SQLQuery query = session.createSQLQuery(
			"select\r\n" + 
			"	s.id student_id,\r\n" + 
			"	s.first_name,\r\n" + 
			"	s.last_name,\r\n" + 
			"	s.grade,\r\n" + 
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
	         .addProperty("grade", "grade")
	         .addProperty("version", "student_version");
        query.addFetch("n", "s", "noteList")
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

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Set<Student> getStudentsByTimestampRange(Timestamp fromTimestamp, Timestamp toTimestamp) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(
			"select \n" + 
			"	s.id student_id, \n" + 
			"	s.first_name, \n" + 
			"	s.last_name, \n" + 
			"	s.grade, \n" + 
			"	s.version student_version, \n" + 
			"	n.id note_id, \n" + 
			"	n.timestamp, \n" + 
			"	n.text, \n" + 
			"	n.version note_version \n" + 
			"from student s join student_note sn on s.id = sn.student_id \n" + 
			"   join note n on sn.note_id = n.id \n" + 
			"where n.timestamp between :fromTimestamp and :toTimestamp \n" +
			"order by first_name, last_name, timestamp\n" 
		);
		query.addRoot("s", Student.class)
	        .addProperty("id", "student_id")
	        .addProperty("firstName", "first_name")
	        .addProperty("lastName", "last_name")
	        .addProperty("grade", "grade")
	        .addProperty("version", "student_version");
		query.addFetch("n", "s", "noteList")
	        .addProperty("key", "student_id")
	        .addProperty("element", "note_id")
	        .addProperty("element.id", "note_id")
	        .addProperty("element.timestamp", "timestamp")
	        .addProperty("element.text", "text")
	        .addProperty("element.version", "note_version");
		query.setParameter("fromTimestamp", fromTimestamp);
		query.setParameter("toTimestamp", toTimestamp);
 		
		List<Object[]> result = query.list();
 		// Use LinkedHashSet so as to preserve the order
 		LinkedHashSet<Student> students = result.stream().map(o->(Student)o[0]).collect(Collectors.toCollection(LinkedHashSet::new));
 		return students;
	}

	public List<Student> getStudentsByListOfIds(List<Long> studentIds) {
		return getStudents(studentIds);
	}
}
