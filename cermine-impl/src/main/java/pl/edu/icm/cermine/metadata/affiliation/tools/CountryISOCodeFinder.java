package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Affiliation parser. Processes an instance of DocumentAffiliation by
 * generating and tagging its tokens.
 * 
 * @author Bartosz Tarnawski
 */
public class CountryISOCodeFinder {

    private static final String CODES_FILE = 
			"/pl/edu/icm/cermine/metadata/affiliation/country-codes.txt";

    private Map<String, String> countryCodes = new HashMap<String, String>();
    
    private void loadCountryCodes() throws TransformationException {
    	InputStream is = CountryISOCodeFinder.class.getResourceAsStream(CODES_FILE);
		if (is == null) {
			throw new TransformationException("Resource not found: " + CODES_FILE);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = in.readLine()) != null) {
				String countryName = line.substring(0, line.length()-3);
                String countryCode = line.substring(line.length()-2);
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

}
