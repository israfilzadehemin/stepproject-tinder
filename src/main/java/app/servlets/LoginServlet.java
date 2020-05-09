package app.servlets;

import app.dao.UserDao;
import app.entities.User;
import app.tools.ConnectionTool;
import app.tools.CookieFilter;
import app.tools.TemplateEngine;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginServlet extends HttpServlet {
  private final TemplateEngine engine;

  public LoginServlet(TemplateEngine engine) {
    this.engine = engine;
  }

  ConnectionTool connTool = new ConnectionTool();
  UserDao userDao = new UserDao();


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HashMap<String, Object> data = new HashMap<>();
    CookieFilter cookieFilter = new CookieFilter();

    if (!cookieFilter.isLogged(req, resp))
    engine.render("login.ftl", data, resp);
    else resp.sendRedirect("/users");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      userDao.getAllUsers().addAll(connTool.getUsers());
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }

    String mail = req.getParameter("mail");
    String password = req.getParameter("password");

    if (userDao.checkUser(mail, password)) {
      Cookie loginCookie = new Cookie("login", String.format("%s", mail));
      loginCookie.setMaxAge(60 * 60 * 24);
      resp.addCookie(loginCookie);
      resp.sendRedirect("/users");
    }

  }
}