package skadistats.clarity.examples.lifestate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.event.Insert;
import skadistats.clarity.model.Entity;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;

public class Main {

    private final Logger log = LoggerFactory.getLogger(Main.class.getPackage().getClass());

    @Insert
    private Context ctx;

    @OnEntitySpawned
    public void onSpawned(Entity e) {
        // System.out.printf("%06d: %s at index %d has spawned\n", ctx.getTick(), e.getDtClass().getDtName(), e.getIndex());
    }

    @OnEntityDied
    public void onDied(Entity e) {
        if (e.getDtClass().getDtName().contains("Hero")){
            int out=e.getProperty("m_iCurrentXP");
            int xp = Integer.valueOf(out);
            if (xp>0){
            
        System.out.printf("%06d\t%s\t%s\n", ctx.getTick()/30, e.getDtClass().getDtName(), e.getProperty("m_iTeamNum"));
    }}}

    public void run(String[] args) throws Exception {
        long tStart = System.currentTimeMillis();
        SimpleRunner r = null;
        try {
            r = new SimpleRunner(new MappedFileSource(args[0])).runWith(this);
        } finally {
            long tMatch = System.currentTimeMillis() - tStart;
            log.info("total time taken: {}s", (tMatch) / 1000.0);
            if (r != null) {
                r.getSource().close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

}
