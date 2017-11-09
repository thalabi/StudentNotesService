package com.kerneldc.education.studentNotesService.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.exception.SnsException;

@Service
public class PdfStudentNotesReportService implements StudentNotesReportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	private static final String XSL_FILE = "studentsToFo.xsl";
	private static final SimpleDateFormat generatedFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

	public byte[] generateReport (
		Students students) throws SnsException {
		
		byte[] xmlBytes;
		try {
			xmlBytes = beanToXml(students);
		} catch (JAXBException e) {
			throw new SnsException(e);
		} 
		return xmlToPdf(xmlBytes);
	}
	
	public byte[] beanToXml (
		Students students) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(students.getClass()); 
        Marshaller marshaller = jaxbContext.createMarshaller(); 
  
        // output pretty printed 
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
  
        ByteArrayOutputStream xmlByteArrayOutputStream = new ByteArrayOutputStream(); 
 
        marshaller.marshal(students, xmlByteArrayOutputStream); 
         
        return xmlByteArrayOutputStream.toByteArray(); 
	}
	
	public byte[] xmlToPdf(
		byte[] studentsXmlByteArray) {

			// Configure fopFactory as desired
			final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

			// Configure foUserAgent as desired
			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

			try {
				// Setup output
				ByteArrayOutputStream pdfByteArrayOutputStream = new ByteArrayOutputStream();

				// Construct fop with desired output format
				Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdfByteArrayOutputStream);

				// Setup XSLT
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream((XSL_FILE))));

				// Set the value of a <param> in the stylesheet
				transformer.setParameter("timeGenerated", generatedFormat.format(new Date()));

				// Setup input for XSLT transformation
				Source src = new StreamSource(new ByteArrayInputStream(studentsXmlByteArray));

				// Resulting SAX events (the generated FO) must be piped through
				// to FOP
				Result res = new SAXResult(fop.getDefaultHandler());

				// Start XSLT transformation and FOP processing
				transformer.transform(src, res);
				//out.close();
				return pdfByteArrayOutputStream.toByteArray();

			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

			return null;
	}
}
