package skadistats.clarity.examples.combatlog;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;
import skadistats.clarity.wire.common.proto.DotaUserMessages;

public class Main {

    private final Logger log = LoggerFactory.getLogger(Main.class.getPackage().getClass());

    private final PeriodFormatter GAMETIME_FORMATTER = new PeriodFormatterBuilder()
        .minimumPrintedDigits(2)
        .printZeroAlways()
        .appendHours()
        .appendLiteral(":")
        .appendMinutes()
        .appendLiteral(":")
        .appendSeconds()
        .appendLiteral(".")
        .appendMillis3Digit()
        .toFormatter();

    private String compileName(String attackerName, boolean isIllusion) {
        return attackerName != null ? attackerName + (isIllusion ? " (illusion)" : "") : "UNKNOWN";
    }

    private String getAttackerNameCompiled(CombatLogEntry cle) {
        return compileName(cle.getAttackerName(), cle.isAttackerIllusion());
    }

    private String getTargetNameCompiled(CombatLogEntry cle) {
        return compileName(cle.getTargetName(), cle.isTargetIllusion());
    }

    @OnCombatLogEntry
    public void onCombatLogEntry(CombatLogEntry cle) {
        String time =GAMETIME_FORMATTER.print(Duration.millis((int) (1000.0f * cle.getTimestamp())).toPeriod());
        switch (cle.getType()) {
            case DOTA_COMBATLOG_DAMAGE:
            if (getAttackerNameCompiled(cle).contains("npc_dota_hero") && 
            getTargetNameCompiled(cle).contains("npc_dota_hero")){
                log.info("{}\tDAMAGE\t{}\t{}\t{}\t{}\t{}",
                    time,
                    getAttackerNameCompiled(cle),
                    getTargetNameCompiled(cle),
                    cle.getInflictorName() != null ? String.format("%s", cle.getInflictorName()) : "",
                    cle.getValue(),
                    cle.getHealth() != 0 ? String.format(" (%s\t%s)", cle.getHealth() + cle.getValue(), cle.getHealth()) : ""
                );}
                break;
            // case DOTA_COMBATLOG_HEAL:
            //     log.info("{} {}'s {} heals {} for {} health ({}->{})",
            //         time,
            //         getAttackerNameCompiled(cle),
            //         cle.getInflictorName(),
            //         getTargetNameCompiled(cle),
            //         cle.getValue(),
            //         cle.getHealth() - cle.getValue(),
            //         cle.getHealth()
            //     );
            //     break;
            case DOTA_COMBATLOG_MODIFIER_ADD:
                if (getAttackerNameCompiled(cle).contains("npc_dota_hero") && 
                getTargetNameCompiled(cle).contains("npc_dota_hero")){
                    log.info("{}\tBUFF\t{}\t{}\t{}",
                        time,
                        getAttackerNameCompiled(cle),
                        getTargetNameCompiled(cle),
                        cle.getInflictorName()                    
                    );}
                break;
            // case DOTA_COMBATLOG_MODIFIER_REMOVE:
            //     log.info("{} {} loses {} buff/debuff",
            //         time,
            //         getTargetNameCompiled(cle),
            //         cle.getInflictorName()
            //     );
            //     break;
            // case DOTA_COMBATLOG_DEATH:
            //     log.info("{} {} is killed by {}",
            //         time,
            //         getTargetNameCompiled(cle),
            //         getAttackerNameCompiled(cle)
            //     );
            //     break;
            case DOTA_COMBATLOG_ABILITY:
            if (getAttackerNameCompiled(cle).contains("npc_dota_hero")){
                log.info("{}\tABILITY\t{}\t{}\t{}",
                    time,
                    getAttackerNameCompiled(cle),
                    cle.getTargetName() != null ? getTargetNameCompiled(cle) : "",
                    cle.getInflictorName(),
                    cle.isAbilityToggleOn() || cle.isAbilityToggleOff() ? "toggles" : "casts"
                    // cle.getAbilityLevel(),
                    // cle.isAbilityToggleOn() ? " on" : cle.isAbilityToggleOff() ? " off" : "",
                );
            }
                break;
            case DOTA_COMBATLOG_ITEM:
                log.info("{}\tITEM\t{}\t{}",
                    time,
                    getAttackerNameCompiled(cle),
                    cle.getInflictorName()
                );
                break;
            // case DOTA_COMBATLOG_GOLD:
            //     log.info("{} {} {} {} gold",
            //         time,
            //         getTargetNameCompiled(cle),
            //         cle.getValue() < 0 ? "looses" : "receives",
            //         Math.abs(cle.getValue())
            //     );
            //     break;
            // case DOTA_COMBATLOG_GAME_STATE:
            //     log.info("{} game state is now {}",
            //         time,
            //         cle.getValue()
            //     );
            //     break;
            // case DOTA_COMBATLOG_XP:
            //     log.info("{} {} gains {} XP",
            //         time,
            //         getTargetNameCompiled(cle),
            //         cle.getValue()
            //     );
            //     break;
            // case DOTA_COMBATLOG_PURCHASE:
            //     log.info("{} {} buys item {}",
            //         time,
            //         getTargetNameCompiled(cle),
            //         cle.getValueName()
            //     );
            // //     break;
            // case DOTA_COMBATLOG_BUYBACK:
            //     log.info("{} player in slot {} has bought back",
            //         time,
            //         cle.getValue()
            //     );
            //     break;

            default:
                DotaUserMessages.DOTA_COMBATLOG_TYPES type = cle.getType();
                // log.info("\n{}({}): {}\n", type.name(), type.ordinal(), cle);
                // log.info("");
                break;

        }
    }

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
