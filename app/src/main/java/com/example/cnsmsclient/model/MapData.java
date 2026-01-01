package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MapData {
    @SerializedName("map_image")
    private String mapImageUrl;
    @SerializedName("nodes")
    private List<MapNode> nodes;

    public String getMapImageUrl() { return mapImageUrl; }
    public List<MapNode> getNodes() { return nodes; }

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

        public int getId() { return id; }
        public String getName() { return name; }
        public int getX() { return x; }
        public int getY() { return y; }
        public String getDescription() { return description; }
    }
}
