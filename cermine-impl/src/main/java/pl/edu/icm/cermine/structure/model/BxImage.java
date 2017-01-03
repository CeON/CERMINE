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

package pl.edu.icm.cermine.structure.model;

import java.awt.image.BufferedImage;
import java.nio.file.Paths;

/**
 * Models an image.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxImage {

    private String prefix;
    
    private final String filename;
    
    private final BufferedImage image;

    public BxImage(String filename, BufferedImage image) {
        this.filename = filename;
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getFilename() {
        return filename;
    }
    
    public String getPath() {
        if (prefix == null) {
            return filename;
        }
        return Paths.get(prefix, filename).toString();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
}
