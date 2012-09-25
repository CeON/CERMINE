package pl.edu.icm.coansys.metaextr.metadata.zoneclassification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneGeneralLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

@Deprecated
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
		mapping.put(BxZoneGeneralLabel.ABSTRACT, new BxZoneLabel[] {
				BxZoneLabel.MET_ABSTRACT
		});
		mapping.put(BxZoneGeneralLabel.METADATA, new BxZoneLabel[] {
				BxZoneLabel.MET_AFFILIATION,
				BxZoneLabel.MET_AUTHOR,
				BxZoneLabel.MET_BIB_INFO,
				BxZoneLabel.OTH_COPYRIGHT,
				BxZoneLabel.MET_CORRESPONDENCE,
				BxZoneLabel.MET_DATES,
				BxZoneLabel.MET_EDITOR,
				BxZoneLabel.MET_KEYWORDS,
				BxZoneLabel.MET_TITLE,
				BxZoneLabel.MET_TYPE
		});

		mapping.put(BxZoneGeneralLabel.BODY, new BxZoneLabel[] {
				BxZoneLabel.GEN_BODY
		});

		mapping.put(BxZoneGeneralLabel.OTHER, new BxZoneLabel[] {
				BxZoneLabel.BODY_EQUATION,
				BxZoneLabel.BODY_EQUATION_LABEL,
				BxZoneLabel.BODY_FIGURE,
				BxZoneLabel.BODY_FIGURE_CAPTION,
				BxZoneLabel.BODY_TABLE,
				BxZoneLabel.BODY_TABLE_CAPTION,
				BxZoneLabel.OTH_FOOTER,
				BxZoneLabel.OTH_HEADER,
				BxZoneLabel.OTH_PAGE_NUMBER,
				BxZoneLabel.OTH_UNKNOWN
		});

		mapping.put(BxZoneGeneralLabel.REFERENCES, new BxZoneLabel[] {
				BxZoneLabel.GEN_REFERENCES
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
