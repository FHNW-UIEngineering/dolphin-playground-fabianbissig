package myapp.presentationmodel.canton;

import org.opendolphin.core.BasePresentationModel;

import myapp.util.veneer.BooleanAttributeFX;
import myapp.util.veneer.IntegerAttributeFX;
import myapp.util.veneer.LongAttributeFX;
import myapp.util.veneer.StringAttributeFX;
import myapp.util.veneer.PresentationModelVeneer;


public class Canton extends PresentationModelVeneer {
    public Canton(BasePresentationModel pm) {
        super(pm);
    }

    public final LongAttributeFX    id      = new LongAttributeFX(getPresentationModel()   , CantonAtt.ID);
    public final StringAttributeFX  name    = new StringAttributeFX(getPresentationModel() , CantonAtt.NAME);
    public final StringAttributeFX mainTown     = new StringAttributeFX(getPresentationModel(), CantonAtt.MAINTOWN);
    public final IntegerAttributeFX join = new IntegerAttributeFX(getPresentationModel(), CantonAtt.JOIN);
    public final IntegerAttributeFX citizens = new IntegerAttributeFX(getPresentationModel(), CantonAtt.CITIZENS);
    public final IntegerAttributeFX area = new IntegerAttributeFX(getPresentationModel(), CantonAtt.AREA);
    public final StringAttributeFX language = new StringAttributeFX(getPresentationModel(), CantonAtt.LANGUAGE);

}
