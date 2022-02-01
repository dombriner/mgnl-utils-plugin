package services.classbasedresolver;

public class Property {
    private String qualifiedClass;
    private PropertyType type;

    public Property(String qualifiedClass, PropertyType type) {
        this.qualifiedClass = qualifiedClass;
        this.type = type;
    }

    public String getQualifiedClass() {
        return qualifiedClass;
    }

    public void setQualifiedClass(String qualifiedClass) {
        this.qualifiedClass = qualifiedClass;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }
}
