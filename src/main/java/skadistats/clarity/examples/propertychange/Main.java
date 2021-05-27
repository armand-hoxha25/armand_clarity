package skadistats.clarity.examples.propertychange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.OnEntityPropertyChanged;
import skadistats.clarity.processor.entities.UsesEntities;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;

@UsesEntities
public class Main {

    private final Logger log = LoggerFactory.getLogger(Main.class.getPackage().getClass());

    @OnEntityPropertyChanged(classPattern = "CDOTA_Unit_Hero_.*", propertyPattern = "CBodyComponent.m_cellX")
    public void onEntityPropertyChanged(Context ctx, Entity e, FieldPath fp) {
        System.out.format(
                "%6d\t%s\t%s\t%s\n",
                ctx.getTick()/30,
                e.getDtClass().getDtName(),
                //e.getDtClass().getNameForFieldPath(fp),
                //e.getPropertyForFieldPath(fp),
                e.getProperty("CBodyComponent.m_vecX"),
                e.getProperty("CBodyComponent.m_vecY")
        );  
    }
   /* @OnEntityPropertyChanged(classPattern = "CDOTA_Unit_Hero_.*", propertyPattern = "CBodyComponent.m_cellY")
    public void onEntityPropertyChangedY(Context ctx, Entity e, FieldPath fp) {
        System.out.format(
                " %s\n",
                //ctx.getTick(),
                //e.getDtClass().getDtName(),
                //e.getDtClass().getNameForFieldPath(fp),
                e.getPropertyForFieldPath(fp)
        );  
    } */
    public void run(String[] args) throws Exception {
        long tStart = System.currentTimeMillis();
        new SimpleRunner(new MappedFileSource(args[0])).runWith(this);
        long tMatch = System.currentTimeMillis() - tStart;
        log.info("total time taken: {}s", (tMatch) / 1000.0);
    }

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

}
