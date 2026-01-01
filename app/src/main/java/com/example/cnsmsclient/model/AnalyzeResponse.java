package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnalyzeResponse {

    @SerializedName("incident_id")
    private int incidentId;
    @SerializedName("labels")
    private List<Label> labels;
    @SerializedName("severity")
    private String severity;
    @SerializedName("recommendation")
    private String recommendation;
    @SerializedName("analyzed_at")
    private String analyzedAt;

    public int getIncidentId() { return incidentId; }
    public List<Label> getLabels() { return labels; }
    public String getSeverity() { return severity; }
    public String getRecommendation() { return recommendation; }
    public String getAnalyzedAt() { return analyzedAt; }

    public static class Label {
        @SerializedName("name")
        private String name;
        @SerializedName("confidence")
        private float confidence;

        public String getName() { return name; }
        public float getConfidence() { return confidence; }
    }

    public static class AnalyzeRequest {
        @SerializedName("incident_id")
        private final int incidentId;

        public AnalyzeRequest(int incidentId) {
            this.incidentId = incidentId;
        }
    }
}
