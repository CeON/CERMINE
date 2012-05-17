package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Zone classifier node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ZoneClassifierNode implements IProcessingNode<EnrichedPayload<BxDocument>, EnrichedPayload<BxDocument>> {

    private ZoneClassifier zoneClassifier;

    @Override
    public EnrichedPayload<BxDocument> process(EnrichedPayload<BxDocument> input, ProcessContext ctx) throws Exception {
        zoneClassifier.classifyZones(input.getObject());
        return input;
    }

    public void setZoneClassifier(ZoneClassifier zoneClassifier) {
        this.zoneClassifier = zoneClassifier;
    }

}
