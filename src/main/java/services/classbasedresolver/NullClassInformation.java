package services.classbasedresolver;

import java.util.ArrayList;
import java.util.List;

public class NullClassInformation extends ClassInformation {

    public NullClassInformation() {
        super("", "", new ArrayList<>());
    }

    @Override
    public void setQualifiedClass(String qualifiedClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public void setProperties(List<Property> properties) {
        throw new UnsupportedOperationException();
    }
}
