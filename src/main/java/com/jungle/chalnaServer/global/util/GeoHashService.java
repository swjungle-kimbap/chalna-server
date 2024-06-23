package com.jungle.chalnaServer.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GeoHashService {
    public static final String GEO_LOC_PREFIX = "GEO_LOCATIONS:";

    private final GeoOperations<String, String> geoOperations;

    public List<POSITION> radius(String key, String target, Double distance) {
        String geoKey = GEO_LOC_PREFIX + key;
        return geoOperations.radius(geoKey, target, distance).getContent().stream()
                .map((r) -> {
                    String name = r.getContent().getName();
                    return new POSITION(name, geoOperations.position(geoKey, name).get(0));
                }).toList();
    }

    public Double distance(String key, Point point, String target) {
        String geoKey = GEO_LOC_PREFIX + key;
        geoOperations.add(geoKey, point, "tmp");
        return geoOperations.distance(geoKey, target, "tmp").getValue();
    }

    public void set(String key, Point point, String name) {
        geoOperations.add(GEO_LOC_PREFIX + key, point, name);
    }

    public void delete(String key, String name) {
        geoOperations.remove(GEO_LOC_PREFIX + key, name);
    }

    public record POSITION(String name, Point point) {

    }
}
