package skadistats.clarity.examples.two.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.two.processor.reader.OnFileInfoOffset;
import skadistats.clarity.two.processor.runner.Context;
import skadistats.clarity.two.processor.runner.Runner;
import skadistats.clarity.two.processor.stringtables.UseStringTable;

import java.io.FileInputStream;

public class Main {

    @UseStringTable("CombatLogNames")
    public static class Test {

        @OnFileInfoOffset
        public void onFileInfoOffset(Context ctx, int offs) {
            System.out.println("fileinfo is at " + offs);
        }
    }

    public static void main(String[] args) throws Exception {

        long tStart = System.currentTimeMillis();

        Logger log = LoggerFactory.getLogger("simple");

        new Runner().runWith(new FileInputStream(args[0]), new Test());

        long tMatch = System.currentTimeMillis() - tStart;
        log.info("total time taken: {}s", (tMatch) / 1000.0);
        
    }

}