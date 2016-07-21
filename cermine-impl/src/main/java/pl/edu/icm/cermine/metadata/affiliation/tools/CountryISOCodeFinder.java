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
package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CountryISOCodeFinder {

    private static final String CODES_FILE
            = "/pl/edu/icm/cermine/metadata/affiliation/country-codes.txt";

    private final Map<String, String> countryCodes = new HashMap<String, String>();

    private void loadCountryCodes() throws TransformationException {
        InputStream is = CountryISOCodeFinder.class.getResourceAsStream(CODES_FILE);
        if (is == null) {
            throw new TransformationException("Resource not found: " + CODES_FILE);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String countryName = line.substring(0, line.length() - 3);
                String countryCode = line.substring(line.length() - 2);
                countryCodes.put(countryName, countryCode.toUpperCase());
                countryCodes.put(countryName.replaceAll("[^a-zA-Z]", ""), countryCode.toUpperCase());
            }
        } catch (IOException ex) {
            throw new TransformationException(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                throw new TransformationException(ex);
            }
        }
    }

    public CountryISOCodeFinder() throws TransformationException {
        loadCountryCodes();
    }

    public String getCountryISOCode(String country) {
        if (countryCodes.get(country) != null) {
            return countryCodes.get(country.toLowerCase());
        } else {
            return countryCodes.get(country.toLowerCase().replaceAll("[^a-zA-Z]", ""));
        }
    }

    public Set<String> getCountries() {
        return countryCodes.keySet();
    }

}
