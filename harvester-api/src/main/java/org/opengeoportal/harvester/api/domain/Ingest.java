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

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
public abstract class Ingest extends AbstractPersistable<Long> {
 private static final long serialVersionUID = -3390351777452085398L;  
 @Column(unique = true, nullable = false)
    private String name;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date beginDate;

    private String frequency;

    @Column(nullable = false)
    private String url;

    @ElementCollection
    private Set<String> requiredFields = new HashSet<String>();

    @Transient
    protected Set<String> validRequiredFields = new HashSet<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * Sets the attribute with the given name to the given value.
     *
     * @param field must not be {@literal null} or empty.
     */
    public void addRequiredField(String field) {
        Assert.hasText(field);

        if (validRequiredFields.contains(field)) requiredFields.add(field);
    }

    /**
     * Returns all the required fields of the {@link Ingest}.
     *
     * @return
     */
    public Set<String> getRequiredFields() {
        return Collections.unmodifiableSet(requiredFields);
    }


    /**
     * Returns all the valid required fields that can be added to the {@link Ingest}.
     *
     * @return
     */
    public Set<String> getValidRequiredFields() {
        return Collections.unmodifiableSet(validRequiredFields);
    }

}
