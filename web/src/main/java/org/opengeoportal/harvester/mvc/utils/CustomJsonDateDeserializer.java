/*
 * CustomJsonDateSerializer.java
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
package org.opengeoportal.harvester.mvc.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

/**
 * @author jlrodriguez
 *
 */
public class CustomJsonDateDeserializer extends JsonDeserializer<Date> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
     * .jackson.core.JsonParser,
     * com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public Date deserialize(final JsonParser jp,
            final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        final String pattern = "MM/dd/yyyy";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        final String dateText = jp.getText();
        Date date;
        try {
            date = dateFormat.parse(dateText);
        } catch (final ParseException e) {
            throw new InvalidFormatException("Date \"" + dateText
                    + "\" can not be parsed in with format \"" + pattern + "\"",
                    dateText, Date.class);
        }

        return date;
    }
}
