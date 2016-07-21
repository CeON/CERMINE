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
package pl.edu.icm.cermine.evaluation.tools;

import java.io.File;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class NlmIterator implements Iterable<NlmPair> {

    private int curItemIdx = -1;

    private List<NlmPair> entries = null;

    private String originalNlmExtension;

    private final Comparator<File> alphabeticalOrder = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            String baseName1 = FilenameUtils.getBaseName(o1.getName());
            String extension1 = FilenameUtils.getExtension(o1.getName());
            String baseName2 = FilenameUtils.getBaseName(o2.getName());

            if (o1.getPath().equals(o2.getPath())) {
                return 0;
            }
            if (o1.getParent().equals(o2.getParent())
                    && baseName1.equals(baseName2)) {
                return extension1.equals(originalNlmExtension) ? -1 : 1;
            }
            return o1.getPath().compareTo(o2.getPath());
        }
    };

    public NlmIterator(String dirPath) {
        this(dirPath, "nxml", "metaxml");
    }

    public NlmIterator(String dirPath, final String originalNlmExtension, final String extractedNlmExtension) {
        this.originalNlmExtension = originalNlmExtension;

        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new IllegalArgumentException("provided input file must exist");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("provided input file must be a directory");
        }
        if (dir.list().length == 0) {
            throw new IllegalArgumentException("provided directory can't be empty");
        }

        List<File> interestingFiles = new ArrayList<File>(FileUtils.listFiles(dir, new String[]{originalNlmExtension, extractedNlmExtension}, true));

        if (interestingFiles.size() < 2) {
            throw new IllegalArgumentException("There are too few files in the given directory!");
        }
        Collections.sort(interestingFiles, alphabeticalOrder);

        entries = new ArrayList<NlmPair>();

        int i = 0;
        while (i < interestingFiles.size() - 1) {
            File file = interestingFiles.get(i);
            File nextFile = interestingFiles.get(i + 1);
            String name = file.getName();
            String nextName = nextFile.getName();
            if (FilenameUtils.getBaseName(name).equals(FilenameUtils.getBaseName(nextName))
                    && !FilenameUtils.getExtension(name).equals(FilenameUtils.getExtension(nextName))) {
                entries.add(new NlmPair(file, nextFile));
                i += 2;
            } else {
                i++;
            }
        }
    }

    public int size() {
        return entries.size();
    }

    @Override
    public Iterator<NlmPair> iterator() {
        return new Iterator<NlmPair>() {

            @Override
            public boolean hasNext() {
                return curItemIdx + 1 < entries.size();
            }

            @Override
            public NlmPair next() {
                ++curItemIdx;
                NlmPair actualItem = entries.get(curItemIdx);
                return actualItem;
            }

            @Override
            public void remove() {
                ++curItemIdx;
            }
        };
    }

}
