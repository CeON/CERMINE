package pl.edu.icm.yadda.analysis.metadata.zoneclassification.malletDemo;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AtCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AtRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author Krzysztof Werys
 */
public class Utils {
    
    public static String getPathOfResource(String name) {
        URL url = Utils.class.getResource(name);
        return url.getPath();
    }
    
    public  List<BxZone> getMetadataZones(String XMLFile) throws TransformationException {
       
       BxDocument document = getBxDocumentFromXML(XMLFile);
       
       List<BxZone> metadataZones = new ArrayList<BxZone>();
       
       for (BxPage page : document.getPages()) {
            
            for (BxZone zone : page.getZones()) {
                
                BxZoneLabel zoneLabel = zone.getLabel().getGeneralLabel();
                
                if (zoneLabel == BxZoneLabel.GEN_METADATA){
                    metadataZones.add(zone);
                }
            }
        }
       
       return metadataZones;
    }
    
    public BxDocument getBxDocumentFromXML(String XMLFile) throws TransformationException {
        
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new SimpleFeatureVectorBuilder<BxZone, BxPage>(); 
        File file = new File(XMLFile);
        System.out.println("Converting from XML to Mallet format: " + file.getName());
        // 1. construct vector of features builder
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
                new ProportionsFeature(),
                new HeightFeature(),
                new WidthFeature(),
                new XPositionFeature(),
                new YPositionFeature(),
                new HeightRelativeFeature(),
                new WidthRelativeFeature(),
                new XPositionRelativeFeature(),
                new YPositionRelativeFeature(),
                new LineCountFeature(),
                new LineRelativeCountFeature(),
                new LineHeightMeanFeature(),
                new LineWidthMeanFeature(),
                new LineXPositionMeanFeature(),
                new LineXPositionDiffFeature(),
                new LineXWidthPositionDiffFeature(),
                new WordCountFeature(),
                new WordCountRelativeFeature(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new DigitCountFeature(),
                new DigitRelativeCountFeature(),
                new LetterCountFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseCountFeature(),
                new LowercaseRelativeCountFeature(),
                new UppercaseCountFeature(),
                new UppercaseRelativeCountFeature(),
                new UppercaseWordCountFeature(),
                new UppercaseWordRelativeCountFeature(),
                new AtCountFeature(),
                new AtRelativeCountFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
                new DotCountFeature(),
                new DotRelativeCountFeature(),
                new WordWidthMeanFeature()
                ));

        String name = getPathOfXMLResource(XMLFile);
        InputStream is = Utils.class.getResourceAsStream(name);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument document = new BxDocument().setPages(reader.read(isr));
        
        return document;
    }
    
    private String getPathOfXMLResource(String XMLTrainingFile) {
        int i = XMLTrainingFile.indexOf("/pl/");
        return XMLTrainingFile.substring(i);
    }
}
