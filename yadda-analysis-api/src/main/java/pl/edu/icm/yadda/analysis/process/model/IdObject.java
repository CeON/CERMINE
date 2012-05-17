package pl.edu.icm.yadda.analysis.process.model;

/**
 * Id object encapsulating an object.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IdObject<T> {

    private String id;
    private T object;

    public IdObject(String id, T object) {
        this.id = id;
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

}
