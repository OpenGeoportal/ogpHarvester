package org.opengeoportal.harvester.api.client.csw.response;

import org.jdom.Element;

import java.util.List;


public class GetRecordsResponse {
    private int numberOfRecordsMatched;
    private int numberOfRecordsReturned;
    private String elementSet;
    private int nextRecord;

    List<Element> results;


    public int getNumberOfRecordsMatched() {
        return numberOfRecordsMatched;
    }

    public void setNumberOfRecordsMatched(int numberOfRecordsMatched) {
        this.numberOfRecordsMatched = numberOfRecordsMatched;
    }

    public int getNumberOfRecordsReturned() {
        return numberOfRecordsReturned;
    }

    public void setNumberOfRecordsReturned(int numberOfRecordsReturned) {
        this.numberOfRecordsReturned = numberOfRecordsReturned;
    }

    public String getElementSet() {
        return elementSet;
    }

    public void setElementSet(String elementSet) {
        this.elementSet = elementSet;
    }

    public int getNextRecord() {
        return nextRecord;
    }

    public void setNextRecord(int nextRecord) {
        this.nextRecord = nextRecord;
    }

    public List<Element> getResults() {
        return results;
    }

    public void setResults(List<Element> results) {
        this.results = results;
    }
}
