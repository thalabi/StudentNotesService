package com.kerneldc.education.studentNotesService.service;

import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.exception.SnsException;

public interface StudentNotesReportService {

	byte[] generateReport (Students students) throws SnsException ;
}
