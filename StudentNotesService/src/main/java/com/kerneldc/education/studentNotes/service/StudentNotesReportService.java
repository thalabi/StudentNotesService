package com.kerneldc.education.studentNotes.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

import com.kerneldc.education.studentNotes.domain.Student;

@Service
public class StudentNotesReportService {

	private final static String XSL_FILE = "studentNotes.xsl";

	public byte[] generateReport (
		Student student) throws JAXBException, ParserConfigurationException, SAXException, IOException, TransformerException {
		
		byte[] navLogReportBeanXmlByteArray = beanToXml(student); 
		//byte[] navLogReportBeanXmlFoByteArray = xmlToXslFo(navLogReportBeanXmlByteArray, XSL_FILE); 
		//return xmlToXslFo(navLogReportBeanXmlByteArray, XSL_FILE);
		return navLogReportBeanXmlByteArray;
	}
	
	public byte[] beanToXml (
		Student student) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(student.getClass()); 
        Marshaller marshaller = jaxbContext.createMarshaller(); 
  
        // output pretty printed 
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
  
        ByteArrayOutputStream xmlByteArrayOutputStream = new ByteArrayOutputStream(); 
 
        marshaller.marshal(student, xmlByteArrayOutputStream); 
         
        return xmlByteArrayOutputStream.toByteArray(); 
	}
	
	public void xmlToPdf() {
		try {
			System.out.println("FOP ExampleXML2PDF\n");
			System.out.println("Preparing...");

			// Setup input and output files

			File xmlfile = new File(ClassLoader.getSystemResource("projectteam.xml").getFile());
			//File xmlfile = new File(ClassLoader.getSystemResource("studentBean.xml").getFile());
			File xsltfile = new File(ClassLoader.getSystemResource("projectteam2fo.xsl").getFile());
			File pdffile = new File("c://temp/pdf.pdf");

			System.out.println("Input: XML (" + xmlfile + ")");
			System.out.println("Stylesheet: " + xsltfile);
			System.out.println("Output: PDF (" + pdffile + ")");
			System.out.println();
			System.out.println("Transforming...");
			// configure fopFactory as desired
			final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
			// configure foUserAgent as desired

			// Setup output
			OutputStream out = new FileOutputStream(pdffile);
			out = new BufferedOutputStream(out);

			try {
				// Construct fop with desired output format
				Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

				// Setup XSLT
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

				// Set the value of a <param> in the stylesheet
				transformer.setParameter("versionParam", "2.0");

				// Setup input for XSLT transformation
				Source src = new StreamSource(xmlfile);

				// Resulting SAX events (the generated FO) must be piped through
				// to FOP
				Result res = new SAXResult(fop.getDefaultHandler());

				// Start XSLT transformation and FOP processing
				transformer.transform(src, res);
			} finally {
				out.close();
			}

			System.out.println("Success!");
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}
}
