/*
 * RemoteRepositoryFormBean.java
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

import org.opengeoportal.harvester.api.domain.InstanceType;

/**
 * @author jlrodriguez
 *
 */
public class RemoteRepositoryFormBean {
	private InstanceType repoType;
	private String repoUrl;
	private String name;
	/**
	 * @return the repoType
	 */
	public InstanceType getRepoType() {
		return repoType;
	}
	/**
	 * @param repoType the repoType to set
	 */
	public void setRepoType(InstanceType repoType) {
		this.repoType = repoType;
	}
	/**
	 * @return the repoUrl
	 */
	public String getRepoUrl() {
		return repoUrl;
	}
	/**
	 * @param repoUrl the repoUrl to set
	 */
	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
