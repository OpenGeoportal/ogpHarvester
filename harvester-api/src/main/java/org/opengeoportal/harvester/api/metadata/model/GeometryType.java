package org.opengeoportal.harvester.api.metadata.model;

public enum GeometryType {
    Point(DataType.Vector), Line(DataType.Vector), Polygon(
            DataType.Vector), Raster(DataType.Raster), ScannedMap(
                    DataType.Raster), Undefined(DataType.Vector), // we're going
                                                                  // to assume
                                                                  // vector if
                                                                  // unknown
    PaperMap(DataType.Paper), LibraryRecord(DataType.Paper);

    enum DataType {
        Raster, Vector, Paper, Undefined
    }

    public static Boolean isRaster(final GeometryType geometryType) {
        if (geometryType.dataType.equals(DataType.Raster)) {
            return true;
        } else {
            return false;
        }
    };

    public static Boolean isVector(final GeometryType geometryType) {
        if (geometryType.dataType.equals(DataType.Vector)) {
            return true;
        } else {
            return false;
        }
    }

    public static GeometryType parseGeometryType(String geometryString) {
        geometryString = geometryString.trim();
        // legacy case
        if (geometryString.equalsIgnoreCase("Paper Map")) {
            return GeometryType.ScannedMap;
        }
        for (final GeometryType geomType : GeometryType.values()) {
            if (geomType.toString().equalsIgnoreCase(geometryString)) {
                return geomType;
            }
        }
        return GeometryType.Undefined;
    }

    private final DataType dataType;

    GeometryType(final DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        if (this.equals(GeometryType.Point)) {
            return "Point";
        } else if (this.equals(GeometryType.Line)) {
            return "Line";
        } else if (this.equals(GeometryType.Polygon)) {
            return "Polygon";
        } else if (this.equals(GeometryType.Raster)) {
            return "Raster";
        } else if (this.equals(GeometryType.PaperMap)) {
            return "Paper Map";
        } else if (this.equals(GeometryType.ScannedMap)) {
            return "Scanned Map";
        } else if (this.equals(GeometryType.LibraryRecord)) {
            return "Library Record";
        } else if (this.equals(GeometryType.Undefined)) {
            return "Undefined";
        } else {
            return "";
        }
    }

}
