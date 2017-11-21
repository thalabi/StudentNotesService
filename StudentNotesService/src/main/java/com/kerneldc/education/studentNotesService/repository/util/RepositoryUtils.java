package com.kerneldc.education.studentNotesService.repository.util;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.AbstractPersistableEntity;
import com.kerneldc.education.studentNotesService.exception.SnsException;

public class RepositoryUtils {

	private RepositoryUtils() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	}

	public static <E extends AbstractPersistableEntity> E getAndCheckEntityVersion(Long id, Long version,
			CrudRepository<E, Long> repository) throws SnsException {
    	E entity = repository.findOne(id);
    	// TODO need to find the class name using the repository argument instead of entity as entity might be null
    	//String entityName = entity.getClass().getSimpleName();
    	if (entity == null) {
    		String entityName = "Entity"; // temporary variable until the above is implemented
    		throw new SnsException(String.format("%s with id: %d no longer exists.", entityName, id));
    	}
    	if (!/* not */entity.getVersion().equals(version)) {
    		throw new SnsException(String.format("%s version has changed.", entity.getClass().getSimpleName()));
    	}
    	return entity;
    }

}
