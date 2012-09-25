package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.malletDemo;

import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AtRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AtCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseCountFeature;
import java.io.*;
import java.util.Arrays;
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.transformers.TrueVizToBxDocumentReader;

/**
 * Converts XML to Mallet format. The result of a conversion will be one txt file
 * with one instance per line. The used format in txt file is as follows:
 * [name]  [label]  [features...]
 * 
 * @author Krzysztof Werys
 */
class XMLToMalletFormatConverter {
    
    // place where converted file will be saved
    private String returnFilePath = "";
    
    private BufferedWriter bufferedWriter;
    private int indexOfZone = 0;
    
    public File convertFile(String XMLPath) throws IOException, TransformationException {
        
        indexOfZone = 0;
        File convertedFile = createNewFile();
        
        FileWriter fileWriter = new FileWriter(convertedFile.getAbsoluteFile());
        bufferedWriter = new BufferedWriter(fileWriter);
        
        extractTrainingDataAndSaveToFile(XMLPath);
        
        bufferedWriter.close();
        
        return convertedFile;
    }
    
    public File convertDirectory(String XMLFolder) throws IOException, TransformationException {
        indexOfZone = 0;
        File convertedFile = createNewFile();
        
        FileWriter fileWriter = new FileWriter(convertedFile.getAbsoluteFile());
        bufferedWriter = new BufferedWriter(fileWriter);
        
        File directory = new File(XMLFolder);
        for (File file : directory.listFiles(new XMLFilter())) {
            extractTrainingDataAndSaveToFile(file.getPath());
        }
        
        bufferedWriter.close();
        
        return convertedFile;
    }
    
    private File createNewFile() throws IOException {
        File file = new File(returnFilePath);
        if (!file.exists()){
            file.createNewFile();
        }
        else {
            file.delete();
            file.createNewFile();
        }
        return file;
    }
    
    private void extractTrainingDataAndSaveToFile(String XMLTrainingFile) throws TransformationException, IOException {
        File file = new File(XMLTrainingFile);
        System.out.println("Converting from XML to Mallet format: " + file.getName());
        // 1. construct vector of features builder
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
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
        
        String name = getPathOfXMLResource(XMLTrainingFile);
        InputStream is = XMLToMalletFormatConverter.class.getResourceAsStream(name);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument document = new BxDocument().setPages(reader.read(isr));
        
        writeLabelsAndFeaturesToFile(document, vectorBuilder);

    }
    
    private String getPathOfXMLResource(String XMLTrainingFile) {
        int i = XMLTrainingFile.indexOf("/pl/");
        return XMLTrainingFile.substring(i);
    }
    
    private void writeLabelsAndFeaturesToFile(BxDocument document, FeatureVectorBuilder vectorBuilder) throws IOException {
        
        for (BxPage page : document.getPages()) {
            
            for (BxZone zone : page.getZones()) {
                String trainingDataInput = indexOfZone + "_";
                
                BxZoneLabel zoneLabel = zone.getLabel().getGeneralLabel();
                
                if (zoneLabel == BxZoneLabel.GEN_METADATA){
                    FeatureVector featureVector = vectorBuilder.getFeatureVector(zone, page);
                    trainingDataInput += zoneLabel.toString();
                    
                    BxZoneLabel metadataLabel = zone.getLabel();
                    trainingDataInput += " " + metadataLabel.toString();

                    for (String feature : featureVector.getFeatureNames()) {
                        double featureValue = featureVector.getFeature(feature);
                        trainingDataInput += " " + feature + "=" + featureValue;
                    }

                    trainingDataInput +="\n";
                    bufferedWriter.write(trainingDataInput);
                    indexOfZone++;
                }
            }
        }
    }
    
    /**
     * Choose a path where to save a converted file.
     * @param path 
     */
    public void setReturnFilePath (String path) {
        this.returnFilePath = path;
    }
    
    class XMLFilter implements FileFilter {

        /** Test whether the string representation of the file 
         *   ends with the correct extension. Note that {@ref FileIterator}
         *   will only call this filter if the file is not a directory,
         *   so we do not need to test that it is a file.
         */
        @Override
        public boolean accept(File file) {
            return file.toString().endsWith(".xml");
        }
    }
}
