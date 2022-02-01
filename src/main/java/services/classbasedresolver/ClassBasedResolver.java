package services.classbasedresolver;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.util.xmlb.annotations.MapAnnotation;

import com.twelvemonkeys.util.NullMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Service
public class ClassBasedResolver implements PersistentStateComponent<ClassBasedResolver.MagnoliaClassesState> {
    public static final String CLASS_PROPERTY = "class";
    public static final String PROPERTIES_PROPERTY = "properties";
    public static final String TYPE_PROPERTY = "type";

    private MagnoliaClassesState classesCache;
    private boolean calculatingClassMap = false;
    private boolean reading = false;

    public static ClassBasedResolver getInstance() {
        return ApplicationManager.getApplication().getService(ClassBasedResolver.class);
    }

    @Override
    public @Nullable MagnoliaClassesState getState() {
        return classesCache;
    }

    @Override
    public void loadState(@NotNull MagnoliaClassesState state) {
        classesCache = state;
    }

    public ClassInformation getClassStructure(String clazz, String version) {
        if (isCalculatingClassMap())
            return new NullClassInformation();
        setReading(true);
        List<ClassInformation> versionMap = getVersionClassInfo(version);
        if (versionMap.isEmpty()) {
            setReading(false);
            runClassMapCalculation();
            return new NullMap();
        }
        Map classMap = getClassMapIfExisting(clazz, versionMap);
        setReading(false);
        return classMap;
    }



    /**
     * @param clazz
     * @param outerMap
     * @return Clasnds map or null map if no class map was fou
     */
    private Map getClassMapIfExisting(String clazz, Map outerMap) {
        for (Object key: outerMap.keySet()) {
            if (key instanceof String && outerMap.get(key) instanceof Map) {
                if (((String) key).toLowerCase().equals(clazz))
                    return (Map) outerMap.get(key);
                else {
                    Map classMap = getClassMapIfExisting(clazz, (Map) outerMap.get(key));
                    if (!(classMap instanceof NullMap))
                        return classMap;
                }
            }
        }
        return new NullMap();
    }

    public void runClassMapCalculation() {

    }

    private List<ClassInformation> getVersionClassInfo(String version) {
        if (getState() == null)
            return new ArrayList<>();
        Map<String, ?> versionMap = getState().state.get(version);
        ArrayList<ClassInformation> classInfos = new ArrayList<>();
        for (String propertyName: versionMap.keySet()) {
            classInfos.add(transformToClassInformation(propertyName, versionMap.get(propertyName)));
        }
    }

    // TODO: Castingexception abfangen
    public ClassInformation transformToClassInformation(String propertyName, Object mapValue) {
        ClassInformationBuilder classInformationBuilder = new ClassInformationBuilder(propertyName);
        if (mapValue instanceof Map) {
            Map<String, Object> classMap = (Map<String, Object>) mapValue;
            Object clazz = classMap.get(CLASS_PROPERTY);
            classInformationBuilder.withClass((String) clazz);


        } else {
            return new NullClassInformation();
        }


        return new ClassInformation(propertyName, );
    }

    public boolean isCalculatingClassMap() {
        return calculatingClassMap;
    }

    public synchronized void setCalculatingClassMap(boolean calculatingClassMap) {
        this.calculatingClassMap = calculatingClassMap;
    }

    public boolean isReading() {
        return reading;
    }

    public void setReading(boolean reading) {
        this.reading = reading;
    }

    static class MagnoliaClassesState {
        @MapAnnotation
        private Map<String, Map> state;

        public MagnoliaClassesState() {
            this.state = new HashMap<>();
        }
    }

    /*
    * PLAN:
    * Whenever class property model is requested:
    * - Load current structure. Search for the class. If it exists, return it.
    * - If it doesn't exist, return nothing but in a background thread, scan class file and update state to reflect
    *   new structure. So next time, the class structure is returned.
    * - Maybe a synchronized flag is needed to return nothing while a change of structure is in progress
    *
    * Class structure:
    * Nested maps, e.g.
    * BrowserDescriptor
    *
    *
    *
    * */
}
