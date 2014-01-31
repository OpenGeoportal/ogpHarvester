/*
 * IngestFormBean.java
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
 * Authors:: Jose García (mailto:jose.garcia@geocat.net)
 */
package org.opengeoportal.harvester.api.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class IngestReport extends AbstractPersistable<Long> {
	private static final long serialVersionUID = 2909056496657495298L;

	@Column
	private long restrictedRecords;

	@Column
	private long publicRecords;

	@Column
	private long vectorRecords;

	@Column
	private long rasterRecords;

	@Column
	private long unrequiredFieldWarnings;

	@Column
	private long webServiceWarnings;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<IngestReportError> errors = new ArrayList<IngestReportError>();

	public long getRestrictedRecords() {
		return restrictedRecords;
	}

	public void setRestrictedRecords(long restrictedRecords) {
		this.restrictedRecords = restrictedRecords;
	}

	public long getPublicRecords() {
		return publicRecords;
	}

	public void setPublicRecords(long publicRecords) {
		this.publicRecords = publicRecords;
	}

	public long getVectorRecords() {
		return vectorRecords;
	}

	public void setVectorRecords(long vectorRecords) {
		this.vectorRecords = vectorRecords;
	}

	public long getRasterRecords() {
		return rasterRecords;
	}

	public void setRasterRecords(long rasterRecords) {
		this.rasterRecords = rasterRecords;
	}

	public long getUnrequiredFieldWarnings() {
		return unrequiredFieldWarnings;
	}

	public void setUnrequiredFieldWarnings(long unrequiredFieldWarnings) {
		this.unrequiredFieldWarnings = unrequiredFieldWarnings;
	}

	public void increaseUnrequiredFieldWarnings() {
		this.unrequiredFieldWarnings++;
	}

	public long getWebServiceWarnings() {
		return webServiceWarnings;
	}

	public void setWebServiceWarnings(long webServiceWarnings) {
		this.webServiceWarnings = webServiceWarnings;
	}

	public List<IngestReportError> getErrors() {
		return errors;
	}

	public void setErrors(List<IngestReportError> errors) {
		this.errors = errors;
	}

	public void addError(IngestReportError error) {
		errors.add(error);
	}

}
