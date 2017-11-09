package com.kerneldc.education.studentNotesService.resource;

import java.util.List;

import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.resource.vo.AbstractBaseVo;

public class SaveRemoveStudentsToFromSchoolYearVO2 extends AbstractBaseVo {

	private static final long serialVersionUID = 1L;

	private Long schoolYearId;
	private List<Long> oldSchoolYearStudentIds;
	private List<Long> newSchoolYearStudentIds;
	
	public Long getSchoolYearId() {
		return schoolYearId;
	}
	public void setSchoolYearId(Long schoolYearId) {
		this.schoolYearId = schoolYearId;
	}
	public List<Long> getOldSchoolYearStudentIds() {
		return oldSchoolYearStudentIds;
	}
	public void setOldSchoolYearStudentIds(List<Long> oldSchoolYearStudentIds) {
		this.oldSchoolYearStudentIds = oldSchoolYearStudentIds;
	}
	public List<Long> getNewSchoolYearStudentIds() {
		return newSchoolYearStudentIds;
	}
	public void setNewSchoolYearStudentIds(List<Long> newSchoolYearStudentIds) {
		this.newSchoolYearStudentIds = newSchoolYearStudentIds;
	}
}
