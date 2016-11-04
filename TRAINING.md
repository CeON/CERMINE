# CERMINE training procedure

One of the most important tasks in CERMINE's workflow is assigning roles to the
document's fragments, which is done by zone classifiers. Zone classifiers are
the heart of the system and along with the page segmenter have the strongest
impact on the classification results.

A zone is a consistent fragment of the document's text, geometrically separated
from surrounding elements and not divided into columns. CERMINE has three zone
classifiers: **category classifier** assigns general categories (*metadata*,
*body*, *references* and *other*) to zones, **metadata classifier** labels
metadata zones with specific metadata classes, and **body classifier** filters
out framents like tables, images and labels from the full text.

The classifiers analyze zones represented as vectors of features, which are
computed from the text content of a zone, zones sequence-related information,
text formatting and geometric information (dimensions, distance, position). In
general the features reflect both the text content of a zone and the way the
text is displayed in a PDF file.

Both classifiers are based on Support Vector Machines, and CERMINE's SVM
implementation uses [LibSVM library](http://www.csie.ntu.edu.tw/~cjlin/libsvm/).
CERMINE contains the classifiers code, default models, and also tools for
training new models.

## Document data format

A document set, the can be used for training and testing the classifiers using
CERMINE tools, needs to store documents as **TrueViz** files.
[TrueViz](http://www.kanungo.com/software/software.html#trueviz) is an XML-based
format that allows to represent the document as a geometric hierarchical
structure containing on consecutive levels: pages, zones, lines, words and
characters. TrueViz allows to preserve the information about the text content of
all the elements, the coordinates of the elements on their pages, the order in
which the elements should be read and also zone labels.

TrueViz files can be viewed and edited manually with the use of [SegmEdit
tool](https://github.com/CeON/SegmEdit). SegmEdit reads the document's PDF file
and displays its content. The hierarchical structure obtained from the
corresponding TrueViz file is displayed in the form of rectangles on top of the
PDF's image. The user can edit the structure, regroup elements or modify zone
labels.

## Available datasets and models

The main document dataset used in CERMINE is
[GROTOAP2](http://cermine.ceon.pl/grotoap2). The dataset contains 13,210 TrueViz
documents based on publications from [PubMed Central Open Access
Subset](https://www.ncbi.nlm.nih.gov/pmc/tools/openftlist/). A set of about 2,5k
documents from GROTOAP2 was used to train CERMINE's default models for all zone
classifiers.

Due to the semi-automatic creation method, GROTOAP2 contains segmentation and
labelling errors. Fortunately, thanks to the large volume of data, small
fraction of errors do not lower the classification and extraction results
significantly.

The default models are available in [CERMINE's
resources](https://github.com/CeON/CERMINE/tree/master/cermine-impl/src/main/resources/pl/edu/icm/cermine). 

## Retraining zone classifiers

Sometimes the default models are not enough, especially for documents with
layouts very different from biomedical publications. In such cases there is a
possibility to retrain zone classifiers based on a set of documents in PDF
format. To do this, one has to perform the following steps:

1. Creating a dataset of TrueViz files for training

  1.1 Download the latest version of CERMINE's JAR:

        $ wget http://maven.ceon.pl/artifactory/simple/kdd-snapshots/pl/edu/icm/cermine/cermine-tools/<VERSION>-SNAPSHOT/cermine-tools-<VERSION>-jar-with-dependencies.jar \
          -O cermine.jar

  1.2 Create initial TrueViz files from a set of PDFs using CERMINE's extraction
  tools:

        $ java -cp cermine.jar pl.edu.icm.cermine.ContentExtractor -path training/set/directory/ -outputs trueviz

    This will result in creating .cermstr files containing the structure in
    TrueViz format.

  1.3 Download and open SegmEdit tool:

        $ git clone https://github.com/CeON/SegmEdit.git
          cd SegmEdit/SegmEditGUI/
          ./segmedit.py

    Refer to
    [SEGMEDITGUI-HELP](https://github.com/CeON/SegmEdit/blob/master/SegmEditGUI/SEGMEDITGUI-HELP)
    for the help regarding needed Python packages.

  1.4 .cermstr files were created automatically and most likely contain errors.
  Use SegmEdit to correct the contents of the files. The most important thing is
  to correct the labelling of all the zones.

2. Training the classifiers

  The corrected files can now be fed to the trainers. The following commands
  will train new models for two zone classifiers based on a set of TrueViz
  files:

        $ java -cp cermine.jar pl.edu.icm.cermine.libsvm.training.SVMMetadataBuilder \
          -input path/to/directory/with/trueviz/ -output model-metadata

        $ java -cp cermine.jar pl.edu.icm.cermine.libsvm.training.SVMBodyBuilder \
          -input path/to/directory/with/trueviz/ -output model-body

        $ java -cp cermine.jar pl.edu.icm.cermine.libsvm.training.SVMInitialBuilder \
          -input path/to/directory/with/trueviz/ -output model-category

  During the training process, CERMINE reads TrueViz files from the input
  directory, converts all zones to feature vectors and generates an SVM model
  from the entire set. The output contains two files: a model (in our case:
  *model-metadata*, *model-body* and *model-category* files) and a file
  containing values ranges of the features (in our case: *model-metadata.range*,
  *model-body.range* and *model-initial.range* files). Both files are used by
  the classifiers during analysing the document.

3. Using the custom models

  3.1 Create .properties file with the following content:

        # paths to category/initial model files
        zoneClassifier.initial.default.model=model-category
        zoneClassifier.initial.default.rangeModel=model-category.range

        # paths to metadata model files
        zoneClassifier.metadata.default.model=model-metadata
        zoneClassifier.metadata.default.rangeModel=model-metadata.range

        # paths to body model files
        contentFilter.default.model=model-body
        contentFilter.default.rangeModel=model-body.range

  3.2 Metadata extraction using custom .properties file:

        $ java -cp cermine.jar pl.edu.icm.cermine.ContentExtractor \
          -configuration file.properties -path path/to/directory/with/pdfs/

