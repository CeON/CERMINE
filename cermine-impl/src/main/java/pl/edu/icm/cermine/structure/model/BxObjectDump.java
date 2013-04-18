package pl.edu.icm.cermine.structure.model;

/**
 * Debugging class aiming to dump content of a document to the standard output in a form of a tree of objects.
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl) @date 05.2012
 *
 */
public class BxObjectDump {

    private static Integer indentWidth = 4;

    public String dump(BxDocument doc) {
        return dump(doc, -1, 0, false, 10);
    }

    public String dump(BxDocument doc, Integer levels, Integer indent, Boolean dumpReference, Integer contentLength) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < indent * indentWidth; ++i) {
            stringBuilder.append(" ");
        }
        stringBuilder.append("BxDocument");
        if (dumpReference) {
            stringBuilder.append(" @").append(cutOutReference(doc.toString()));
        }

        stringBuilder.append("\n");

        if (levels != 0) {
            --levels;
            for (BxPage page : doc.getPages()) {
                stringBuilder.append(dump(page, levels, indent + 1, dumpReference, contentLength));
            }
        }
        return stringBuilder.toString();
    }

    public String dump(BxPage page, Integer levels, Integer indent, Boolean dumpReference, Integer contentLength) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < indent * indentWidth; ++i) {
            stringBuilder.append(" ");
        }
        stringBuilder.append("BxPage(");
        if (page.getId() != null) {
            stringBuilder.append(page.getId());
        } else {
            stringBuilder.append("");
        }
        stringBuilder.append(")");
        if (dumpReference) {
            stringBuilder.append(" @").append(cutOutReference(page.toString()));
        }
        stringBuilder.append(dumpContentBriefly(page, contentLength)).append("\n");

        if (levels != 0) {
            --levels;
            for (BxZone zone : page.getZones()) {
                stringBuilder.append(dump(zone, levels, indent + 1, dumpReference, contentLength));
            }
        }
        return stringBuilder.toString();
    }

    private String brief(String text) {
        if (text.length() < 100) {
            return text;
        } else {
            return text.substring(0, 100) + "...";
        }
    }

    private <A extends Printable> String dumpContentBriefly(A obj, Integer contentLength) {
        if (contentLength > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" [").append(brief(obj.toText())).append("]");
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    public String dump(BxZone zone, Integer levels, Integer indent, Boolean dumpReference, Integer contentLength) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < indent * indentWidth; ++i) {
            stringBuilder.append(" ");
        }
        stringBuilder.append("BxZone(");
        if (zone.getId() != null) {
            stringBuilder.append(zone.getId());
        } else {
            stringBuilder.append("");
        }
        stringBuilder.append(")");
        if (zone.getLabel() != null) {
            stringBuilder.append("[").append(zone.getLabel()).append("]");
        }
        if (dumpReference) {
            stringBuilder.append("@").append(cutOutReference(zone.toString()));
        }
        stringBuilder.append(dumpContentBriefly(zone, contentLength)).append("\n");

        if (levels != 0) {
            --levels;
            for (BxLine line : zone.getLines()) {
                stringBuilder.append(dump(line, levels, indent + 1, dumpReference, contentLength));
            }
        }
        return stringBuilder.toString();
    }

    public String dump(BxLine line, Integer levels, Integer indent, Boolean dumpReference, Integer contentLength) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < indent * indentWidth; ++i) {
            stringBuilder.append(" ");
        }
        stringBuilder.append("BxLine(");
        if (line.getId() != null) {
            stringBuilder.append(line.getId());
        } else {
            stringBuilder.append("");
        }
        stringBuilder.append(")");
        if (dumpReference) {
            stringBuilder.append(" @").append(cutOutReference(line.toString()));
        }
        stringBuilder.append(dumpContentBriefly(line, contentLength));
        stringBuilder.append("\n");

        if (levels != 0) {
            --levels;
            for (BxWord word : line.getWords()) {
                stringBuilder.append(dump(word, levels, indent + 1, dumpReference, contentLength));
            }
        }
        return stringBuilder.toString();
    }

    public String dump(BxWord word, Integer levels, Integer indent, Boolean dumpReference, Integer contentLength) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < indent * indentWidth; ++i) {
            stringBuilder.append(" ");
        }
        stringBuilder.append("BxWord(");
        if (word.getId() != null) {
            stringBuilder.append(word.getId());
        } else {
            stringBuilder.append("");
        }
        stringBuilder.append(")");
        if (dumpReference) {
            stringBuilder.append("@").append(cutOutReference(word.toString()));
        }
        stringBuilder.append(dumpContentBriefly(word, contentLength)).append("\n");
        return stringBuilder.toString();
    }

    protected String cutOutReference(String str) {
        String[] parts = str.split("@");
        String addr = parts[parts.length - 1];
        return addr;
    }
}
