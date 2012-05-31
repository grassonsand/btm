package org.savara.btm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.savara.bpmn2.model.TDefinitions;
import org.savara.bpmn2.model.util.BPMN2ModelJSONUtil;
import org.savara.bpmn2.model.util.BPMN2ModelUtil;

@Path("/test")
public class JSONTranslator {
	
	@POST
	@Path("/upload")
	public void translateBpmnXMLtoJSON(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		ServletFileUpload uploadHandler = new ServletFileUpload();
		try {
			PrintWriter out = response.getWriter();
			
			FileItemIterator iter = uploadHandler.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream fis = iter.next();
				InputStream is = fis.openStream();
				if (! fis.isFormField()) {
					TDefinitions defns = BPMN2ModelUtil.deserialize(is);
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					BPMN2ModelJSONUtil.serialize(defns, baos);					
					baos.close();
					
					String json = new String(baos.toByteArray());
					
					out.write(json);
					out.flush();
					out.close();
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}