/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.service;

import java.util.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
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
