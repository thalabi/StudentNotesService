/**
 * 
 */
package com.kerneldc.education.studentNotesService.domain.resultTransformer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.transform.ResultTransformer;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.dto.ui.GradeUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.SchoolYearUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;
import com.kerneldc.education.studentNotesService.util.KdcCollectionUtils;

public class StudentUiDtoResultTtransformer implements ResultTransformer {

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		StudentUiDto studentUiDto = new StudentUiDto();
		// skip element 0 which is the username
		studentUiDto.setId(longFromObject(tuple[1]));
		studentUiDto.setFirstName((String)tuple[2]);
		studentUiDto.setLastName((String)tuple[3]);
		GradeUiDto gradeUiDto = new GradeUiDto();
		gradeUiDto.setId(longFromObject(tuple[4]));
		gradeUiDto.setGradeEnum(GradeEnum.fromValue((String)tuple[5]));
		gradeUiDto.setVersion(longFromObject(tuple[6]));
		studentUiDto.setGradeUiDto(gradeUiDto);
		NoteUiDto noteUiDto = new NoteUiDto();
		noteUiDto.setId(longFromObject(tuple[7]));
		noteUiDto.setTimestamp((Timestamp)tuple[8]);
		noteUiDto.setText((String)tuple[9]);
		noteUiDto.setVersion(longFromObject(tuple[10]));
		if (noteUiDto.getId() != null && noteUiDto.getTimestamp() != null && noteUiDto.getText() != null
				&& noteUiDto.getVersion() != null) {
			studentUiDto.addNoteUiDto(noteUiDto);			
		}
		SchoolYearUiDto schoolYearUiDto = new SchoolYearUiDto();
		schoolYearUiDto.setId(longFromObject(tuple[11]));
		schoolYearUiDto.setSchoolYear((String)tuple[12]);
		schoolYearUiDto.setStartDate((Date)tuple[13]);
		schoolYearUiDto.setEndDate((Date)tuple[14]);
		schoolYearUiDto.setVersion(longFromObject(tuple[15]));
		studentUiDto.setSchoolYearUiDto(schoolYearUiDto);
		studentUiDto.setVersion(longFromObject(tuple[16]));
		return studentUiDto;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List transformList(List collection) {
		@SuppressWarnings("unchecked")
		Map<Long, StudentUiDto> studentUiDtoMap = KdcCollectionUtils.toMapGeneric(collection, "id", Long.class);
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
