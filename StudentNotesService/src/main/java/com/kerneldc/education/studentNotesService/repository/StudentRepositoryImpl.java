package com.kerneldc.education.studentNotesService.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.Student_;


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
		EntityGraph<?> graph = entityManager.getEntityGraph("Student.noteList");
		Map<String,Object> hints = new HashMap<>();
//		hints.put("javax.persistence.fetachgraph", graph);
		hints.put("javax.persistence.loadgraph", graph);
		return entityManager.find(Student.class, id, hints);
	}
	
	@Override
	public List<Student> getAllStudents() {
		
    	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Student> criteriaQuery = builder.createQuery(Student.class);
    	Root<Student> student = criteriaQuery.from(Student.class);
    	Order lastNameOrder = builder.asc(student.get(Student_.lastName));
    	Order firstNameOrder = builder.asc(student.get(Student_.firstName));
    	//Order timestampOrder =  builder.asc(student.join(Student_.noteList, JoinType.LEFT).get(Note_.timestamp));
    	//Order idOrder =  builder.asc(student.join(Student_.noteSet).get(Note_.Id));
    	criteriaQuery.select(student).distinct(true).orderBy(lastNameOrder, firstNameOrder/*, timestampOrder*/);
    	
    	TypedQuery<Student> typedQuery = entityManager.createQuery(criteriaQuery);
//    	typedQuery.setHint("javax.persistence.fetchgraph", entityManager.getEntityGraph("Student.noteList"));
    	typedQuery.setHint("javax.persistence.loadgraph", entityManager.getEntityGraph("Student.noteList"));
    	List<Student> students = typedQuery.getResultList();
		
		return students;
	}

	@Override
	//@Transactional(propagation=Propagation.NESTED)
	public Student updateStudent(Student detachedStudent) {
		
		Student student = getStudentById(detachedStudent.getId());
		BeanUtils.copyProperties(detachedStudent, student);
		LOGGER.debug("student: {}, detachedStudent: {}", student, detachedStudent);
		return entityManager.merge(student);
	}
	
	@Override
	@Transactional
	public List<Student> getLatestActiveStudents(int limit) {

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
				"select distinct " +
                        "s.id student_id, " +
                        "s.first_name, " +
                        "s.last_name, " +
                        "s.grade, " +
                        "s.version student_version, " +
                        "n.id note_id, " +
                        "n.timestamp, " +
                        "n.text, " +
                        "n.version note_version " +
                        "from student s join student_note sn on s.id = sn.student_id join note n on sn.note_id = n.id where s.id in (\r\n" +
        				"select student_id from (\r\n" + 
        				"select sn.student_id, max(n.timestamp)\r\n" + 
        				"from student_note sn join note n on sn.note_id = n.id\r\n" + 
        				"group by sn.student_id\r\n" + 
        				"order by max(n.timestamp) desc\r\n" + 
        				"limit 5\r\n" + 
        				")\r\n" + ")"				);
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
 		List<Object[]> result = query.list();
 		LOGGER.debug("result.size(): {}", result.size());
 		Set<Student> students = new HashSet<>();
 		for (Object[] tuple : result) {
 			Student s = (Student)tuple[0];
 			students.add(s);
 			LOGGER.debug("s.getId(): {}, s.getNoteList().size(): {}, ((Note)tuple[1]).getId()", s.getId(), s.getNoteList().size(), ((Note)tuple[1]).getId());
 		}
 		LOGGER.debug("students.size(): {}", students.size());
 		
 		return new ArrayList(students);
	}

}