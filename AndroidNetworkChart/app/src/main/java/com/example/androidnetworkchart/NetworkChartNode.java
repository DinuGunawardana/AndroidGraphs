package com.example.androidnetworkchart;

import java.util.List;

public class NetworkChartNode {
    private String id;
    private String name;
    private List<String> targets;

    public NetworkChartNode() {
        // Default constructor required for calls to DataSnapshot.getValue(NetworkChartNode.class)
    }

    public NetworkChartNode(String id, String name, List<String> targets) {
        this.id = id;
        this.name = name;
        this.targets = targets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
}



