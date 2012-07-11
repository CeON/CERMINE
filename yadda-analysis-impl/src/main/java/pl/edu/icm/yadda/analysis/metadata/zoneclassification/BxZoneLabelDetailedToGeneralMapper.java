package pl.edu.icm.yadda.analysis.metadata.zoneclassification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pl.edu.icm.yadda.analysis.textr.model.BxZoneGeneralLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

public class BxZoneLabelDetailedToGeneralMapper {
	public static class BxZoneLabelMappingError extends Exception {
        private static final long serialVersionUID = 68027091130335L;
        BxZoneLabelMappingError() {
            super();
        }
        BxZoneLabelMappingError(String msgKey) {
            super(msgKey);
        }
    };

	Map<BxZoneGeneralLabel, BxZoneLabel[]> mapping;
	public BxZoneLabelDetailedToGeneralMapper() {
		mapping = new HashMap<BxZoneGeneralLabel, BxZoneLabel[]>();
		mapping.put(BxZoneGeneralLabel.METADATA, new BxZoneLabel[] {
				BxZoneLabel.ABSTRACT,
				BxZoneLabel.AFFILIATION,
				BxZoneLabel.AUTHOR,
				BxZoneLabel.BIB_INFO,
				BxZoneLabel.COPYRIGHT,
				BxZoneLabel.CORRESPONDENCE,
				BxZoneLabel.DATES,
				BxZoneLabel.EDITOR,
				BxZoneLabel.KEYWORDS,
				BxZoneLabel.REFERENCES,
				BxZoneLabel.TITLE,
				BxZoneLabel.TYPE
		});

		mapping.put(BxZoneGeneralLabel.BODY, new BxZoneLabel[] {
				BxZoneLabel.BODY,
				BxZoneLabel.EQUATION,
				BxZoneLabel.EQUATION_LABEL,
				BxZoneLabel.FIGURE,
				BxZoneLabel.FIGURE_CAPTION,
				BxZoneLabel.TABLE,
				BxZoneLabel.TABLE_CAPTION
		});

		mapping.put(BxZoneGeneralLabel.OTHER, new BxZoneLabel[] {
				BxZoneLabel.FOOTER,
				BxZoneLabel.HEADER,
				BxZoneLabel.PAGE_NUMBER,
				BxZoneLabel.UNKNOWN,
		});

		mapping.put(BxZoneGeneralLabel.REFERENCES, new BxZoneLabel[] {
				BxZoneLabel.REFERENCES
		});
	}
	
	public BxZoneGeneralLabel map(BxZoneLabel mapped) throws BxZoneLabelMappingError {
		BxZoneGeneralLabel ret = null;
		for(Entry<BxZoneGeneralLabel, BxZoneLabel[]> entry: mapping.entrySet()) {
			if(Arrays.asList(entry.getValue()).contains(mapped)) {
				ret = entry.getKey();
				break;
			}
		}
		if(ret == null)
			throw new BxZoneLabelMappingError("Mapping for " + mapped + " not found!");
		return ret;
	}
};
