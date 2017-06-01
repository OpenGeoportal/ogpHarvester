/*
 * PageWrapper.java
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

import org.springframework.data.domain.Page;

/**
 * Wrap a {@link Page} hidding the contents and only expose attributes related
 * with page size, number, total elements count, ...
 * 
 * @author jlrodriguez
 *
 */
public class PageWrapper {
    private final Page<?> page;

    public PageWrapper(final Page<?> page) {
        this.page = page;
    }

    /**
     * Returns the number of the current page. Is always non-negative and less
     * that {@code Page#getTotalPages()}.
     * 
     * @return the number of the current page
     */
    public int getNumber() {
        return this.page.getNumber() + 1;
    }

    /**
     * Returns the number of elements currently on this page.
     * 
     * @return the number of elements currently on this page
     */
    public int getNumberOfElements() {
        return this.page.getNumberOfElements();
    }

    /**
     * Returns the size of the page.
     * 
     * @return the size of the page
     */
    public int getSize() {
        return this.page.getSize();
    }

    /**
     * Returns the total amount of elements.
     * 
     * @return the total amount of elements
     */
    public long getTotalElements() {
        return this.page.getTotalElements();
    }

    /**
     * Returns the number of total pages.
     * 
     * @return the number of toral pages
     */
    public int getTotalPages() {
        return this.page.getTotalPages();
    }

    /**
     * Returns if there is a next page.
     * 
     * @return if there is a next page
     */
    public boolean hasNextPage() {
        return this.page.hasNextPage();
    }

    /**
     * Returns if there is a previous page.
     * 
     * @return if there is a previous page
     */
    public boolean hasPreviousPage() {
        return this.page.hasPreviousPage();
    }

    /**
     * Returns whether the current page is the first one.
     * 
     * @return
     */
    public boolean isFirstPage() {
        return this.page.isFirstPage();
    }

    /**
     * Returns whether the current page is the last one.
     * 
     * @return
     */
    public boolean isLastPage() {
        return this.page.isLastPage();
    }
}
