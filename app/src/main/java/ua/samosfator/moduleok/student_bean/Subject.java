package ua.samosfator.moduleok.student_bean;

import java.util.List;

public class Subject {

    private String name;
    private String controlType;
    private List<Module> modules;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public Module getLastModule() {
        return modules.get(modules.size() - 1);
    }

    public int getTotalScore() {
        double totalScore = 0;
        for (Module module : modules) {
            totalScore += module.getScore() * module.getWeight() / 100;
        }
        return (int) totalScore;
    }
}