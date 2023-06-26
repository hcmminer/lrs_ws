/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.config;

import java.io.IOException;
import java.io.Serializable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

  @Override
  public void commence(HttpServletRequest hsr, HttpServletResponse hsr1, org.springframework.security.core.AuthenticationException ae) throws IOException, ServletException {
    hsr1.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }
}
