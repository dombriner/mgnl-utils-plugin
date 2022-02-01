package services.classbasedresolver;

import java.util.ArrayList;
import java.util.List;

public class NullClassInformation extends ClassInformation {
    private String name;
    private String qualifiedClass;
    private List<NullClassInformation> properties;

    public NullClassInformation() {
        super("", "", new ArrayList<>(), );
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setQualifiedClass(String qualifiedClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperties(List<ClassInformation> properties) {
        throw new UnsupportedOperationException();
    }
}
