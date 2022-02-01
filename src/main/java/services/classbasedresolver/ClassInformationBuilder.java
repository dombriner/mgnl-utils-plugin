package services.classbasedresolver;

import java.util.ArrayList;
import java.util.List;

public class ClassInformationBuilder {
    private String name;
    private String qualifiedClass;
    private List<ClassInformation> properties = new ArrayList<>();
    private PropertyType type;

    public ClassInformationBuilder(String name) {
        this.name = name;
    }

    public ClassInformationBuilder withClass(String qualifiedClass) {
        this.qualifiedClass = qualifiedClass;
        return this;
    }

    public ClassInformationBuilder withProperties(List<ClassInformation> properties) {
        this.properties = properties;
        return this;
    }

    public ClassInformationBuilder withType(PropertyType type) {
        this.type = type;
        return this;
    }

    public ClassInformationBuilder addProperty(ClassInformation property) {
        if (!properties.contains(property))
            properties.add(property);
        return this;
    }
}
