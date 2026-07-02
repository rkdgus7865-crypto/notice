package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import dao.BbsDAO;
import dto.Bbs;

@WebServlet("/viewDetail")
public class ViewController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("group");
        if (groupName == null) groupName = "자유게시판";

        BbsDAO bbsDAO = new BbsDAO();
        Bbs bbs = bbsDAO.getDetail(bbsID);

        request.setAttribute("bbs", bbs);
        request.setAttribute("groupName", groupName);
        request.getRequestDispatcher("view.jsp").forward(request, response);
    }
}