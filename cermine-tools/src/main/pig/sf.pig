REGISTER ../../../target/cermine-tools-1.0-SNAPSHOT.jar

DEFINE RichSequenceFileLoader pl.edu.icm.cermine.pubmed.importers.RichSequenceFileLoader();
DEFINE DocumentComponentsProtoTupler pl.edu.icm.cermine.pubmed.importers.DocumentComponentsProtoTupler();
DEFINE PubmedXMLGenerator pl.edu.icm.cermine.pubmed.importers.PubmedXMLGenerator();
DEFINE FilenameGenerator pl.edu.icm.cermine.pubmed.PubmedXMLGenerator();

A = LOAD 'pubmed_data' USING RichSequenceFileLoader();
B = FOREACH A GENERATE FLATTEN(DocumentComponentsProtoTupler($1)) AS (filename, nlm, pdf);
C = FOREACH B GENERATE PubmedXMLGenerator() AS fields;

STORE C INTO '$output';
