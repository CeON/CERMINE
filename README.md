Content ExtRactor and MINEr
===========================

[![Join the chat at https://gitter.im/CeON/CERMINE](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/CeON/CERMINE?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

CERMINE is a Java library and a web service ([cermine.ceon.pl](http://cermine.ceon.pl/)) for extracting metadata
and content from PDF files containing academic publications.
CERMINE is written in Java at [Centre for Open Science](http://ceon.pl/en/research/) at [Interdisciplinary Centre for Mathematical and Computational Modelling](http://www.icm.edu.pl/), [University of Warsaw](http://www.uw.edu.pl/).

The code is licensed under GNU Affero General Public License version 3.

How to cite CERMINE:

	Dominika Tkaczyk, Pawel Szostek, Mateusz Fedoryszak, Piotr Jan Dendek and Lukasz Bolikowski. 
	CERMINE: automatic extraction of structured metadata from scientific literature. 
	In International Journal on Document Analysis and Recognition (IJDAR), 2015, 
	vol. 18, no. 4, pp. 317-335, doi: 10.1007/s10032-015-0249-8.

DOI of CERMINE release 1.8: 

[![DOI](https://zenodo.org/badge/doi/10.5281/zenodo.45063.svg)](http://dx.doi.org/10.5281/zenodo.45063)

Using CERMINE
-------------

CERMINE can be used for:

  * extracting metadata, full text and parsed references from a PDF file,
  * extracting metadata from reference strings,
  * extracting metadata from affiliation strings.

In all tasks the default output format is [NLM JATS](http://jats.nlm.nih.gov/archiving/tag-library/1.1/).

There are three way of using CERMINE, depending on the user's needs:

  * **standalone application** -- use this, if you need to process larger amounts of data locally on your laptop or server
  * **Maven dependency** -- allows to use CERMINE's API in your own Java/Scala code
  * **web application** -- for demonstration purposes and only small amounts (less than 50 files) of data

Refer to one of the sections below for details.


**Standalone application**

The easiest way to process files on a laptop/server is using CERMINE as a standalone application. All you will need is a single JAR file containing all the tools, external libraries and learned models. The latest release can be downloaded from [the repository](http://maven.icm.edu.pl/artifactory/simple/kdd-releases/pl/edu/icm/cermine/cermine-impl/) (look for a file called `cermine-impl-<VERSION>-jar-with-dependencies.jar`).

The file can be directly used for extraction. The following command will extract the metadata and content from PDF files:

    $ java -cp cermine-impl-<VERSION>-jar-with-dependencies.jar pl.edu.icm.cermine.ContentExtractor -path path/to/directory/with/pdfs/

To extract metadata from a reference string use the following:

    $ java -cp cermine-impl-<VERSION-jar-with-dependencies.jar pl.edu.icm.cermine.bibref.CRFBibReferenceParser -reference "the text of the reference"

Finally, to extract metadata from an affiliation string use:

    $ java -cp cermine-impl-<VERSION>-jar-with-dependencies.jar pl.edu.icm.cermine.metadata.affiliation.CRFAffiliationParser -affiliation "the text of the affiliation"

(OPTIONAL) if you would like to build an executable JAR yourself, clone the project and execute:

    $ cd CERMINE/cermine-impl
    $ mvn compile assembly:single

This will result in a file `cermine-impl-<VERSION>-jar-with-dependencies.jar` in `cermine-impl/target` directory.


**Maven dependency**

CERMINE can be used in Java projects by adding the following dependency and repository to the project's `pom.xml` file:

	<dependency>
		<groupId>pl.edu.icm.cermine</groupId>
		<artifactId>cermine-impl</artifactId>
		<version>${cermine.version}</version>
	</dependency>

	<repository>
		<id>icm</id>
		<name>ICM repository</name>
		<url>http://maven.icm.edu.pl/artifactory/repo</url>
	</repository>

Example code to extract the content from a PDF file:

	ContentExtractor extractor = new ContentExtractor();
	InputStream inputStream = new FileInputStream("path/to/pdf/file");
    extractor.setPDF(inputStream);
	Element result = extractor.getNLMContent();

Example code to extract metadata from a reference string:

	CRFBibReferenceParser parser = CRFBibReferenceParser.getInstance();
	BibEntry reference = parser.parseBibReference(referenceText);

Example code to extract metadata from an affiliation string:
	
	CRFAffiliationParser parser = new CRFAffiliationParser();
	Element affiliation = parser.parse(affiliationText);


**REST service**

The third possibility is to use CERMINE's REST service with cURL tool. Note, however, that this should only be used for small amounts of data, as the server does not have a lot of resources. Moreover, the web application might not use the latest code version. In most cases using the executable JAR is a better choice.

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

