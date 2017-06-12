package myapp.presentationmodel.presentationstate;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.ValueType;



public enum ApplicationStateAtt implements AttributeDescription {


    // these are almost always needed
    APPLICATION_TITLE(ValueType.STRING),
    LANGUAGE(ValueType.STRING),
    CLEAN_DATA(ValueType.BOOLEAN);

    private final ValueType valueType;

    ApplicationStateAtt(ValueType type) {
        valueType = type;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.APPLICATION_STATE;
    }
}
