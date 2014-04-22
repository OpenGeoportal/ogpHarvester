/* AjaxAwareAuthenticationEntryPoint.java
 * 
 * Copyright (C) 2014
 * 
 * This file is part of project OGP Harvester
 * 
 * This software is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As a special exception, if you link this library with other files to
 * produce an executable, this library does not by itself cause the
 * resulting executable to be covered by the GNU General Public License.
 * This exception does not however invalidate any other reasons why the
 * executable file might be covered by the GNU General Public License.
 * 
 * Authors:: Juan Luis Rodríguez Ponce (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc.interceptor;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 *
 * @author JuanLuis
 */
public class AjaxAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Logger LOG = LoggerFactory.getLogger(AjaxAwareAuthenticationEntryPoint.class);

    /**
     *
     * @param loginFormUrl URL where the login page can be found. Should either
     * be relative to the web-app context path (include a leading {@code /}) or
     * an absolute URL.
     * @see
     * LoginUrlAuthenticationEntryPoint#LoginUrlAuthenticationEntryPoint(java.lang.String)
     */
    public AjaxAwareAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOG.debug("commence");
        if ("ajax".equals(request.getHeader("X-ats-type"))) {
            LOG.debug("AJAX request detected - sending error code.");
            response.sendError(440, "Session Timeout");
        } else {
            LOG.debug("Delegating processing to parent class");           
            super.commence(request, response, authException);
        } 
    }
}
