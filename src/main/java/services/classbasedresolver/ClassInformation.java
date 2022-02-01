package services.classbasedresolver;

import java.util.ArrayList;
import java.util.List;

public class ClassInformation {
    private String name;
    private String qualifiedClass;
    private List<ClassInformation> properties = new ArrayList<>();
    private PropertyType type;

    // TODO: Builder
    public ClassInformation(String name, String qualifiedClass) {
        this(name, qualifiedClass, new ArrayList<>());
    }

    public ClassInformation(String name, String qualifiedClass, List<ClassInformation> properties) {
        this(name, qualifiedClass, properties, null);
    }

    public ClassInformation(String name, String qualifiedClass, List<ClassInformation> properties, PropertyType type) {
        this.name = name;
        this.qualifiedClass = qualifiedClass;
        this.properties = properties;
        this.type = type;
    }

    public void addProperty(ClassInformation property) {
        properties.add(property);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedClass() {
        return qualifiedClass;
    }

    public void setQualifiedClass(String qualifiedClass) {
        this.qualifiedClass = qualifiedClass;
    }

    public List<ClassInformation> getProperties() {
        return properties;
    }

    public void setProperties(List<ClassInformation> properties) {
        this.properties = properties;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }
}
