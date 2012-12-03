/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
@Service
@Scope(value = "session")
public class TaskManagerImpl implements TaskManager {

    private static final Random random = new Random();
    Map<Long, ExtractionTask> tasks = new HashMap<Long, ExtractionTask>();

    protected long newId() {
        long id = random.nextLong();
        while (id < 0 || tasks.containsKey(id)) {
            id = random.nextLong();
        }
        return id;
    }

    @Override
    public long registerTask(ExtractionTask task) {
        if (task.getId() == 0) {
            task.setId(newId());
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public ExtractionTask getTask(long id) throws NoSuchTaskException {
        ExtractionTask t = tasks.get(id);
        if (t == null) {
            throw new NoSuchTaskException(id);
        }
        return t;
    }

    @Override
    public List<ExtractionTask> taskList() {
        List<ExtractionTask> res = new ArrayList<ExtractionTask>();
        for (Map.Entry<Long, ExtractionTask> entries : tasks.entrySet()) {
            res.add(entries.getValue());
        }
        Collections.sort(res, new Comparator<ExtractionTask>() {
            @Override
            public int compare(ExtractionTask t, ExtractionTask t1) {
                return t.getCreationDate().compareTo(t1.getCreationDate());
            }
        });
        return res;
    }

    public void deleteFinishedBefore(Date before) {
        List<Long> toRemove = new ArrayList<Long>();

        for (Map.Entry<Long, ExtractionTask> entry : tasks.entrySet()) {
            if (entry.getValue().getStatus() == ExtractionTask.TaskStatus.FINISHED
                    && entry.getValue().getResult().getProcessingEnd().before(before)) {
                toRemove.add(entry.getKey());
            }
        }
        for (Long id : toRemove) {
            tasks.remove(id);
        }
    }

    @Override
    public String getProperFilename(String filename) {
        String fbase = filename;
        if (fbase == null || fbase.isEmpty()) {
            fbase = "input.pdf";
        }
        boolean ok = true;
        for (Map.Entry<Long, ExtractionTask> entry : tasks.entrySet()) {
            if (fbase.equals(entry.getValue().getFileName())) {
                ok = false;
                break;
            }
        }
        int suffix = 1;
        String fname = fbase;
        while (!ok) {
            ok = true;
            fname = fbase + "#" + suffix;
            for (Map.Entry<Long, ExtractionTask> entry : tasks.entrySet()) {
                if (fname.equals(entry.getValue().getFileName())) {
                    ok = false;
                    break;
                }
            }
            suffix++;
        }
        return fname;
    }
}
