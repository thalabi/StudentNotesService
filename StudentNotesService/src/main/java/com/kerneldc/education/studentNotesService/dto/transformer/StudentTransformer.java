package com.kerneldc.education.studentNotesService.dto.transformer;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Persistence;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.GradeDto;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;

public class StudentTransformer {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	private StudentTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	  }

	// TODO convert schoolYearSet
	public static StudentDto entityToDto(Student student) {
		StudentDto studentDto = new StudentDto();

		if (Persistence.getPersistenceUtil().isLoaded(student.getNoteSet())) {
			System.out.println("Persistence.getPersistenceUtil().isLoaded(student.getNoteSet())");
		}
		if (Persistence.getPersistenceUtil().isLoaded(student.getGradeSet())) {
			System.out.println("Persistence.getPersistenceUtil().isLoaded(student.getGradeSet())");
		}
		
		if (Hibernate.isInitialized(student.getNoteSet())) {
			System.out.println("Hibernate.isInitialized(student.getNoteSet())");
		}
		if (Hibernate.isInitialized(student.getGradeSet())) {
			System.out.println("Hibernate.isInitialized(student.getGradeSet())");
		}
		
		BeanUtils.copyProperties(student, studentDto, "noteSet", "gradeSet");
		if (Persistence.getPersistenceUtil().isLoaded(student.getNoteSet())) {
			Set<NoteDto> noteDtoSet = new LinkedHashSet<>();
			for (Note note : student.getNoteSet()) {
				NoteDto noteDto = new NoteDto();
				BeanUtils.copyProperties(note, noteDto);
				noteDtoSet.add(noteDto);
			}
			studentDto.setNoteDtoSet(noteDtoSet);
		}
		if (Persistence.getPersistenceUtil().isLoaded(student.getGradeSet())) {
			Set<GradeDto> grade2DtoSet = new LinkedHashSet<>();
			for (Grade grade : student.getGradeSet()) {
				GradeDto gradeDto = new GradeDto();
				BeanUtils.copyProperties(grade, gradeDto);
				grade2DtoSet.add(gradeDto);
				System.out.println(gradeDto);
			}
			studentDto.setGradeDtoSet(grade2DtoSet);
		}
		return studentDto;
	}

	public static StudentUiDto entityToUiDto(Student student) {
		StudentUiDto studentUiDto = new StudentUiDto();

		// TODO these methods are not working properly they all return true irrespective
		if (Persistence.getPersistenceUtil().isLoaded(student.getNoteSet())) {
			System.out.println("Persistence.getPersistenceUtil().isLoaded(student, \"noteSet\")");
		}
		if (Persistence.getPersistenceUtil().isLoaded(student.getGradeSet())) {
			System.out.println("Persistence.getPersistenceUtil().isLoaded(student, \"gradeSet\")");
		}
		if (Persistence.getPersistenceUtil().isLoaded(student.getSchoolYearSet())) {
			System.out.println("Persistence.getPersistenceUtil().isLoaded(student, \"schoolYearSet\")");
		}
		
		if (Hibernate.isInitialized(student.getNoteSet())) {
			System.out.println("Hibernate.isInitialized(student.getNoteSet())");
		}
		if (Hibernate.isInitialized(student.getGradeSet())) {
			System.out.println("Hibernate.isInitialized(student.getGradeSet())");
		}
		
		if (Hibernate.isInitialized(student.getSchoolYearSet())) {
			System.out.println("Hibernate.isInitialized(student.getSchoolYearSet())");
		}
		
		BeanUtils.copyProperties(student, studentUiDto, "noteSet", "gradeSet");
		if (Persistence.getPersistenceUtil().isLoaded(student.getNoteSet())) {
			for (Note note : student.getNoteSet()) {
				NoteUiDto noteUiDto = new NoteUiDto();
				BeanUtils.copyProperties(note, noteUiDto);
				studentUiDto.addNoteUiDto(noteUiDto);
			}
		}
//		if (Persistence.getPersistenceUtil().isLoaded(student, "gradeSet")) {
//			Set<GradeDto> grade2DtoSet = new LinkedHashSet<>();
//			for (Grade grade : student.getGradeSet()) {
//				GradeDto gradeDto = new GradeDto();
//				BeanUtils.copyProperties(grade, gradeDto);
//				grade2DtoSet.add(gradeDto);
//				System.out.println(gradeDto);
//			}
//			studentUiDto.setGradeDtoSet(grade2DtoSet);
//		}
		return studentUiDto;
	}

	// TODO convert schoolYearSet
	public static Student dtoToEntity(StudentDto studentDto) {
		Student student = new Student();
		BeanUtils.copyProperties(studentDto, student);
		if (CollectionUtils.isNotEmpty(studentDto.getNoteDtoSet())) {
			Set<Note> noteSet = new LinkedHashSet<>();
			for (NoteDto noteDto : studentDto.getNoteDtoSet()) {
				noteSet.add(NoteTransformer.dtoToEntity(noteDto));
			}
			student.setNoteSet(noteSet);
		}
		if (CollectionUtils.isNotEmpty(studentDto.getSchoolYearDtoSet())) {
			Set<SchoolYear> schoolYearSet = new LinkedHashSet<>();
			for (SchoolYearDto schoolYearDto : studentDto.getSchoolYearDtoSet()) {
				schoolYearSet.add(SchoolYearTransformer.dtoToEntity(schoolYearDto));
			}
			student.setSchoolYearSet(schoolYearSet);
		}
		if (CollectionUtils.isNotEmpty(studentDto.getGradeDtoSet())) {
			Set<Grade> gradeSet = new LinkedHashSet<>();
			for (GradeDto gradeDto : studentDto.getGradeDtoSet()) {
				gradeSet.add(GradeTransformer.dtoToEntity(gradeDto));
			}
			student.setGradeSet(gradeSet);
		}
		return student;
	}

	public static Student uiDtoToEntity(StudentUiDto studentUiDto) {
		Student student = new Student();
		BeanUtils.copyProperties(studentUiDto, student);
		if (studentUiDto.getSchoolYearUiDto() != null && studentUiDto.getGradeUiDto() != null) {
			SchoolYear schoolYear = new SchoolYear();
			BeanUtils.copyProperties(studentUiDto.getSchoolYearUiDto(), schoolYear);
			Grade grade = new Grade();
			BeanUtils.copyProperties(studentUiDto.getGradeUiDto(), grade);
			grade.setStudent(student);
			grade.setSchoolYear(schoolYear);
			student.getGradeSet().add(grade);
			student.addSchoolYear(schoolYear);
		}
		if (CollectionUtils.isNotEmpty(studentUiDto.getNoteUiDtoSet())) {
			Set<Note> noteSet = new LinkedHashSet<>();
			for (NoteUiDto noteUiDto : studentUiDto.getNoteUiDtoSet()) {
				noteSet.add(NoteTransformer.uiDtoToEntity(noteUiDto));
			}
			student.setNoteSet(noteSet);
		}
		return student;
	}
}
