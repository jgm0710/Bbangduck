package bbangduck.bd.bbangduck.global.common.util;


import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;

public interface DistanceUtil {
  static double getDistance(Location originLocation, Location remoteLocation) {
    double lon1 = originLocation.getLongitude();
    double lon2 = remoteLocation.getLongitude();
    double lat1 = remoteLocation.getLatitude();
    double lat2 = remoteLocation.getLatitude();

    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;

    return dist * 1.609344;
  }

  private static double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private static double rad2deg(double rad) {
    return (rad * 180 / Math.PI);
  }

  static double calculateLatitudeDistance(double latitude, int distanceMetKm) {
    return latitude - distanceMetKm / 109.958489129649955;
  }

  static double calculateLongitudeDistance(double longitude, int distanceKm) {
    return longitude - distanceKm / 88.74;
  }

}
