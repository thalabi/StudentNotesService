package com.kerneldc.education.studentNotesService.resource;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
		if (!/* not */students.getStudentList().isEmpty()) {
			pdfByteArray = pdfStudentNotesReportService.generateReport(students);
		}
		// TODO print an empty pdf when there are no students returned
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}

	@POST
	@Path("/pdfStudentsByTimestampRange")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response pdfStudentsByTimestampRange(PrintRequestVo printRequestVo) throws SnsException {

		LOGGER.debug("begin ...");
		LocalDateTime toMidnight = printRequestVo.getToTimestamp().toLocalDateTime().toLocalDate()
				.atTime(LocalTime.MAX); // midnight
		printRequestVo.setToTimestamp(Timestamp.valueOf(toMidnight));
		Students students = new Students();
		students.setStudentList(studentRepository.getStudentsByTimestampRange(printRequestVo.getSchoolYearId(), printRequestVo.getFromTimestamp(), printRequestVo.getToTimestamp()));
		byte[] pdfByteArray = null;
		//if (!/* note */students.getStudentList().isEmpty()) {
			pdfByteArray = pdfStudentNotesReportService.generateReport(students);
		//}
		// TODO print an empty pdf when there are no students returned
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}

	@POST
	@Path("/pdfAll")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response pdfAll(PrintRequestVo printRequestVo) throws SnsException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getStudentGraphBySchoolYear(printRequestVo.getSchoolYearId()));
		byte[] pdfByteArray = pdfStudentNotesReportService.generateReport(students);
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}

}
