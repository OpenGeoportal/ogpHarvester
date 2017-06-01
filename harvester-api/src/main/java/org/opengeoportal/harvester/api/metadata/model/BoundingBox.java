package org.opengeoportal.harvester.api.metadata.model;

/**
 * A simple class that represents a bounding box in geodetic coordinates, with
 * some convenience methods
 * 
 * @author chris
 *
 */
public class BoundingBox {
    private static Boolean isInRange(final Double var, final Double low,
            final Double high) {
        if ((var >= low) && (var <= high)) {
            return true;
        } else {
            return false;
        }
    }

    private Double minX;
    private Double minY;
    private Double maxX;

    private Double maxY;

    /**
     * BoundingBox constructor from Doubles
     * 
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public BoundingBox(final Double minX, final Double minY, final Double maxX,
            final Double maxY) {
        this.init(minX, minY, maxX, maxY);
    }

    /**
     * BoundingBox constructor from Strings
     * 
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public BoundingBox(final String minX, final String minY, final String maxX,
            final String maxY) {
        this.init(Double.parseDouble(minX), Double.parseDouble(minY),
                Double.parseDouble(maxX), Double.parseDouble(maxY));
    }

    /*
     * <gml:Box srsName="http://www.opengis.net/gml/srs/epsg.xml#4326">
     * <gml:coordinates>-75.102613,40.212597
     * -72.361859,41.512517</gml:coordinates> </gml:Box>
     */
    public String generateGMLBox(final int epsgCode) {
        final String envelope = "<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#"
                + String.valueOf(epsgCode) + "\">" + "<gml:coordinates>"
                + Double.toString(this.getMinX()) + ","
                + Double.toString(this.getMinY()) + " "
                + Double.toString(this.getMaxX()) + ","
                + Double.toString(this.getMaxY()) + "</gml:coordinates>"
                + "</gml:Box>";
        return envelope;
    }

    public String generateGMLEnvelope(final int epsgCode) {
        final String envelope = "<gml:Envelope srsName=\"http://www.opengis.net/gml/srs/epsg.xml#"
                + String.valueOf(epsgCode) + "\">" + "<gml:pos>"
                + Double.toString(this.getMinX()) + " "
                + Double.toString(this.getMinY()) + "</gml:pos>" + "<gml:pos>"
                + Double.toString(this.getMaxX()) + " "
                + Double.toString(this.getMaxY()) + "</gml:pos>"
                + "</gml:Envelope>";
        return envelope;
    }

    public Double getArea() {
        return this.getLength(this.minX, this.maxX)
                * this.getLength(this.minY, this.maxY);

    }

    public Double getAspectRatio() {
        Double aspectRatio = (this.getMinX() - this.getMaxX())
                / (this.getMinY() - this.getMaxY());
        aspectRatio = Math.abs(aspectRatio);
        return aspectRatio;
    }

    private Double getCenter(final Double a, final Double b) {
        return (a + b) / 2.;
    }

    public Double getCenterX() {
        return this.getCenter(this.minX, this.maxX);
    }

    public Double getCenterY() {
        return this.getCenter(this.minY, this.maxY);
    }

    public Double getHeight() {
        return Math.abs(this.minY - this.maxY);
    }

    public BoundingBox getIntersection(final BoundingBox anotherBoundingBox) {
        final Double intersectionMinX = Math.max(this.getMinX(),
                anotherBoundingBox.getMinX());
        final Double intersectionMaxX = Math.min(this.getMaxX(),
                anotherBoundingBox.getMaxX());
        final Double intersectionMinY = Math.max(this.getMinY(),
                anotherBoundingBox.getMinY());
        final Double intersectionMaxY = Math.min(this.getMaxY(),
                anotherBoundingBox.getMaxY());
        final BoundingBox intersection = new BoundingBox(intersectionMinX,
                intersectionMinY, intersectionMaxX, intersectionMaxY);
        return intersection;
    }

    private Double getLength(final Double a, final Double b) {
        return Math.abs(a - b);
    }

    public Double getMaxX() {
        return this.maxX;
    }

    public Double getMaxY() {
        return this.maxY;
    }

    public Double getMinX() {
        return this.minX;
    }

    public Double getMinY() {
        return this.minY;
    }

    public Double getWidth() {
        return Math.abs(this.minX - this.maxX);

    }

    private void init(final Double minX, final Double minY, final Double maxX,
            final Double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public Boolean isEquivalent(final BoundingBox anotherBoundingBox) {
        final double acceptableDelta = .001;
        if ((Math.abs(this.minX - anotherBoundingBox.minX) < acceptableDelta)
                && (Math.abs(
                        this.minX - anotherBoundingBox.minX) < acceptableDelta)
                && (Math.abs(
                        this.minX - anotherBoundingBox.minX) < acceptableDelta)
                && (Math.abs(this.minX
                        - anotherBoundingBox.minX) < acceptableDelta)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isValid() {
        if ((this.getMinX() == null) || (this.getMaxX() == null)
                || (this.getMinY() == null) || (this.getMaxY() == null)) {
            return false;
        }

        if (BoundingBox.isInRange(this.getMinX(), -180.0, 180.0)
                && BoundingBox.isInRange(this.getMaxX(), -180.0, 180.0)
                && BoundingBox.isInRange(this.getMinY(), -90.0, 90.0)
                && BoundingBox.isInRange(this.getMaxY(), -90.0, 90.0)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Double.toString(this.minX) + "," + Double.toString(this.minY)
                + "," + Double.toString(this.maxX) + ","
                + Double.toString(this.maxY);
    }

    public String toString1_3() {
        // wms 1.3 reverses the axes
        return Double.toString(this.minY) + "," + Double.toString(this.minX)
                + "," + Double.toString(this.maxY) + ","
                + Double.toString(this.maxX);
    }

}
