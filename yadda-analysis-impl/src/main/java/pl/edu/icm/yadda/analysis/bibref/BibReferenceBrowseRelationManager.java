package pl.edu.icm.yadda.analysis.bibref;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.client.browser.iterator.FetcherIterator;
import pl.edu.icm.yadda.service2.browse.InvalidCookieException;
import pl.edu.icm.yadda.service2.browse.InvalidNameException;
import pl.edu.icm.yadda.service2.browse.NoSuchFieldInRelationException;
import pl.edu.icm.yadda.service2.browse.NoSuchRelationException;
import pl.edu.icm.yadda.service2.browse.facade.IBrowserFacade;
import pl.edu.icm.yadda.service2.browse.facade.Relation;
import pl.edu.icm.yadda.service2.browse.query.Condition;
import pl.edu.icm.yadda.service2.browse.query.Query;
import pl.edu.icm.yadda.service2.browse.query.SimpleClause;
import pl.edu.icm.yadda.service2.browse.relation.Field;
import pl.edu.icm.yadda.service2.browse.relation.RelationInfo;
import pl.edu.icm.yadda.service2.catalog.CountingIterator;

/**
 * Class stores and reads relations between bibliographic references and documents in browse.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibReferenceBrowseRelationManager implements BibReferenceRelationManager {

    private static final Logger log = LoggerFactory.getLogger(BibReferenceBrowseRelationManager.class);

    private static String REFS_RELATION_NAME = "bibReferences";
    private static String REFS_RELATION_SOURCE_FIELD = "sourceId";
    private static String REFS_RELATION_POSITION_FIELD = "position";
    private static String REFS_RELATION_TARGET_FIELD = "targetId";

    private static String DIRTY_RELATION_NAME = "documentDirty";
    private static String DIRTY_RELATION_SOURCE_FIELD = "sourceId";

    private static int PAGE_SIZE = 500;

    private IBrowserFacade browserFacade;

    private Map<String, SynchronizingObject> synchronObjects = new HashMap<String, SynchronizingObject>();


    public void setBrowserFacade(IBrowserFacade browserFacade) {
        this.browserFacade = browserFacade;
    }

    @Override
    public Map<Integer, String> getFromReferences(String sourceId) throws AnalysisException {
        try {
            Map<Integer, String> references = new HashMap<Integer, String>();
            Relation refsRelation = getRefsRelation();

            FetcherIterator fetcherIterator = new FetcherIterator(refsRelation.select(
                    Query.fields(REFS_RELATION_POSITION_FIELD, REFS_RELATION_TARGET_FIELD)
                    .where(getRefsSourceCondition(sourceId))), PAGE_SIZE);
            while (fetcherIterator.hasNext()) {
                Serializable[] record = fetcherIterator.next();
                references.put(Integer.valueOf(record[0].toString()), record[1].toString());
            }

            return references;
        } catch (InvalidCookieException ex) {
             throw new AnalysisException(ex);
        } catch (NoSuchFieldInRelationException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchRelationException ex) {
            throw new AnalysisException(ex);
        }
    }

    @Override
    public Map<String, Integer> getToReferences(String targetId) throws AnalysisException {
        try {
            Map<String, Integer> references = new HashMap<String, Integer>();
            Relation refsRelation = getRefsRelation();
            
            FetcherIterator fetcherIterator = new FetcherIterator(refsRelation.select(
                    Query.fields(REFS_RELATION_SOURCE_FIELD, REFS_RELATION_POSITION_FIELD)
                    .where(getRefsTargetCondition(targetId))), PAGE_SIZE);
            while (fetcherIterator.hasNext()) {
                Serializable[] record = fetcherIterator.next();
                references.put(record[0].toString(), Integer.valueOf(record[1].toString()));
            }

            return references;
        } catch (InvalidCookieException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchFieldInRelationException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchRelationException ex) {
            throw new AnalysisException(ex);
        }
    }

    @Override
    public void setReference(String sourceId, int position, String targetId) throws AnalysisException {
        log.debug("Relation manager: Setting a reference: " + sourceId + ", " + position+ ", " + targetId);
        Relation refsRelation = getRefsRelation();
        Relation dirtyRelation = getDirtyRelation();
        try {
            waitForSource(sourceId);
        } catch (InterruptedException ex) {
            throw new AnalysisException(ex);
        }
        try {
            refsRelation.addOrUpdate(getRefsSourcePosCondition(sourceId, position),
                    new Serializable[]{sourceId, position, targetId});
            dirtyRelation.addOrUpdate(getDirtySourceCondition(sourceId), new Serializable[]{sourceId});
        } catch (NoSuchFieldInRelationException ex) {
            notifyForSource(sourceId);
            throw new AnalysisException(ex);
        } catch (NoSuchRelationException ex) {
            notifyForSource(sourceId);
            throw new AnalysisException(ex);
        }
        notifyForSource(sourceId);
    }

    @Override
    public void removeReference(String sourceId, int position) throws AnalysisException {
        log.debug("Relation manager: Removing a reference: " + sourceId + ", " + position);
        Relation refsRelation = getRefsRelation();
        Relation dirtyRelation = getDirtyRelation();
        try {
            waitForSource(sourceId);
        } catch (InterruptedException ex) {
            throw new AnalysisException(ex);
        }
        try {
            refsRelation.delete(getRefsSourcePosCondition(sourceId, position));
            dirtyRelation.addOrUpdate(getDirtySourceCondition(sourceId), new Serializable[]{sourceId});
        } catch (NoSuchFieldInRelationException ex) {
            notifyForSource(sourceId);
            throw new AnalysisException(ex);
        } catch (NoSuchRelationException ex) {
            notifyForSource(sourceId);
            throw new AnalysisException(ex);
        }
        notifyForSource(sourceId);
    }

    @Override
    public Map<Integer, String> getFromReferencesAndLock(String sourceId) throws AnalysisException {
        try {
            Map<Integer, String> references = new HashMap<Integer, String>();
            Relation refsRelation = getRefsRelation();

            FetcherIterator fetcherIterator = new FetcherIterator(refsRelation.select(
                    Query.fields(REFS_RELATION_POSITION_FIELD, REFS_RELATION_TARGET_FIELD)
                    .where(getRefsSourceCondition(sourceId))), PAGE_SIZE);
            while (fetcherIterator.hasNext()) {
                Serializable[] record = fetcherIterator.next();
                references.put(Integer.valueOf(record[0].toString()), record[1].toString());
            }

            waitForSource(sourceId);

            return references;
        } catch (InvalidCookieException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchFieldInRelationException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchRelationException ex) {
            throw new AnalysisException(ex);
        } catch (InterruptedException ex) {
            throw new AnalysisException(ex);
        }
    }

    @Override
    public CountingIterator<String> getModifiedSourceIds() throws AnalysisException {
        try {
            Relation dirtyRelation = getDirtyRelation();
            
            final FetcherIterator fetcherIterator = new FetcherIterator(dirtyRelation.select(
                    Query.fields(DIRTY_RELATION_SOURCE_FIELD)
                    .upBy(DIRTY_RELATION_SOURCE_FIELD)), PAGE_SIZE);
            return new CountingIterator<String>() {

                @Override
                public boolean hasNext() {
                    return fetcherIterator.hasNext();
                }

                @Override
                public String next() {
                    Serializable[] record = fetcherIterator.next();
                    return record[0].toString();
                }

                @Override
                public void remove() {
                    fetcherIterator.remove();
                }

                @Override
                public int count() {
                    return fetcherIterator.count();
                }

            };
        } catch (InvalidCookieException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchFieldInRelationException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchRelationException ex) {
            throw new AnalysisException(ex);
        }
    }

    @Override
    public void setSourceDoneAndUnlock(String sourceId) throws AnalysisException {
        try {
            Relation dirtyRelation = getDirtyRelation();
            dirtyRelation.delete(getDirtySourceCondition(sourceId));
            notifyForSource(sourceId);
        } catch (NoSuchFieldInRelationException ex) {
            throw new AnalysisException(ex);
        } catch (NoSuchRelationException ex) {
            throw new AnalysisException(ex);
        }
    }


    private Condition getRefsSourceCondition(String sourceId) {
        return new SimpleClause(REFS_RELATION_SOURCE_FIELD, SimpleClause.Operator.EQUALS, sourceId);
    }

    private Condition getRefsSourcePosCondition(String sourceId, int position) {
        return getRefsSourceCondition(sourceId)
                .and(new SimpleClause(REFS_RELATION_POSITION_FIELD, SimpleClause.Operator.EQUALS, position));
    }

    private Condition getRefsTargetCondition(String targetId) {
        return new SimpleClause(REFS_RELATION_TARGET_FIELD, SimpleClause.Operator.EQUALS, targetId);
    }

    private Condition getDirtySourceCondition(String sourceId) {
        return new SimpleClause(DIRTY_RELATION_SOURCE_FIELD, SimpleClause.Operator.EQUALS, sourceId);
    }


    private synchronized Relation getRefsRelation() {
        try {
            return browserFacade.relation(REFS_RELATION_NAME);
        } catch (NoSuchRelationException ex) {
            Field sourceField = new Field(REFS_RELATION_SOURCE_FIELD, Field.Type.STRING, false, true);
            Field positionField = new Field(REFS_RELATION_POSITION_FIELD, Field.Type.INTEGER, false, false);
            Field targetField = new Field(REFS_RELATION_TARGET_FIELD, Field.Type.STRING, false, true);
            try {
                return browserFacade.create(
                        new RelationInfo(REFS_RELATION_NAME, sourceField, positionField, targetField));
            } catch (InvalidNameException ex1) {
                throw new IllegalArgumentException(ex1);
            }
        }
    }

    private synchronized Relation getDirtyRelation() {
        try {
            return browserFacade.relation(DIRTY_RELATION_NAME);
        } catch (NoSuchRelationException ex) {
            Field sourceField = new Field(DIRTY_RELATION_SOURCE_FIELD, Field.Type.STRING, false, true);
            try {
                return browserFacade.create(new RelationInfo(DIRTY_RELATION_NAME, sourceField));
            } catch (InvalidNameException ex1) {
                throw new IllegalArgumentException(ex1);
            }
        }
    }


    private void waitForSource(String sourceId) throws InterruptedException {
        SynchronizingObject sObject = getSynchronizingObject(sourceId);
        synchronized(sObject) {
            while (sObject.isBlocked()) {
                sObject.wait();
            }
            sObject.setBlocked(true);
        }
    }

    private void notifyForSource(String sourceId) {
        SynchronizingObject sObject = getSynchronizingObject(sourceId);
        synchronized(sObject) {
            sObject.setBlocked(false);
            sObject.notifyAll();
        }
    }

    private synchronized SynchronizingObject getSynchronizingObject(String sourceId) {
        if (synchronObjects.get(sourceId) == null) {
            synchronObjects.put(sourceId, new SynchronizingObject(sourceId));
        }
        return synchronObjects.get(sourceId);
    }


    private static class SynchronizingObject {

        private String name;
        private boolean blocked;

        public SynchronizingObject(String name) {
            this.name = name;
            this.blocked = false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }
    }
}
