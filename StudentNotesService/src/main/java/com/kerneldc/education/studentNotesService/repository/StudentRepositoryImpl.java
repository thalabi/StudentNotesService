package com.kerneldc.education.studentNotesService.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import com.kerneldc.education.studentNotes.domain.Student_;
import com.kerneldc.education.studentNotesService.domain.Student;

public class StudentRepositoryImpl implements StudentRepositoryCustom {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private JpaContext jpaContext;
	
	@Override
	public Student getStudentById(Long id) {
		EntityManager entityManager = jpaContext.getEntityManagerByManagedType(Student.class);
		EntityGraph<?> graph = entityManager.getEntityGraph("Student.noteList");
		Map<String,Object> hints = new HashMap<>();
//		hints.put("javax.persistence.fetachgraph", graph);
		hints.put("javax.persistence.loadgraph", graph);
		return entityManager.find(Student.class, id, hints);
	}
	
	@Override
	public List<Student> getAllStudents() {
		
		EntityManager entityManager = jpaContext.getEntityManagerByManagedType(Student.class); 
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
		EntityManager entityManager = jpaContext.getEntityManagerByManagedType(Student.class);
		LOGGER.debug("student: {}, detachedStudent: {}", student, detachedStudent);
		return entityManager.merge(student);
	}
}
