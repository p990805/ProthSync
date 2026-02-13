package com.prothsync.prothsync.service;

public interface GeocodingService {

    GeocodingResult geocode(String address);


    record GeocodingResult(
        Double latitude,
        Double longitude,
        boolean success
    ){
        public static GeocodingResult success(Double latitude, Double longitude){
            return new GeocodingResult(latitude, longitude, true);
        }

        public static GeocodingResult fail(){
            return new GeocodingResult(null,null,false);
        }
    }

}
