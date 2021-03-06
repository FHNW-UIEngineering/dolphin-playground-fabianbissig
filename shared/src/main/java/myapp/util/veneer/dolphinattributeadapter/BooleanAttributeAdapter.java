package myapp.util.veneer.dolphinattributeadapter;

import javafx.beans.property.SimpleBooleanProperty;

import org.opendolphin.core.Attribute;

public class BooleanAttributeAdapter extends SimpleBooleanProperty{
    private final ObjectAttributeAdapter<Boolean> wrapper;

    public BooleanAttributeAdapter(Attribute attribute) {
        wrapper = new ObjectAttributeAdapter<>(attribute);
        wrapper.addListener((observable, oldValue, newValue) -> fireValueChangedEvent());
    }

    @Override
    public String getName() {
        return wrapper.getName();
    }

    @Override
    public void set(boolean newValue) {
        wrapper.set(newValue);
    }

    @Override
    public void setValue(Boolean v) {
        set(v);
    }

    @Override
    public boolean get() {
        return wrapper.getValue();
    }

    @Override
    public Boolean getValue() {
        return get();
    }
}
