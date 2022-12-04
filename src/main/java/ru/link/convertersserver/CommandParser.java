package ru.link.convertersserver;

public class CommandParser {
    private static final String CMD_FREQ_RANGE = "p1";
    private static final String CMD_ATTDOWN = "p2";
    private static final String CMD_ATTUP = "p3";
    private static final String CMD_REFSOURCE = "p4";
    private static final String CMD_LOSOURCE = "p5";
    private static final String CMD_POWER = "p6";
    private static final String CMD_CONFIG = "add";

    private static final String FREQ_RANGE_1 = "L1: 10.7 - 12.75 GHz";
    private static final String FREQ_RANGE_2 = "L2: 14 - 14.5 GHz";
    private static final String FREQ_RANGE_3 = "L3: 17.3 - 20.2 GHz";
    private static final String FREQ_RANGE_4 = "L4: 22.55 - 23.25 GHz";
    private static final String FREQ_RANGE_5 = "L5: 27.5 - 30 GHz";

    private static final String CMD_FREQ_MASK = "FREQ:BAND %d";

    private static final String ATTDOWN = "POWER:ATTDOWN";

    private static final String ATTUP = "POWER:ATTUP";

    private static final String REFSOURCE = "FREQ:REFSOURCE";

    private static final String LOSOURCE = "FREQ:LOSOURCE";

    private static final String POWER = "POWER:RF";

    private static final String CONFIG = "SYSTEM:CONFIG:IPADDRESS";

    /**
     * @param cmd Команда, которую требуется преобразовать.
     * @return Возвращает преобразованную команду.
     */
    public static String parse(String cmd) {
        String[] cache = cmd.split(";");

        return switch (cache[0]) {
            case CMD_FREQ_RANGE -> parseFreqRange(cache[1]);
            case CMD_ATTDOWN -> getCmd(ATTDOWN, cache[1]);
            case CMD_ATTUP -> getCmd(ATTUP, cache[1]);
            case CMD_REFSOURCE -> getCmd(REFSOURCE, cache[1]);
            case CMD_LOSOURCE -> getCmd(LOSOURCE, cache[1]);
            case CMD_POWER -> getCmd(POWER, cache[1]);
            case CMD_CONFIG -> getCmd(CONFIG, cache[1]);
            default -> "";
        };
    }

    /**
     * @param freqRange Частотный диапазон
     * @return Возвращает команду, в соответствии с частотным диапазоном.
     */
    private static String parseFreqRange(String freqRange) {
        int rangeId = switch (freqRange) {
            case FREQ_RANGE_1 -> 1;
            case FREQ_RANGE_2 -> 2;
            case FREQ_RANGE_3 -> 3;
            case FREQ_RANGE_4 -> 4;
            case FREQ_RANGE_5 -> 5;
            default -> 6;
        };

        return String.format(CMD_FREQ_MASK, rangeId);
    }

    private static String getCmd(String cmd, String arg) {
        return cmd + " " + arg;
    }
}
