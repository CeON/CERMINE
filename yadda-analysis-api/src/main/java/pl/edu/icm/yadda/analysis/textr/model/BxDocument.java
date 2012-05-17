package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a document containing pages.
 */
public final class BxDocument implements Serializable {

    private static final long serialVersionUID = -4826783896245709986L;

    /** list of document's pages */
    private final List<BxPage> pages = new ArrayList<BxPage>();

    public List<BxPage> getPages() {
        return pages;
    }

    public BxDocument setPages(Collection<BxPage> pages) {
        if (pages == null) {
            throw new NullPointerException();
        }
        this.pages.clear();
        this.pages.addAll(pages);
        return this;
    }

    public BxDocument addPage(BxPage page) {
        if (page == null) {
            throw new NullPointerException();
        }
        this.pages.add(page);
        return this;
    }

    public String toText() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BxPage w : pages) {
            if (!first) {
                sb.append("\n");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
}
