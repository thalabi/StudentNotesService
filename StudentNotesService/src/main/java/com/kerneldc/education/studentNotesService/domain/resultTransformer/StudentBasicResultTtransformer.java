/**
 * 
 */
package com.kerneldc.education.studentNotesService.domain.resultTransformer;

import java.util.List;

import org.hibernate.transform.ResultTransformer;

import com.kerneldc.education.studentNotesService.dto.StudentDto;

public class StudentBasicResultTtransformer implements ResultTransformer {

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		StudentDto studentDto = new StudentDto();
		studentDto.setId(longFromObject(tuple[0]));
		studentDto.setFirstName((String)tuple[1]);
		studentDto.setLastName((String)tuple[2]);
		studentDto.setVersion(longFromObject(tuple[3]));
		return studentDto;
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
