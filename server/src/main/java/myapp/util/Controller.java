package myapp.util;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;

import myapp.presentationmodel.PMDescription;

import myapp.util.veneer.PresentationModelVeneer;

public abstract class Controller extends DolphinServerAction implements DolphinMixin, DTOMixin {

    private static final String RESOURCEBUNDLE_DIRECTORY = "resourcebundles/";

    @Override
    public final void registerIn(ActionRegistry registry) {
        registry.register(BasicCommands.INITIALIZE_BASE_PMS  , (command, response) -> initializeBasePMs());
        registry.register(BasicCommands.INITIALIZE_CONTROLLER, (command, response) -> initializeController());
        registerCommands(registry);
    }

    protected abstract void registerCommands(ActionRegistry actionRegistry);

    protected abstract void initializeBasePMs();

    protected void initializeController() {
        setupModelStoreListener();
        setupValueChangedListener();
        setupBinding();
        setDefaultValues();
        rebaseAll();
    }

    protected void setupModelStoreListener() {
    }

    protected void setupValueChangedListener() {
    }

    protected void setupBinding() {
    }

    protected void setDefaultValues() {
    }


    protected ServerPresentationModel createPM(PMDescription pmDescription, DTO dto) {
        long id = entityID(dto);
        return getServerDolphin().presentationModel(pmDescription.pmId(id), pmDescription.getName(), dto);
    }

    protected ServerPresentationModel createProxyPM(PMDescription pmDescription, String pmId) {
        List<Slot> proxySlots = createProxySlots(pmDescription);

        return getServerDolphin().presentationModel(pmId,
                                                    "Proxy:" + pmDescription.getName(),
                                                    new DTO(proxySlots));
    }


    protected void rebase(PMDescription pmDescription) {
        dirtyModels(pmDescription).forEach(ServerPresentationModel::rebase);
    }


    protected void reset(PMDescription pmDescription) {
        dirtyModels(pmDescription).forEach(ServerPresentationModel::reset);
    }


    protected List<DTO> dirtyDTOs(PMDescription pmDescription) {
        List<ServerPresentationModel> dirtyPMs = dirtyModels(pmDescription);

        return dirtyPMs.stream()
                       .map(pm -> pm.getAttributes().stream()
                                    .filter(BaseAttribute::isDirty)
                                    .map(att -> new Slot(att.getPropertyName(),
                                                         att.getValue(),
                                                         att.getQualifier()))
                                    .collect(Collectors.toList()))
                       .map(DTO::new)
                       .collect(Collectors.toList());
    }

    @Override
    public ServerPresentationModel get(PMDescription pmDescription, long id) {
        return get(pmDescription.pmId(id));
    }

    @Override
    public ServerPresentationModel get(String pmId) {
        return (ServerPresentationModel) DolphinMixin.super.get(pmId);
    }


    protected void translate(ServerPresentationModel proxyPM, Language language) {
        ResourceBundle        bundle     = getResourceBundle(proxyPM, language);
        List<ServerAttribute> attributes = proxyPM.getAttributes();
        attributes.stream()
                  .filter(att -> att.getTag().equals(Tag.LABEL))
                  .forEach(att -> {
                      String propertyName = att.getPropertyName();
                      att.setValue(translate(bundle, propertyName));
                  });
    }

    protected void translate(PresentationModelVeneer veneer, Language language) {
        translate((ServerPresentationModel) veneer.getPresentationModel(), language);
    }

    private void rebaseAll() {
        Collection<ServerPresentationModel> allPMs = getServerDolphin().getServerModelStore().listPresentationModels();
        allPMs.forEach(ServerPresentationModel::rebase);
    }


    private String translate(ResourceBundle bundle, String propertyName) {
        String labelText;
        if (bundle.containsKey(propertyName)) {
            labelText = new String(bundle.getString(propertyName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } else {
            labelText = propertyName.toLowerCase();
        }
        return labelText;
    }

    private ResourceBundle getResourceBundle(PresentationModel pm, Language language) {
        String type      = pm.getPresentationModelType();
        String className = type.contains(":") ? type.split(":")[1] : type;
        String baseName  = RESOURCEBUNDLE_DIRECTORY + className;
        try {
            return ResourceBundle.getBundle(baseName, language.getLocale());
        } catch (MissingResourceException e) {
            throw new IllegalStateException("resource bundle " + baseName + " not found");
        }
    }

    private List<ServerPresentationModel> dirtyModels(PMDescription pmDescription) {
        return getServerDolphin().findAllPresentationModelsByType(pmDescription.getName())
                                 .stream()
                                 .filter(BasePresentationModel::isDirty)
                                 .collect(Collectors.toList());
    }

    private List<Slot> createProxySlots(PMDescription pmDescription) {
        List<Slot> slots = new ArrayList<>();

        Arrays.stream(pmDescription.getAttributeDescriptions()).forEach(att -> {
            slots.add(new Slot(att.name(), getInitialValue(att)     , null                , Tag.VALUE));
            slots.add(new Slot(att.name(), att.name().toLowerCase() , att.labelQualifier(), Tag.LABEL));
            slots.add(new Slot(att.name(), att.getValueType().name(), null                , Tag.VALUE_TYPE));
            slots.add(new Slot(att.name(), false                    , null                , Tag.MANDATORY));
            slots.add(new Slot(att.name(), true                     , null                , AdditionalTag.VALID));
            slots.add(new Slot(att.name(), null                     , null                , AdditionalTag.VALIDATION_MESSAGE));
            slots.add(new Slot(att.name(), false                    , null                , AdditionalTag.READ_ONLY));
            slots.add(new Slot(att.name(), ""                       , null                , AdditionalTag.USER_FACING_STRING));
        });

        return slots;
    }

    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }


}
