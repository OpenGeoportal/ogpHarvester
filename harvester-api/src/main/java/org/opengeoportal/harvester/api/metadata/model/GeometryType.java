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
		enum DataType {Raster, Vector, Paper, Undefined};

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
			geometryString = geometryString.trim();
			for (GeometryType geomType : GeometryType.values()){
				if (geomType.toString().equalsIgnoreCase(geometryString)){
					return geomType;
				}
			}
			return GeometryType.Undefined;
		}
		
		public static Boolean isVector(GeometryType geometryType){
			if (geometryType.dataType.equals(DataType.Vector)){
				return true;
			} else {
				return false;
			}
		}
		
		public static Boolean isRaster(GeometryType geometryType){
			if (geometryType.dataType.equals(DataType.Raster)){
				return true;
			} else {
				return false;
			}
		}


}
