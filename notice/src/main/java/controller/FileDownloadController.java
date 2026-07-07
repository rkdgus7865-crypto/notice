package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;

@WebServlet("/fileDownload")
public class FileDownloadController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String fileName = request.getParameter("fileName");
		String originalName = request.getParameter("originalName");

		String uploadPath = getServletContext().getRealPath("/uploads");
		File file = new File(uploadPath, fileName);

		if (!file.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + originalName + "\"");
		response.setContentLength((int) file.length());

		try (FileInputStream fis = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		}
	}
}