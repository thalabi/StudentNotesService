package com.kerneldc.education.studentNotesService.repository.util;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.AbstractPersistableEntity;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;

public class RepositoryUtils {

	private RepositoryUtils() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	}

	public static <E extends AbstractPersistableEntity> E getAndCheckEntityVersion(Long id, Long version,
			CrudRepository<E, Long> repository, Class<E> entityClass) throws SnsException {

		if (id == null) {
			try {
				return entityClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new SnsRuntimeException(e);
			}
		}
			
    	E entity = repository.findOne(id);
    	String entityName = entityClass.getSimpleName();
    	if (entity == null) {
    		throw new SnsException(String.format("%s with id: %d no longer exists.", entityName, id));
    	}
    	if (!/* not */entity.getVersion().equals(version)) {
    		throw new SnsException(String.format("%s version has changed.", entityName));
    	}
    	return entity;
    }

}
