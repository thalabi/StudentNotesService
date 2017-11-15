package com.kerneldc.education.studentNotesService.resource;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.dto.transformer.UserPreferenceTransformer;
import com.kerneldc.education.studentNotesService.dto.ui.UserPreferenceUiDto;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.UserPreferenceRepository;

@Component
@Path("/StudentNotesService/userPreference")
public class UserPreferenceResource implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private UserPreferenceRepository userPreferenceRepository;

	@Autowired
	private SchoolYearRepository schoolYearRepository;

	@Autowired
	private JpaContext jpaContext;
	
	private EntityManager userPreferenceEntityManager;

	@Override
	public void afterPropertiesSet() {
		userPreferenceEntityManager = jpaContext.getEntityManagerByManagedType(UserPreference.class);
	}

	public UserPreferenceResource() {
		LOGGER.info("Initialized ...");
	}
	
	@GET
	public String hello() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return "Hello";
	}

	@GET
	@Path("/getUiDtoByUsername/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPreferenceUiDto getUiDtoByUsername(
		@PathParam("username") String username) throws SnsException {
		
		LOGGER.debug("begin ...");
		UserPreferenceUiDto userPreferenceUiDto = new UserPreferenceUiDto();
		try {
			UserPreference userPreference = userPreferenceRepository.findByUsername(username).get(0);
			userPreferenceUiDto = UserPreferenceTransformer.entityToUiDto(userPreference);
			LOGGER.debug("userPreferenceUiDto: {}", userPreferenceUiDto);
		} catch (RuntimeException e) {
			throw new SnsException(ExceptionUtils.getRootCauseMessage(e));
		}
		LOGGER.debug("end ...");
		return userPreferenceUiDto;
	}

    @POST
	@Path("/saveUserPreferenceUiDto")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public UserPreferenceUiDto saveUserPreferenceUiDto(
    	UserPreferenceUiDto userPreferenceUiDto) throws SnsException {

    	LOGGER.debug("begin ...");
    	//UserPreference userPreference = UserPreferenceTransformer.uiDtoToEntity(userPreferenceUiDto);
    	UserPreferenceUiDto savedUserPreferenceUiDto;
    	try {
    		UserPreference userPreference = userPreferenceRepository.findOne(userPreferenceUiDto.getId());
    		checkVersion(userPreferenceUiDto.getVersion(), userPreference.getVersion());
    		userPreference.setUsername(userPreferenceUiDto.getUsername());
    		SchoolYear schoolYear = schoolYearRepository.findOne(userPreferenceUiDto.getSchoolYearUiDto().getId());
    		userPreference.setSchoolYear(schoolYear);
    		userPreference = userPreferenceRepository.save(userPreference);
    		// Flush is needed so that userPreference.version is incremented
    		userPreferenceEntityManager.flush();
    		savedUserPreferenceUiDto = UserPreferenceTransformer.entityToUiDto(userPreference);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsException(ExceptionUtils.getRootCauseMessage(e));
		}
    	LOGGER.debug("end ...");
    	return savedUserPreferenceUiDto;
    }

    private void checkVersion(Long clientVersion, Long entityVersion) throws SnsException {
    	if (clientVersion.compareTo(entityVersion) != 0) {
    		throw new SnsException("Entity has been updated by another resource."); 
    	}
    }

}
