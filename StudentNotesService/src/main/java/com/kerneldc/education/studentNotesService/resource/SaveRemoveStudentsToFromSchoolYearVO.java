package com.kerneldc.education.studentNotesService.resource;

import java.util.List;

import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.resource.vo.AbstractBaseVo;

public class SaveRemoveStudentsToFromSchoolYearVO extends AbstractBaseVo {

	private static final long serialVersionUID = 1L;

	private Long schoolYearId;
	private List<Student> oldSchoolYearStudents;
	private List<Student> newSchoolYearStudents;
	
	public Long getSchoolYearId() {
		return schoolYearId;
	}
	public void setSchoolYearId(Long schoolYearId) {
		this.schoolYearId = schoolYearId;
	}
	public List<Student> getOldSchoolYearStudents() {
		return oldSchoolYearStudents;
	}
	public void setOldSchoolYearStudents(List<Student> oldSchoolYearStudents) {
		this.oldSchoolYearStudents = oldSchoolYearStudents;
	}
	public List<Student> getNewSchoolYearStudents() {
		return newSchoolYearStudents;
	}
	public void setNewSchoolYearStudents(List<Student> newSchoolYearStudents) {
		this.newSchoolYearStudents = newSchoolYearStudents;
	}
}
