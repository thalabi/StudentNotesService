package com.kerneldc.education.studentNotesService.repository;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;


public class SchoolYearRepositoryImpl implements SchoolYearRepositoryCustom, InitializingBean {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private JpaContext jpaContext;

	private EntityManager entityManager;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		entityManager = jpaContext.getEntityManagerByManagedType(SchoolYear.class);
	}
	/* (non-Javadoc)
	 * @see com.kerneldc.education.studentNotesService.repository.StudentRepositoryCustom#getStudentById(java.lang.Long)
	 */
	@Override
	public SchoolYear getSchoolYearById(Long id) {
		EntityGraph<?> graph = entityManager.getEntityGraph("SchoolYear.studentSet");
		Map<String,Object> hints = new HashMap<>();
//		hints.put("javax.persistence.fetachgraph", graph);
		hints.put("javax.persistence.loadgraph", graph);
		return entityManager.find(SchoolYear.class, id, hints);
	}
}
