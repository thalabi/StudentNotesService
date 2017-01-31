package com.kerneldc.education.studentNotes.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.kerneldc.education.studentNotes.bean.Students;

@Service
public class StudentNotesReportService {

	private final static String XSL_FILE = "studentsToFo.xsl";

	public byte[] generateReport (
		Students students) throws JAXBException, ParserConfigurationException, SAXException, IOException, TransformerException {
		
		byte[] navLogReportBeanXmlByteArray = beanToXml(students); 
		//byte[] navLogReportBeanXmlFoByteArray = xmlToXslFo(navLogReportBeanXmlByteArray, XSL_FILE); 
		//return xmlToXslFo(navLogReportBeanXmlByteArray, XSL_FILE);
		return navLogReportBeanXmlByteArray;
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
	
	public void xmlToPdf(
		byte[] studentsXmlByteArray) {
		
			File xsltfile = new File(ClassLoader.getSystemResource(XSL_FILE).getFile());
			File pdffile = new File("c://temp/pdf.pdf");

			// Configure fopFactory as desired
			final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

			// Configure foUserAgent as desired
			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

			try {
				// Setup output
				OutputStream out = new FileOutputStream(pdffile);
				out = new BufferedOutputStream(out);

				// Construct fop with desired output format
				Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

				// Setup XSLT
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

				// Set the value of a <param> in the stylesheet
				transformer.setParameter("timeGenerated", new Date());

				// Setup input for XSLT transformation
				Source src = new StreamSource(new ByteArrayInputStream(studentsXmlByteArray));

				// Resulting SAX events (the generated FO) must be piped through
				// to FOP
				Result res = new SAXResult(fop.getDefaultHandler());

				// Start XSLT transformation and FOP processing
				transformer.transform(src, res);
				out.close();

			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

			System.out.println("Success!");
	}
}
