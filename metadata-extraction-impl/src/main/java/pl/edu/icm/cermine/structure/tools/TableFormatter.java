package pl.edu.icm.cermine.structure.tools;

import java.io.PrintStream;
import java.util.Formatter;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author krusek
 */
public class TableFormatter {

    private Formatter formatter;

    public TableFormatter(PrintStream ps, Locale l) {
        formatter = new Formatter(ps, l);
    }

    public void startRow() {
        formatter.format(" |");
    }

    public void endRow() {
        formatter.format("\n");
    }

    public void data(String text, int size, Alignment padding) {
        if (text.length() >= size) {
            formatter.format(" %s |", text.substring(0, size));
        } else {
            formatter.format(" %s |", padding.align(text, size));
        }
    }

    public void dataFormat(String format, int size, Alignment padding, Object... args) {
        StringBuilder sb = new StringBuilder();
        Formatter tempFormatter = new Formatter(sb, formatter.locale());
        tempFormatter.format(format, args);
        data(sb.toString(), size, padding);
    }

    public void left(String text, int size) {
        data(text, size, Alignment.LEFT);
    }

    public void center(String text, int size) {
        data(text, size, Alignment.CENTER);
    }

    public void rightFormat(String format, int size, Object... args) {
        dataFormat(format, size, Alignment.RIGHT, args);
    }

    public void startSeparator() {
        formatter.format(" +");
    }

    public void separator(int size) {
        formatter.format("-%s-+", StringUtils.repeat("-", size));
    }

    public void endSeparator() {
        formatter.format("\n");
    }

    public static enum Alignment {
        LEFT {
            @Override
            public String align(String text, int size) {
                return StringUtils.rightPad(text, size);
            }
        },
        RIGHT {
            @Override
            public String align(String text, int size) {
                return StringUtils.leftPad(text, size);
            }
        },
        CENTER {
            @Override
            public String align(String text, int size) {
                return StringUtils.center(text, size);
            }
        };

        abstract public String align(String text, int size);
    }
}