package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MapData {
    @SerializedName("map_image")
    private String mapImageUrl;
    @SerializedName("nodes")
    private List<MapNode> nodes;

    public String getMapImageUrl() {
        return mapImageUrl;
    }

    public List<MapNode> getNodes() {
        return nodes;
    }

    public static class MapNode {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("x")
        private int x;
        @SerializedName("y")
        private int y;
        @SerializedName("desc")
        private String description;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String getDescription() {
            return description;
        }
    }

    // RouteResponse class for navigation routes
    public static class RouteResponse {
        @SerializedName("path")
        private List<MapNode> path;

        @SerializedName("distance")
        private double distance;

        @SerializedName("estimated_time")
        private int estimatedTimeMinutes;

        @SerializedName("instructions")
        private List<String> instructions;

        public List<MapNode> getPath() {
            return path;
        }

        public double getDistance() {
            return distance;
        }

        public int getEstimatedTimeMinutes() {
            return estimatedTimeMinutes;
        }

        public List<String> getInstructions() {
            return instructions;
        }
    }
}
