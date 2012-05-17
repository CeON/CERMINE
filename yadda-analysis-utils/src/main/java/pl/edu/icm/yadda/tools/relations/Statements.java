package pl.edu.icm.yadda.tools.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Statements concerning a given subject.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public class Statements {
    protected String subject;

    protected final List<PredicateAndObject> continuations = new ArrayList<PredicateAndObject>();

    public Statements() {
    }

    public Statements(String subject) {
        setSubject(subject);
    }

    public Statements(String subject, Collection<PredicateAndObject> continuations) {
        setSubject(subject);
        setContinuations(continuations);
    }

    public Statements(String subject, String predicate, String object) {
        setSubject(subject);
        setContinuations(Arrays.asList(new PredicateAndObject(predicate, object)));
    }

    public String getSubject() {
        return subject;
    }

    public Statements setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public List<PredicateAndObject> getContinuations() {
        return continuations;
    }

    public Statements setContinuations(Collection<PredicateAndObject> continuations) {
        this.continuations.clear();
        if (continuations != null)
            this.continuations.addAll(continuations);
        return this;
    }

    public Statements addContinuation(PredicateAndObject continuation) {
        this.continuations.add(continuation);
        return this;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for(PredicateAndObject pao : continuations)
    		sb.append(subject+"\t"+pao+"\n");
        return sb.toString();
    }
    
    public static class PredicateAndObject {
        protected String predicate;
        protected String object;

        @Override
        public String toString() {
            return "{[" + predicate + "]" + "[" + object + "]}";
        }

        public PredicateAndObject() {
        }

        public PredicateAndObject(String predicate, String object) {
            setPredicate(predicate);
            setObject(object);
        }

        public String getPredicate() {
            return predicate;
        }

        public PredicateAndObject setPredicate(String predicate) {
            this.predicate = predicate;
            return this;
        }

        public String getObject() {
            return object;
        }

        public PredicateAndObject setObject(String object) {
            this.object = object;
            return this;
        }
    }
}
