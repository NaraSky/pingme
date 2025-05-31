package com.lb.pingme.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private HttpServletRequest request;

    public boolean set(String key, String val) {
        HttpSession session = request.getSession();
        session.setAttribute(key, val);
        return true;
    }

    public String get(String key) {
        Object valObj = request.getSession().getAttribute(key);
        return valObj == null ? null : valObj.toString();
    }

    public void remove(String key) {
        request.getSession().removeAttribute(key);
    }
}
