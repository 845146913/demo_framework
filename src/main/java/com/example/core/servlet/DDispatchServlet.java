package com.example.core.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * DispatchServlet
 */
@WebServlet
public class DDispatchServlet extends HttpServlet {

    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // 1.加载配置文件
        // 2.根据配置文件，初始化相关的类
        // 3.初始化ioc容器，通过反射
        // 4.自动化注入依赖
        // 5.初始化HandlerMapping(url与RequestMapping对应)
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}
