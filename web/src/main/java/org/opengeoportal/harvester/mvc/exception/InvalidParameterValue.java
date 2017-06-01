/*
 * InvalidParameterValue.java
 *
 * Copyright (C) 2013
 *
 * This file is part of Open Geoportal Harvester
 *
 * This software is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * As a special exception, if you link this library with other files to produce
 * an executable, this library does not by itself cause the resulting executable
 * to be covered by the GNU General Public License. This exception does not
 * however invalidate any other reasons why the executable file might be covered
 * by the GNU General Public License.
 *
 * Authors:: Juan Luis Rodriguez Ponce (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc.exception;

/**
 * Thrown when a parameter has not a valid value.
 *
 * @author jlrodriguez
 */
public class InvalidParameterValue extends RuntimeException {

    private static final long serialVersionUID = -2875967026341351607L;

    /**
     * 
     */
    public InvalidParameterValue() {
    }

    /**
     * @param message
     */
    public InvalidParameterValue(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidParameterValue(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public InvalidParameterValue(final Throwable cause) {
        super(cause);
    }

}
