package myapp;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.opendolphin.binding.Converter;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.canton.Canton;
import myapp.presentationmodel.canton.CantonAtt;
import myapp.presentationmodel.canton.CantonCommands;
import myapp.presentationmodel.presentationstate.ApplicationState;
import myapp.presentationmodel.presentationstate.ApplicationStateAtt;
import myapp.util.AdditionalTag;
import myapp.util.Language;
import myapp.util.ViewMixin;
import myapp.util.veneer.AttributeFX;
import myapp.util.veneer.BooleanAttributeFX;

class RootPane extends GridPane implements ViewMixin, BasePmMixin {

    private static final String DIRTY_STYLE     = "dirty";
    private static final String INVALID_STYLE   = "invalid";
    private static final String MANDATORY_STYLE = "mandatory";

    private final ClientDolphin clientDolphin;

    private Label lblCanton;

    private Label lblMainTown;
    private TextField txtMainTown;

    private Label lblJoin;
    private TextField txtJoin;

    private Label lblCitizens;
    private TextField txtCitizens;

    private Label lblArea;
    private TextField txtArea;

    private Label lblLanguage;
    private TextField txtLanguage;

    private Button saveButton;
    private Button resetButton;
    private Button nextButton;
    private Button germanButton;
    private Button englishButton;

    private final Canton cantonProxy;


    private final ApplicationState ps;

