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
import com.kerneldc.education.studentNotesService.dto.ui.GradeUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.SchoolYearUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;

public class StudentGrpahResultTtransformer implements ResultTransformer {

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		StudentUiDto studentUiDto = new StudentUiDto();
		studentUiDto.setId(longFromObject(tuple[0]));
		studentUiDto.setFirstName((String)tuple[1]);
		studentUiDto.setLastName((String)tuple[2]);
		GradeUiDto gradeUiDto = new GradeUiDto();
		gradeUiDto.setId(longFromObject(tuple[3]));
		gradeUiDto.setGradeEnum(GradeEnum.fromValue((String)tuple[4]));
		gradeUiDto.setVersion(longFromObject(tuple[5]));
		studentUiDto.setGradeUiDto(gradeUiDto);
		NoteUiDto noteUiDto = new NoteUiDto();
		noteUiDto.setId(longFromObject(tuple[6]));
		noteUiDto.setTimestamp((Timestamp)tuple[7]);
		noteUiDto.setText((String)tuple[8]);
		noteUiDto.setVersion(longFromObject(tuple[9]));
		if (noteUiDto.getId() != null && noteUiDto.getTimestamp() != null && noteUiDto.getText() != null
				&& noteUiDto.getVersion() != null) {
			studentUiDto.addNoteUiDto(noteUiDto);			
		}
		SchoolYearUiDto schoolYearUiDto = new SchoolYearUiDto();
		schoolYearUiDto.setId(longFromObject(tuple[10]));
		schoolYearUiDto.setSchoolYear((String)tuple[11]);
		schoolYearUiDto.setStartDate((Date)tuple[12]);
		schoolYearUiDto.setEndDate((Date)tuple[13]);
		schoolYearUiDto.setVersion(longFromObject(tuple[14]));
		studentUiDto.setSchoolYearUiDto(schoolYearUiDto);
		studentUiDto.setVersion(longFromObject(tuple[15]));
		return studentUiDto;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List transformList(List collection) {
		Map<Long, StudentUiDto> studentUiDtoMap = new LinkedHashMap<>();
		for (Object object : collection) {
			StudentUiDto studentUiDto = (StudentUiDto)object;
			if (!/* not */studentUiDtoMap.containsKey(studentUiDto.getId())) {
				studentUiDtoMap.put(studentUiDto.getId(), studentUiDto);
			} else {
				if (CollectionUtils.isNotEmpty(studentUiDto.getNoteUiDtoSet())) {
					studentUiDtoMap.get(studentUiDto.getId()).addNoteUiDto(studentUiDto.getNoteUiDtoSet().iterator().next());
				}
			}
		}
		return new ArrayList<>(studentUiDtoMap.values());
	}

	private Long longFromObject(Object object) {
		return object == null ? null : (Long)object;
	}
}
