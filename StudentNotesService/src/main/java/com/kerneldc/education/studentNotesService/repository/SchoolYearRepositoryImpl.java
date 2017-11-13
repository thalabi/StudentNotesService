package com.kerneldc.education.studentNotesService.repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;

public class SchoolYearRepositoryImpl implements SchoolYearRepositoryCustom, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private JpaContext jpaContext;

	private EntityManager entityManager;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		entityManager = jpaContext.getEntityManagerByManagedType(SchoolYear.class);
	}

//	@Override
//	@Transactional
//	public Set<SchoolYear> getLatestActiveStudentsBySchoolYearId(Long schoolYearId, int limit) {
//
//		Session session = entityManager.unwrap(Session.class);
//		SQLQuery query = session.createSQLQuery(
//			"select\r\n" + 
//					"	sy.id school_year_id,\r\n" + 
//					"	sy.school_year,\r\n" + 
//					"	sy.start_date,\r\n" + 
//					"	sy.end_date,\r\n" + 
//					"	sy.version school_year_version,\r\n" + 
//					"	s.id student_id,\r\n" + 
//					"	s.first_name,\r\n" + 
//					"	s.last_name,\r\n" + 
//					"	s.grade,\r\n" + 
//					"	s.version student_version,\r\n" + 
//					"	n.id note_id,\r\n" + 
//					"	n.timestamp,\r\n" + 
//					"	n.text,\r\n" + 
//					"	n.version note_version\r\n" + 
//			"from school_year sy\r\n" + 
//			"left outer join student_school_year ssy\r\n" + 
//			"on sy.id = ssy.school_year_id\r\n" + 
//			"join student s\r\n" + 
//			"on ssy.student_id = s.id\r\n" + 
//			"left outer join student_note sn\r\n" + 
//			"on s.id = sn.student_id\r\n" + 
//			"left outer join note n\r\n" + 
//			"on sn.note_id = n.id\r\n" + 
//			"where sy.id = :schoolYearId\r\n" + 
//			"and (n.timestamp is null or n.timestamp between sy.start_date and sy.end_date)\r\n" +
//
//			"and s.id in	(\r\n" + 
//			"				select student_id\r\n" + 
//			"					from ( \r\n" + 
//			"						select sn.student_id, max(n.timestamp) \r\n" + 
//			"						from student_note sn join note n on sn.note_id = n.id\r\n" +
//			"											 join student_school_year ssy on sn.student_id = ssy.student_id\r\n" + 
//			"                                            join school_year sy on ssy.school_year_id = sy.id\r\n" +
//			"                       where ssy.school_year_id = :schoolYearId\r\n" + 
//			"                         and n.timestamp between sy.start_date and sy.end_date\r\n" +
//			"						group by sn.student_id \r\n" + 
//			"						order by max(n.timestamp) desc \r\n" + 
//			"						limit :limit \r\n" + 
//			"						)\r\n" + 
//			"				)\r\n" + 
//			
//			
//			"order by s.first_name, s.last_name, n.timestamp"
//			);
//		query.addRoot("sy", SchoolYear.class)
//			.addProperty("id", "school_year_id")
//			.addProperty("schoolYear", "school_year")
//			.addProperty("startDate", "start_date")
//			.addProperty("endDate", "end_date")
//			.addProperty("version", "school_year_version");
//		query.addFetch("s", "sy", "studentSet")
//			.addProperty("key", "school_year_id")
//			.addProperty("element", "student_id")
//			.addProperty("element.id", "student_id")
//			.addProperty("element.firstName", "first_name")
//			.addProperty("element.lastName", "last_name")
//			.addProperty("element.grade", "grade")
//			.addProperty("element.version", "student_version");
//        query.addFetch("n", "s", "noteSet")
//			.addProperty("key", "student_id")
//			.addProperty("element", "note_id")
//			.addProperty("element.id", "note_id")
//			.addProperty("element.timestamp", "timestamp")
//			.addProperty("element.text", "text")
//			.addProperty("element.version", "note_version");
//		LOGGER.debug("schoolYearId: {}, limit: {}", schoolYearId, limit);
//        query.setParameter("schoolYearId", schoolYearId);
//        query.setParameter("limit", limit);
//
//        
// 		@SuppressWarnings("unchecked")
//		List<Object[]> result = query.list();
// 		LOGGER.debug("result.size(): {}", result.size());
//		LOGGER.debug("result: {}", result);
// 		// Use LinkedHashSet so as to preserve the order
// 		LinkedHashSet<SchoolYear> schoolYears = result.stream()
// 				.map(o->(SchoolYear)o[0])
// 				.collect(Collectors.toCollection(LinkedHashSet::new));
// 		LOGGER.debug("schoolYears.size(): {}", schoolYears.size());
//		LOGGER.debug("schoolYears: {}", schoolYears);
// 		return schoolYears;
//	}

}
