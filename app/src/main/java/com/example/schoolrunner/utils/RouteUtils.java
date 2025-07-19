package com.example.schoolrunner.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for campus route information
 */
public class RouteUtils {

    // List of predefined campus locations
    public static final List<String> CAMPUS_PLACES = Arrays.asList(
            "Haking Wong Building",           // Canteen 1
            "Chong Yuet Ming Building",       // Canteen 2 (Chong Yuet Ming Amenities / Physics direction)
            "Library Building",               // Library
            "Main Building",                  // Teaching Building 1
            "Chow Yei Ching Building"         // Teaching Building 2
    );

    // Predefined routes between campus locations
    private static final Map<String, List<String>> ROUTES_MAP = new HashMap<>();

    static {
        // Initialize all predefined routes
        List<String> road1 = Arrays.asList(
                "Haking Wong Building",
                "Hui Oi Chow Science Building (along University Street westward)",
                "Run Run Shaw Building (through the platform)",
                "Chong Yuet Ming Building"
        );

        List<String> road2 = Arrays.asList(
                "Haking Wong Building",
                "Sun Yat‑sen Place (down the stairs/slope)",
                "Library Building"
        );

        List<String> road3 = Arrays.asList(
                "Haking Wong Building",
                "Library Building",
                "Main Building"
        );

        List<String> road4 = Arrays.asList(
                "Haking Wong Building",
                "Simon K. Y. Lee Hall (University Street eastward)",
                "Chow Yei Ching Building"
        );

        List<String> road5 = Arrays.asList(
                "Chong Yuet Ming Building",
                "Knowles Building (via connecting corridor)",
                "Library Building"
        );

        List<String> road6 = Arrays.asList(
                "Chong Yuet Ming Building",
                "Knowles Building",
                "Library Building",
                "Main Building"
        );

        List<String> road7 = Arrays.asList(
                "Chong Yuet Ming Building",
                "Run Run Shaw Building",
                "Haking Wong Building",
                "Simon K. Y. Lee Hall",
                "Chow Yei Ching Building"
        );

        List<String> road8 = Arrays.asList(
                "Library Building",
                "Main Building"
        );

        List<String> road9 = Arrays.asList(
                "Library Building",
                "Haking Wong Building",
                "Simon K. Y. Lee Hall",
                "Chow Yei Ching Building"
        );

        List<String> road10 = Arrays.asList(
                "Main Building",
                "Library Building",
                "Haking Wong Building",
                "Simon K. Y. Lee Hall",
                "Chow Yei Ching Building"
        );

        // Add routes to map
        ROUTES_MAP.put("Haking Wong Building to Chong Yuet Ming Building", road1);
        ROUTES_MAP.put("Haking Wong Building to Library Building", road2);
        ROUTES_MAP.put("Haking Wong Building to Main Building", road3);
        ROUTES_MAP.put("Haking Wong Building to Chow Yei Ching Building", road4);
        ROUTES_MAP.put("Chong Yuet Ming Building to Library Building", road5);
        ROUTES_MAP.put("Chong Yuet Ming Building to Main Building", road6);
        ROUTES_MAP.put("Chong Yuet Ming Building to Chow Yei Ching Building", road7);
        ROUTES_MAP.put("Library Building to Main Building", road8);
        ROUTES_MAP.put("Library Building to Chow Yei Ching Building", road9);
        ROUTES_MAP.put("Main Building to Chow Yei Ching Building", road10);

        // Add reverse routes
        addReverseRoutes();
    }

    /**
     * Add reverse routes for all defined routes
     */
    private static void addReverseRoutes() {
        Map<String, List<String>> reversedRoutes = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : ROUTES_MAP.entrySet()) {
            String key = entry.getKey();
            List<String> route = entry.getValue();
            
            // Split the key to get start and end locations
            String[] locations = key.split(" to ");
            if (locations.length == 2) {
                String reverseKey = locations[1] + " to " + locations[0];
                
                // Create reversed route
                List<String> reverseRoute = new ArrayList<>(route);
                java.util.Collections.reverse(reverseRoute);
                
                reversedRoutes.put(reverseKey, reverseRoute);
            }
        }
        
        // Add all reversed routes to the map
        ROUTES_MAP.putAll(reversedRoutes);
    }

    /**
     * Check if a location is one of the predefined campus places
     * 
     * @param location Location to check
     * @return True if the location is a predefined campus place
     */
    public static boolean isCampusPlace(String location) {
        return CAMPUS_PLACES.contains(location);
    }

    /**
     * Get the route between two campus locations
     * 
     * @param from Starting location
     * @param to Destination location
     * @return List of waypoints if a route exists, null otherwise
     */
    public static List<String> getRoute(String from, String to) {
        // If either location is not a campus place, return null
        if (!isCampusPlace(from) || !isCampusPlace(to)) {
            return null;
        }
        
        // If start and end are the same, return a single-point route
        if (from.equals(to)) {
            return Arrays.asList(from);
        }
        
        String routeKey = from + " to " + to;
        return ROUTES_MAP.get(routeKey);
    }

    /**
     * Format route information as a readable string
     * 
     * @param route List of waypoints
     * @return Formatted route string
     */
    public static String formatRouteString(List<String> route) {
        if (route == null || route.isEmpty()) {
            return "No route available";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Route: ");
        
        for (int i = 0; i < route.size(); i++) {
            sb.append(route.get(i));
            if (i < route.size() - 1) {
                sb.append(" → ");
            }
        }
        
        return sb.toString();
    }
} 