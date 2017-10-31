/**
 * 
 */
package com.kerneldc.education.studentNotesService.domain.resultTransformer;

import java.util.List;

import org.hibernate.transform.ResultTransformer;

import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;

public class StudentBasicResultTtransformer implements ResultTransformer {

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		StudentUiDto studentUiDto = new StudentUiDto();
		studentUiDto.setId(longFromObject(tuple[0]));
		studentUiDto.setFirstName((String)tuple[1]);
		studentUiDto.setLastName((String)tuple[2]);
		studentUiDto.setVersion(longFromObject(tuple[3]));
		return studentUiDto;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List transformList(List arg0) {
		return arg0;
	}

	private Long longFromObject(Object object) {
		return object == null ? null : (Long)object;
	}

}
