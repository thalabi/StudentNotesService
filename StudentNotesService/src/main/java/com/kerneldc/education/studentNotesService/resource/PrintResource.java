package com.kerneldc.education.studentNotesService.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.resource.vo.PrintRequestVo;
import com.kerneldc.education.studentNotesService.service.PdfStudentNotesReportService;

@Component
@Path("/StudentNotesService/PrintResource")
public class PrintResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private PdfStudentNotesReportService pdfStudentNotesReportService;

	// TODO not covered by a test case
	@POST
	@Path("/pdfStudentsByStudentIds")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response pdfStudentsByStudentIds(PrintRequestVo printRequestVo) throws SnsException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getStudentsBySchoolYearIdAndListOfIds(printRequestVo.getSchoolYearId(), printRequestVo.getStudentIds()));
		byte[] pdfByteArray = null;
		if (students.getStudentList().size() != 0) {
			pdfByteArray = pdfStudentNotesReportService.generateReport(students);
		}
		// TODO print an empty pdf when there are no students returned
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}

}
