/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.filter;

import com.viettel.base.cms.service.JwtUserDetailsService;
import com.viettel.base.cms.utils.JwtTokenUtil;
import com.viettel.security.PassTranformer;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  @Autowired
  private Environment env;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    String userName = request.getHeader("User-Name");
    String password = request.getHeader("Password");
    String jwtToken = null;
    // JWT Token is in the form "Bearer token". Remove Bearer word and get
    // only the Token
    UserDetails userDetails = null;
    if (userName != null && password != null && userName.length() > 0 && password.length() > 0) {
      try {
        PassTranformer.setInputKey(env.getProperty("key.token.secret"));
        password = PassTranformer.decrypt(password);
      } catch (Exception e) {
        logger.error(e);
        password = null;
      }
      if (password != null) {
        password = password.replace(env.getProperty("key.public.salt"), "");
        password = PassTranformer.encrypt(password);
        try {
          userDetails = this.jwtUserDetailsService.loadUserByUsernameAndPassword(userName, password);
        } catch (Exception e) {
          logger.error(e);
        }
        if (userDetails != null) {
          jwtToken = jwtTokenUtil.generateToken(userDetails);
        }
      }
    } else {
      logger.error("userName or password app invalid");
    }
    // Once we get the token validate it.
    if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // if token is valid configure Spring Security to manually set
      // authentication
      if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    chain.doFilter(request, response);
  }
}
