package com;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Searcher extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyWord = request.getParameter("searchWord").trim();
		Integer pubNum = Integer.parseInt(request.getParameter("dot"));
		Integer filedNum = Integer.parseInt(request.getParameter("dotF"));
		Integer page = (request.getParameter("page") != null) ? Integer.parseInt(request.getParameter("page")) : 1;
		
		ArrayList<Info> lists = SearchProcess.results(keyWord, pubNum, filedNum, page);
		
		request.setAttribute("allinfo", lists);
		request.setAttribute("ikv", keyWord);
		request.setAttribute("idv", pubNum);
		request.setAttribute("fdv", filedNum);
		request.setAttribute("pn", page);
		
		request.getRequestDispatcher("search.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}