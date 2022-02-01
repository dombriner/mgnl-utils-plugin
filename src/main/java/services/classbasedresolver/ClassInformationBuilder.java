package services.classbasedresolver;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ClassInformationBuilder {
    private String qualifiedClass= null;
    private String type= null;
    private List<Property> properties = new ArrayList<>();

    public ClassInformationBuilder() {
    }

    public ClassInformationBuilder withClass(String qualifiedClass) {
        this.qualifiedClass = qualifiedClass;
        return this;
    }

    public ClassInformationBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public ClassInformationBuilder withProperties(List<Property> properties) {
        this.properties = properties;
        return this;
    }

    public ClassInformationBuilder addProperty(Property property) {
        if (!properties.contains(property))
            properties.add(property);
        return this;
    }

    public ClassInformation build() throws ClassInformationBuildException {
        if (StringUtils.isEmpty(qualifiedClass))
            throw new ClassInformationBuildException("Class cannot be null");
        return new ClassInformation()
    }
}
