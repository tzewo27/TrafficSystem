package models;

import java.util.List;
import java.util.ArrayList;

// This holds the first aid instructions
// e.g. CPR steps, bleeding control steps
// This is the feature that makes your project UNIQUE

public class FirstAidGuide {

    private int id;
    private String title;       // "How to perform CPR"
    private String category;    // "CPR", "Bleeding", "Shock", "Burns"
    private List<String> steps; // the list of instructions step by step
    private String emergency;   // emergency phone number e.g. "907"

    public FirstAidGuide(String title, String category, String emergency) {
        this.title = title;
        this.category = category;
        this.emergency = emergency;
        this.steps = new ArrayList<>(); // empty list, we add steps one by one
    }

    // Add a step to the instructions
    // e.g. guide.addStep("Call 907 immediately")
    public void addStep(String step) {
        this.steps.add(step);
    }

    // Getters
    public int getId()              { return id; }
    public String getTitle()        { return title; }
    public String getCategory()     { return category; }
    public List<String> getSteps()  { return steps; }
    public String getEmergency()    { return emergency; }

    // Setters
    public void setId(int id)       { this.id = id; }

    @Override
    public String toString() {
        return "FirstAidGuide[title=" + title + ", steps=" + steps.size() + "]";
    }
}