    RootPane(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        ps = getApplicationState();
        cantonProxy = getCantonProxy();

        init();
    }

    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }

    @Override
    public void initializeSelf() {
        addStylesheetFiles("/fonts/fonts.css", "/myapp/myApp.css");
        getStyleClass().add("rootPane");
    }

    @Override
    public void initializeParts() {
        lblCanton = new Label();
        lblCanton.getStyleClass().add("heading");

        lblMainTown = new Label();
        txtMainTown = new TextField();

        lblJoin = new Label();
        txtJoin = new TextField();

        lblCitizens = new Label();
        txtCitizens = new TextField();

        lblArea = new Label();
        txtArea = new TextField();

        lblLanguage = new Label();
        txtLanguage = new TextField();

        saveButton    = new Button("Save");
        resetButton   = new Button("Reset");
        nextButton    = new Button("Next");
        germanButton  = new Button("German");
        englishButton = new Button("English");
    }

    @Override
    public void layoutParts() {
        ColumnConstraints grow = new ColumnConstraints();
        grow.setHgrow(Priority.ALWAYS);

        getColumnConstraints().setAll(new ColumnConstraints(), grow);
        setVgrow(lblCanton, Priority.ALWAYS);

        add(lblCanton, 0, 0, 5, 1);
        add(lblMainTown, 0, 1);
        add(txtMainTown, 1, 1, 4, 1);
        add(lblJoin, 0, 2);
        add(txtJoin, 1, 2, 4, 1);
        add(lblCitizens, 0, 3);
        add(txtCitizens, 1, 3, 4, 1);
        add(lblArea, 0, 4);
        add(txtArea, 1, 4, 4, 1);
        add(lblLanguage, 0, 5);
        add(txtLanguage, 1,5,4,1);
        add(new HBox(5, saveButton, resetButton, nextButton, germanButton, englishButton), 0, 6, 5, 1);
    }

    @Override
    public void setupEventHandlers() {

        ApplicationState ps = getApplicationState();
        saveButton.setOnAction(   $ -> clientDolphin.send(CantonCommands.SAVE));
        resetButton.setOnAction(  $ -> clientDolphin.send(CantonCommands.RESET));
        nextButton.setOnAction(   $ -> clientDolphin.send(CantonCommands.LOAD_SOME_CANTON));

        germanButton.setOnAction( $ -> ps.language.setValue(Language.GERMAN));
        englishButton.setOnAction($ -> ps.language.setValue(Language.ENGLISH));
    }

    @Override
    public void setupValueChangedListeners() {
        //cantonProxy.name.dirtyProperty().addListener((observable, oldValue, newValue)    -> updateStyle(txtJoin, DIRTY_STYLE, newValue));
        cantonProxy.mainTown.dirtyProperty().addListener((observable, oldValue, newValue)     -> updateStyle(txtCitizens, DIRTY_STYLE, newValue));
        cantonProxy.join.dirtyProperty().addListener((observable, oldValue, newValue) -> updateStyle(txtArea, DIRTY_STYLE, newValue));
        cantonProxy.citizens.dirtyProperty().addListener((observable, oldValue, newValue)    -> updateStyle(txtJoin, DIRTY_STYLE, newValue));
        cantonProxy.area.dirtyProperty().addListener((observable, oldValue, newValue)     -> updateStyle(txtCitizens, DIRTY_STYLE, newValue));
        cantonProxy.language.dirtyProperty().addListener((observable, oldValue, newValue) -> updateStyle(txtArea, DIRTY_STYLE, newValue));

        //cantonProxy.name.validProperty().addListener((observable, oldValue, newValue)    -> updateStyle(txtJoin, INVALID_STYLE, !newValue));
        cantonProxy.mainTown.validProperty().addListener((observable, oldValue, newValue)     -> updateStyle(txtCitizens, INVALID_STYLE, !newValue));
        cantonProxy.join.validProperty().addListener((observable, oldValue, newValue) -> updateStyle(txtArea, INVALID_STYLE, !newValue));
        cantonProxy.citizens.validProperty().addListener((observable, oldValue, newValue)    -> updateStyle(txtJoin, INVALID_STYLE, !newValue));
        cantonProxy.area.validProperty().addListener((observable, oldValue, newValue)     -> updateStyle(txtCitizens, INVALID_STYLE, !newValue));
        cantonProxy.language.validProperty().addListener((observable, oldValue, newValue) -> updateStyle(txtArea, INVALID_STYLE, !newValue));



        //cantonProxy.name.mandatoryProperty().addListener((observable, oldValue, newValue)    -> updateStyle(txtJoin, MANDATORY_STYLE, newValue));
        cantonProxy.mainTown.mandatoryProperty().addListener((observable, oldValue, newValue)     -> updateStyle(txtCitizens, MANDATORY_STYLE, newValue));
        cantonProxy.join.mandatoryProperty().addListener((observable, oldValue, newValue) -> updateStyle(txtArea, MANDATORY_STYLE, newValue));
        cantonProxy.citizens.mandatoryProperty().addListener((observable, oldValue, newValue)    -> updateStyle(txtJoin, MANDATORY_STYLE, newValue));
        cantonProxy.area.mandatoryProperty().addListener((observable, oldValue, newValue)     -> updateStyle(txtCitizens, MANDATORY_STYLE, newValue));
        cantonProxy.language.mandatoryProperty().addListener((observable, oldValue, newValue) -> updateStyle(txtArea, MANDATORY_STYLE, newValue));

    }

    @Override
    public void setupBindings() {
        //setupBindings_DolphinBased();
        setupBindings_VeneerBased();
    }

    private void setupBindings_DolphinBased() {

        ClientPresentationModel cantonProxyPM = clientDolphin.getAt(BasePmMixin.CANTON_PROXY_PM_ID);


        JFXBinder.bind(CantonAtt.NAME.name())
                 .of(cantonProxyPM)
                 .using(value -> value + ", " + cantonProxyPM.getAt(CantonAtt.NAME.name()).getValue())
                 .to("text")
                 .of(lblCanton);

        JFXBinder.bind(CantonAtt.MAINTOWN.name())
                 .of(cantonProxyPM)
                 .using(value -> cantonProxyPM.getAt(CantonAtt.MAINTOWN.name()).getValue() + ", " + value)
                 .to("text")
                 .of(lblCanton);

        JFXBinder.bind(CantonAtt.MAINTOWN.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(lblMainTown);
        JFXBinder.bind(CantonAtt.MAINTOWN.name()).of(cantonProxyPM).to("text").of(txtMainTown);
        JFXBinder.bind("text").of(txtMainTown).to(CantonAtt.MAINTOWN.name()).of(cantonProxyPM);

               JFXBinder.bind(CantonAtt.JOIN.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(lblJoin);
        JFXBinder.bind(CantonAtt.JOIN.name()).of(cantonProxyPM).to("text").of(txtJoin);
        Converter toIntConverter = value -> {
            try {
                int newValue = Integer.parseInt(value.toString());
                cantonProxyPM.getAt(CantonAtt.JOIN.name(), AdditionalTag.VALID).setValue(true);
                cantonProxyPM.getAt(CantonAtt.JOIN.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("OK");

                return newValue;
            } catch (NumberFormatException e) {
                cantonProxyPM.getAt(CantonAtt.JOIN.name(), AdditionalTag.VALID).setValue(false);
                cantonProxyPM.getAt(CantonAtt.JOIN.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("Not a number");
                return cantonProxyPM.getAt(CantonAtt.JOIN.name()).getValue();
            }
        };

        JFXBinder.bind("text").of(txtJoin).using(toIntConverter).to(CantonAtt.JOIN.name()).of(cantonProxyPM);

        JFXBinder.bind(CantonAtt.CITIZENS.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(lblCitizens);
        JFXBinder.bind(CantonAtt.CITIZENS.name()).of(cantonProxyPM).to("text").of(txtCitizens);
        Converter toIntConverter1 = value -> {
            try {
                int newValue = Integer.parseInt(value.toString());
                cantonProxyPM.getAt(CantonAtt.CITIZENS.name(), AdditionalTag.VALID).setValue(true);
                cantonProxyPM.getAt(CantonAtt.CITIZENS.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("OK");

                return newValue;
            } catch (NumberFormatException e) {
                cantonProxyPM.getAt(CantonAtt.CITIZENS.name(), AdditionalTag.VALID).setValue(false);
                cantonProxyPM.getAt(CantonAtt.CITIZENS.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("Not a number");
                return cantonProxyPM.getAt(CantonAtt.CITIZENS.name()).getValue();
            }
        };

        JFXBinder.bind("text").of(txtJoin).using(toIntConverter1).to(CantonAtt.CITIZENS.name()).of(cantonProxyPM);

        JFXBinder.bind(CantonAtt.AREA.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(lblArea);
        JFXBinder.bind(CantonAtt.AREA.name()).of(cantonProxyPM).to("text").of(txtArea);
        Converter toIntConverter2 = value -> {
            try {
                int newValue = Integer.parseInt(value.toString());
                cantonProxyPM.getAt(CantonAtt.AREA.name(), AdditionalTag.VALID).setValue(true);
                cantonProxyPM.getAt(CantonAtt.AREA.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("OK");

                return newValue;
            } catch (NumberFormatException e) {
                cantonProxyPM.getAt(CantonAtt.AREA.name(), AdditionalTag.VALID).setValue(false);
                cantonProxyPM.getAt(CantonAtt.AREA.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("Not a number");
                return cantonProxyPM.getAt(CantonAtt.AREA.name()).getValue();
            }
        };

        JFXBinder.bind("text").of(txtArea).using(toIntConverter2).to(CantonAtt.AREA.name()).of(cantonProxyPM);

        JFXBinder.bind(CantonAtt.LANGUAGE.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(lblLanguage);
        JFXBinder.bind(CantonAtt.LANGUAGE.name()).of(cantonProxyPM).to("text").of(txtLanguage);
        JFXBinder.bind("text").of(txtLanguage).to(CantonAtt.LANGUAGE.name()).of(cantonProxyPM);

        PresentationModel presentationStatePM = clientDolphin.getAt(BasePmMixin.APPLICATION_STATE_PM_ID);

        JFXBinder.bind(ApplicationStateAtt.LANGUAGE.name()).of(presentationStatePM).using(value -> value.equals(Language.GERMAN.name())).to("disable").of(germanButton);
        JFXBinder.bind(ApplicationStateAtt.LANGUAGE.name()).of(presentationStatePM).using(value -> value.equals(Language.ENGLISH.name())).to("disable").of(englishButton);
    }

    private void setupBindings_VeneerBased(){
          lblCanton.textProperty().bind(cantonProxy.name.valueProperty().concat(", ").concat(cantonProxy.mainTown.valueProperty()));

     /*   lblMainTown.textProperty().bind(cantonProxy.mainTown.labelProperty());
        txtMainTown.textProperty().bind(cantonProxy.mainTown.valueProperty());

        lblJoin.textProperty().bind(cantonProxy.join.labelProperty());
        txtJoin.textProperty().bind(cantonProxy.join.valueProperty().asString());

        lblCitizens.textProperty().bind(cantonProxy.citizens.labelProperty());
        txtCitizens.textProperty().bind(cantonProxy.citizens.valueProperty().asString());

        lblArea.textProperty().bind(cantonProxy.area.labelProperty());
        txtArea.textProperty().bind(cantonProxy.area.valueProperty().asString());

        lblLanguage.textProperty().bind(cantonProxy.language.labelProperty());
        txtLanguage.textProperty().bind(cantonProxy.language.valueProperty());*/

        setupBinding(lblMainTown, txtMainTown, cantonProxy.mainTown);
        setupBinding(lblJoin, txtJoin, cantonProxy.join);
        setupBinding(lblCitizens, txtCitizens, cantonProxy.citizens);
        setupBinding(lblArea, txtArea, cantonProxy.area);
        setupBinding(lblLanguage, txtLanguage, cantonProxy.language);

        germanButton.disableProperty().bind(Bindings.createBooleanBinding(() -> Language.GERMAN.equals(ps.language.getValue()), ps.language.valueProperty()));
        englishButton.disableProperty().bind(Bindings.createBooleanBinding(() -> Language.ENGLISH.equals(ps.language.getValue()), ps.language.valueProperty()));

        saveButton.disableProperty().bind(cantonProxy.dirtyProperty().not());
        resetButton.disableProperty().bind(cantonProxy.dirtyProperty().not());
    }

    private void setupBinding(Label label, TextField field, AttributeFX attribute) {
        setupBinding(label, attribute);

        field.textProperty().bindBidirectional(attribute.userFacingStringProperty());
        field.tooltipProperty().bind(Bindings.createObjectBinding(() -> new Tooltip(attribute.getValidationMessage()),
                                                                  attribute.validationMessageProperty()
                                                                 ));
    }

    private void setupBinding(Label label, CheckBox checkBox, BooleanAttributeFX attribute) {
        setupBinding(label, attribute);
        checkBox.selectedProperty().bindBidirectional(attribute.valueProperty());
    }

    private void setupBinding(Label label, AttributeFX attribute){
        label.textProperty().bind(Bindings.createStringBinding(() -> attribute.getLabel() + (attribute.isMandatory() ? " *" : "  "),
                                                               attribute.labelProperty(),
                                                               attribute.mandatoryProperty()));
    }

    private void updateStyle(Node node, String style, boolean value){
        if(value){
            node.getStyleClass().add(style);
        }
        else {
            node.getStyleClass().remove(style);
        }
    }
}
