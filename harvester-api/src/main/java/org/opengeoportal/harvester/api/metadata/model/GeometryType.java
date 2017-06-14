package org.opengeoportal.harvester.api.metadata.model;

public enum GeometryType {
		Point (DataType.Vector),
		Line (DataType.Vector),
		Polygon (DataType.Vector),
		Raster (DataType.Raster),
		ScannedMap (DataType.Raster),
		Undefined (DataType.Vector), //we're going to assume vector if unknown
		PaperMap (DataType.Paper), 
		LibraryRecord(DataType.Paper);
		
		private final DataType dataType;

    enum DataType {Raster, Vector, Paper, Undefined}

		GeometryType(DataType dataType){
			this.dataType = dataType;
		}

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
        
		public static GeometryType parseGeometryType(String geometryString){
            //trim the string and remove internal spaces
            geometryString = geometryString.trim();//.replace(" ", "");

			for (GeometryType geomType : GeometryType.values()){
				if (geomType.toString().equalsIgnoreCase(geometryString)){
					return geomType;
                } else if (geometryString.equalsIgnoreCase("ScannedMap")) {
                    return GeometryType.ScannedMap;
                }
            }
            return GeometryType.Undefined;
		}
		
		public static Boolean isVector(GeometryType geometryType){
            return geometryType.dataType.equals(DataType.Vector);
        }

    public static Boolean isRaster(GeometryType geometryType){
            return geometryType.dataType.equals(DataType.Raster);
        }


}
