/*
 * DataType.java
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
package org.opengeoportal.harvester.api.domain;

/**
 * @author jlrodriguez
 *
 */
public enum DataType {
    POINT, LINE, POLYGON, RASTER, SCANNED;

    @Override
    public String toString() {
        if (this.equals(DataType.POINT)) {
            return "Point";
        } else if (this.equals(DataType.LINE)) {
            return "Line";
        } else if (this.equals(DataType.POLYGON)) {
            return "Polygon";
        } else if (this.equals(DataType.RASTER)) {
            return "Raster";
        } else if (this.equals(DataType.SCANNED)) {
            return "Paper Map";
        } else {
            return "";
        }
    }
}
