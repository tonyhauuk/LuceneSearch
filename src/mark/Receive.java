package mark;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Receive extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NumberFormatException {
		
		Integer roleID = (request.getParameter("roleID") != "") ? Integer.parseInt(request.getParameter("roleID")) : 0;
		Integer fid = (request.getParameter("fid") != "") ? Integer.parseInt(request.getParameter("fid")) : 1;
		Integer amount = (request.getParameter("amount") != "") ? Integer.parseInt(request.getParameter("amount")) : 100;
		
		PrintWriter out = response.getWriter();
		
		if (roleID != null && roleID > 0) {
			Tagging.tagRole(roleID, fid, amount);
			
			out.println("OK");
		}
		else if (roleID == null || roleID == 0) {
			out.println("Extract failure!");
			System.exit(0);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
