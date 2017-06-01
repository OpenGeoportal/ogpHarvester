package org.opengeoportal.harvester.api.client.csw.response;

import java.util.List;

import org.jdom.Element;

public class GetRecordsResponse {
    private int numberOfRecordsMatched;
    private int numberOfRecordsReturned;
    private String elementSet;
    private int nextRecord;

    List<Element> results;

    public String getElementSet() {
        return this.elementSet;
    }

    public int getNextRecord() {
        return this.nextRecord;
    }

    public int getNumberOfRecordsMatched() {
        return this.numberOfRecordsMatched;
    }

    public int getNumberOfRecordsReturned() {
        return this.numberOfRecordsReturned;
    }

    public List<Element> getResults() {
        return this.results;
    }

    public void setElementSet(final String elementSet) {
        this.elementSet = elementSet;
    }

    public void setNextRecord(final int nextRecord) {
        this.nextRecord = nextRecord;
    }

    public void setNumberOfRecordsMatched(final int numberOfRecordsMatched) {
        this.numberOfRecordsMatched = numberOfRecordsMatched;
    }

    public void setNumberOfRecordsReturned(final int numberOfRecordsReturned) {
        this.numberOfRecordsReturned = numberOfRecordsReturned;
    }

    public void setResults(final List<Element> results) {
        this.results = results;
    }
}
