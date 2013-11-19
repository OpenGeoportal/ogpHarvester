/*
 * BoundingBox.java
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
package org.opengeoportal.harvester.mvc.bean;

import java.io.Serializable;

/**
 * @author jlrodriguez
 *
 */
public class BoundingBox  implements Serializable{

	private static final long serialVersionUID = -365308259496309151L;
	
	private Double minx;
	private Double miny;
	private Double maxx;
	private Double maxy;

	/**
	 * 
	 */
	public BoundingBox() {
		
	}

	/**
	 * @return the minx
	 */
	public Double getMinx() {
		return minx;
	}

	/**
	 * @param minx the minx to set
	 */
	public void setMinx(Double minx) {
		this.minx = minx;
	}

	/**
	 * @return the miny
	 */
	public Double getMiny() {
		return miny;
	}

	/**
	 * @param miny the miny to set
	 */
	public void setMiny(Double miny) {
		this.miny = miny;
	}

	/**
	 * @return the maxx
	 */
	public Double getMaxx() {
		return maxx;
	}

	/**
	 * @param maxx the maxx to set
	 */
	public void setMaxx(Double maxx) {
		this.maxx = maxx;
	}

	/**
	 * @return the maxy
	 */
	public Double getMaxy() {
		return maxy;
	}

	/**
	 * @param maxy the maxy to set
	 */
	public void setMaxy(Double maxy) {
		this.maxy = maxy;
	}

}
