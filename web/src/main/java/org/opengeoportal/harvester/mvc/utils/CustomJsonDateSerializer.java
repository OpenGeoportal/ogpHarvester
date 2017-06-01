/**
 * CustomDateSerializer.java
 *
 * Copyright (C) 2013
 *
 * This file is part of Open Geoportal Harvester.
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
 * Authors:: Juan Luis Rodríguez (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class CustomJsonDateSerializer extends JsonSerializer<Date> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object,
     * com.fasterxml.jackson.core.JsonGenerator,
     * com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(final Date value, final JsonGenerator jgen,
            final SerializerProvider provider)
            throws IOException, JsonProcessingException {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = null;
        if (value != null) {
            formattedDate = formatter.format(value);
        }

        jgen.writeString(formattedDate);

    }

}
