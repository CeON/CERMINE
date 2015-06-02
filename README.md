Content ExtRactor and MINEr
===========================

[![Join the chat at https://gitter.im/CeON/CERMINE](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/CeON/CERMINE?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

CERMINE is a Java library and a web service ([cermine.ceon.pl](http://cermine.ceon.pl/)) for extracting metadata
and content from PDF files containing academic publications.
CERMINE is written in Java at [Centre for Open Science](http://ceon.pl/en/research/) at [Interdisciplinary Centre for Mathematical and Computational Modelling](http://www.icm.edu.pl/), [University of Warsaw](http://www.uw.edu.pl/).

The code is licensed under GNU Affero General Public License version 3.

How to cite CERMINE:

	Dominika Tkaczyk, Pawel Szostek, Piotr Jan Dendek, Mateusz Fedoryszak and Lukasz Bolikowski. 
	CERMINE - automatic extraction of metadata and references from scientific literature. 
	In Proceedings of the 11th IAPR International Workshop on Document Analysis Systems,
	pages 217-221, 2014.

DOI of CERMINE release 1.6: 

[![DOI](https://zenodo.org/badge/doi/10.5281/zenodo.17594.svg)](http://dx.doi.org/10.5281/zenodo.17594)

Using CERMINE
-------------

CERMINE can be used for:

  * extracting metadata, full text and parsed references from a PDF file,
  * extracting metadata from reference strings,
  * extracting metadata from affiliation strings.

**Maven dependency**

CERMINE can be used in Java projects by adding the following dependency and repository to the project's *pom.xml* file:

	<dependency>
		<groupId>pl.edu.icm.cermine</groupId>
		<artifactId>cermine-impl</artifactId>
		<version>1.6</version>
	</dependency>

	<repository>
		<id>icm</id>
		<name>ICM repository</name>
		<url>http://maven.icm.edu.pl/artifactory/repo</url>
	</repository>

To extract the content from a PDF file:

	PdfNLMContentExtractor extractor = new PdfNLMContentExtractor();
	InputStream inputStream = new FileInputStream("path/to/pdf/file");
	Element result = extractor.extractContent(inputStream);

To extract metadata from a reference string:

	CRFBibReferenceParser parser = CRFBibReferenceParser.getInstance();
	BibEntry reference = parser.parseBibReference(referenceText);

To extract metadata from an affiliation string:
	
	CRFAffiliationParser parser = new CRFAffiliationParser();
	Element affiliation = parser.parse(affiliationText);


**Executable JAR**

Alternatively, Maven can be used to build an executable JAR containing all needed classes and resources:

	$ cd CERMINE/cermine-impl
	$ mvn compile assembly:single

This will result in a file *cermine-impl-1.7-SNAPSHOT-jar-with-dependencies.jar* in *cermine-impl/target* directory. Executable JARs can also be downloaded from [the repository](http://maven.ceon.pl/artifactory/simple/kdd-shapshots/pl/edu/icm/cermine/cermine-impl/).

To extract the content from PDF files:

	$ java -cp target/cermine-impl-1.7-SNAPSHOT-jar-with-dependencies.jar pl.edu.icm.cermine.PdfNLMContentExtractor -path path/to/directory/with/pdfs/or/a/single/pdf

To extract metadata from a reference string:

	$ java -cp target/cermine-impl-1.7-SNAPSHOT-jar-with-dependencies.jar pl.edu.icm.cermine.bibref.CRFBibReferenceParser -reference "the text of the reference"

To extract metadata from an affiliation string:

	$ java -cp target/cermine-impl-1.7-SNAPSHOT-jar-with-dependencies.jar pl.edu.icm.cermine.metadata.affiliation.CRFAffiliationParser -affiliation "the text of the affiliation"


**REST service**

The third possibility is to use CERMINE's REST service with cURL tool.

To extract the content from a PDF file:

	$ curl -X POST --data-binary @article.pdf \
	  --header "Content-Type: application/binary"\
	  http://cermine.ceon.pl/extract.do

To extract metadata from a reference string:

	$ curl -X POST --data "reference=the text of the reference" \
	  http://cermine.ceon.pl/parse.do

To extract metadata from an affiliation string:

	$ curl -X POST --data "affiliation=the text of the affiliation" \
	  http://cermine.ceon.pl/parse.do

