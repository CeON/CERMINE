/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
@Service
public class TaskManager {

    private static final Random random = new Random();
    Map<Long, ExtractionTask> tasks = new HashMap<Long, ExtractionTask>();

    protected long newId() {
        long id = random.nextLong();
        while (id < 0 || tasks.containsKey(id)) {
            id = random.nextLong();
        }
        return id;
    }

    public long registerTask(ExtractionTask task) {
        if (task.getId() == 0) {
            task.setId(newId());
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public void deleteFinishedBefore(Date before) {
        List<Long> toRemove = new ArrayList<Long>();

        for (Map.Entry<Long, ExtractionTask> entry : tasks.entrySet()) {
            if (entry.getValue().getStatus() == ExtractionTask.TaskStatus.FINISHED && 
                    entry.getValue().getResult().getProcessingEnd().before(before)) {
                toRemove.add(entry.getKey());
            }
        }
        for (Long id : toRemove) {
            tasks.remove(id);
        }
    }
}
