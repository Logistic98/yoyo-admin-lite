package com.yoyo.admin.web_manage.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "forward:index.html";
    }

    @RequestMapping(value = ERROR_PATH)
    public String notFoundError(HttpServletRequest request, HttpServletResponse response) {
        return "forward:index.html";
    }

}
