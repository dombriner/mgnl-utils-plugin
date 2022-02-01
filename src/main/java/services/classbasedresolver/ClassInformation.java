package services.classbasedresolver;

import java.util.ArrayList;
import java.util.List;

public class ClassInformation {
    private String qualifiedClass;
    private String type;
    private List<Property> properties = new ArrayList<>();

    private ClassInformation(String qualifiedClass) {
        this(qualifiedClass, null);
    }

    // TODO: Builder
    public ClassInformation(String qualifiedClass, String type) {
        this(qualifiedClass, type, new ArrayList<>());
    }

    public ClassInformation(String qualifiedClass, String type, List<Property> properties) {
        this.qualifiedClass = qualifiedClass;
        this.type = type;
        this.properties = properties;
    }

    public void addProperty(Property property) {
        properties.add(property);
    }

    public String getQualifiedClass() {
        return qualifiedClass;
    }

    public void setQualifiedClass(String qualifiedClass) {
        this.qualifiedClass = qualifiedClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}
