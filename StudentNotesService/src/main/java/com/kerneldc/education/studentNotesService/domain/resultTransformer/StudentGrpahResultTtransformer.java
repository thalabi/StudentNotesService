/**
 * 
 */
package com.kerneldc.education.studentNotesService.domain.resultTransformer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.transform.ResultTransformer;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.dto.GradeDto;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;

public class StudentGrpahResultTtransformer implements ResultTransformer {

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		StudentDto studentDto = new StudentDto();
		studentDto.setId(longFromObject(tuple[0]));
		studentDto.setFirstName((String)tuple[1]);
		studentDto.setLastName((String)tuple[2]);
		GradeDto gradeDto = new GradeDto();
		gradeDto.setId(longFromObject(tuple[3]));
		gradeDto.setGradeEnum(GradeEnum.fromValue((String)tuple[4]));
		gradeDto.setVersion(longFromObject(tuple[5]));
		studentDto.setGradeDto(gradeDto);
		NoteDto noteDto = new NoteDto();
		noteDto.setId(longFromObject(tuple[6]));
		noteDto.setTimestamp((Timestamp)tuple[7]);
		noteDto.setText((String)tuple[8]);
		noteDto.setVersion(longFromObject(tuple[9]));
		if (noteDto.getId() != null && noteDto.getTimestamp() != null && noteDto.getText() != null
				&& noteDto.getVersion() != null) {
			studentDto.addNoteDto(noteDto);			
		}
		SchoolYearDto schoolYearDto = new SchoolYearDto();
		schoolYearDto.setId(longFromObject(tuple[10]));
		schoolYearDto.setSchoolYear((String)tuple[11]);
		schoolYearDto.setStartDate((Date)tuple[12]);
		schoolYearDto.setEndDate((Date)tuple[13]);
		schoolYearDto.setVersion(longFromObject(tuple[14]));
		studentDto.setSchoolYearDto(schoolYearDto);
		studentDto.setVersion(longFromObject(tuple[15]));
		return studentDto;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List transformList(List collection) {
		Map<Long, StudentDto> studentDtoMap = new LinkedHashMap<>();
		for (Object object : collection) {
			StudentDto studentDto = (StudentDto)object;
			if (!/* not */studentDtoMap.containsKey(studentDto.getId())) {
				studentDtoMap.put(studentDto.getId(), studentDto);
			} else {
				if (CollectionUtils.isNotEmpty(studentDto.getNoteDtoSet())) {
					studentDtoMap.get(studentDto.getId()).addNoteDto(studentDto.getNoteDtoSet().iterator().next());
				}
			}
		}
		return new ArrayList<>(studentDtoMap.values());
	}

	private Long longFromObject(Object object) {
		return object == null ? null : (Long)object;
	}
}
