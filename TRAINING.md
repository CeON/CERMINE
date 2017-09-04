# Training procedures in CERMINE

## Introduction

CERMINE is to a great extent based on machine learning. Supervised classifiers
are the heart of the implementation of key extraction workflow parts, such as
zone (block) classification or reference parsing.

CERMINE by default contains predefined models for classification, which can be
used "as is". It is possible, however, to prepare custom models and use them for
the extraction instead of the default ones. This feature of the system is
particularly useful when we need to process specific document layouts or
reference formats, which might not be present in the training sets used to learn
default models.

This document describes the procedures of preparing custom models for two tasks:
document zone classification and reference parsing, and gives information about
how to include custom models in the system.

## Zone classification

One of the most important tasks CERMINE internally performs is assigning roles
to the document's text fragments. The task is called zone classification. Zone
classifiers are the heart of the system and along with the page segmenter have
the strongest impact on the metadata extraction results.

A zone is a consistent fragment of the document's text, geometrically separated
from surrounding elements and not divided into columns. CERMINE has three zone
classifiers: category classifier assigns general categories (metadata, body,
references and other) to zones, metadata classifier labels metadata zones with
specific metadata classes, and body classifier filters out fragments like
tables, images and labels from the full text.

The classifiers analyze zones represented as vectors of features, which are
computed from the text content of a zone, zones sequence-related information,
text formatting and geometric information (dimensions, distance, position). In
general the features reflect both the text content of a zone and the way the
text is displayed in a PDF file.

Both classifiers are based on Support Vector Machines, and CERMINE's SVM
implementation uses [LibSVM
library](http://www.csie.ntu.edu.tw/%7Ecjlin/libsvm/). CERMINE contains the
classifiers code, default models, and also tools for training new models.

### Available resources

CERMINE contains three [zone classification
models](https://github.com/CeON/CERMINE/tree/master/cermine-impl/src/main/resources/pl/edu/icm/cermine),
one for each zone classifier. The models were trained using 2,500 documents from
[GROTOAP2 dataset](https://repod.pon.edu.pl/dataset/grotoap2).

GROTOAP2 contains 13,210 documents generated from [PubMed Central Open Access
Subset resources](https://www.ncbi.nlm.nih.gov/pmc/tools/openftlist/). The
dataset uses TrueViz format to store documents.
[TrueViz](http://www.kanungo.com/software/software.html#trueviz) is an XML-based
format that allows to represent the document as a geometric hierarchical
structure containing on consecutive levels: pages, zones, lines, words and
characters. TrueViz allows to preserve the information about the text content of
all the elements, the coordinates of the elements on their pages, the order in
which the elements should be read and also zone labels.

### Training

Sometimes the default models will not result in satisfactory results, especially
if we deal with documents with layouts very different from those used for
training CERMINE's models. In such cases it is possible to retrain zone
classifiers based on a set of documents in PDF format.

#### Preparing the dataset

First, PDF documents have to be transformed in TrueViz format. This step is
necessary, because PDF format does not preserve the information needed for
building the feature representations of the zones.

First, download the latest version of CERMINE's JAR file:

    $ wget http://maven.ceon.pl/artifactory/simple/kdd-snapshots/pl/edu/icm/cermine/cermine-tools/<VERSION>-SNAPSHOT/cermine-tools-<VERSION>-jar-with-dependencies.jar \
      -O cermine.jar

Initial TrueViz files can be generated from a set of PDFs using CERMINE's
extraction tools:

    $ java -cp cermine.jar pl.edu.icm.cermine.ContentExtractor \
      -path path/to/directory/with/pdfs -outputs trueviz

This will result in a single **.cermstr* file for every **.pdf* file found in
the input directory. The new files contain the structure in TrueViz format. Of
course, since the files were generated automatically, they will contain errors
(for example some zones can have wrong labels), and it is necessary to correct
them manually.

Editing TrueViz files is easily done with [SegmEdit
tool](https://github.com/CeON/SegmEdit). This application written in Python
allows the user to inspect and edit the structure of a TrueViz file by
displaying various objects (words, lines or zones) in a form of rectangles on
top of the original PDF file.

The following instructions can be used to download and open SegmEdit tool
(refer to [SegmEdit
help](https://github.com/CeON/SegmEdit/blob/master/SegmEditGUI/SEGMEDITGUI-HELP.md)
for the help regarding needed Python packages).

    $ git clone https://github.com/CeON/SegmEdit.git
    $ cd SegmEdit/SegmEditGUI/
    $ ./segmedit.py

After opening, use SegmEdit to correct the contents of the training files. The
most important thing is to correct the labelling of all the zones.

#### Training the classifiers

The corrected training files can now be fed to the trainers. The following
commands will train new models for three zone classifiers based on the prepared
set of TrueViz files:

    $ java -cp cermine.jar pl.edu.icm.cermine.libsvm.training.SVMMetadataBuilder \
      -input path/to/directory/with/trueviz/ -output model-metadata

    $ java -cp cermine.jar pl.edu.icm.cermine.libsvm.training.SVMBodyBuilder \
      -input path/to/directory/with/trueviz/ -output model-body

    $ java -cp cermine.jar pl.edu.icm.cermine.libsvm.training.SVMInitialBuilder \
      -input path/to/directory/with/trueviz/ -output model-category

During the training process, CERMINE reads TrueViz files from the input
directory, converts all zones to feature vectors and generates an SVM model
from the entire set. The output contains two files per classifier: a model (in
our case: *model-metadata*, *model-body* and *model-category* files) and a file
containing values ranges of the features (in our case: *model-metadata.range*,
*model-body.range* and *model-initial.range* files). Both files are used by the
classifiers during analysing the document.

### Using custom models

The information about models to use is passed to CERMINE through a *.properties*
file. The system contains a [default .properties
file](https://github.com/CeON/CERMINE/blob/master/cermine-impl/src/main/resources/pl/edu/icm/cermine/application-default.properties),
in which the model paths refer to internal system resources. To use a custom
model, we have to create a new *.properties* file with new values for the paths
we wish to change.

In the case of zone classifiers, these will be the following properties:

    # paths to category/initial model files
    zoneClassifier.initial.model=model-category
    zoneClassifier.initial.ranges=model-category.range

    # paths to metadata model files
    zoneClassifier.metadata.model=model-metadata
    zoneClassifier.metadata.ranges=model-metadata.range

    # paths to body model files
    contentFilter.model=model-body
    contentFilter.ranges=model-body.range

Of course, if we are interested in having a custom model for one of the
classifiers only, it is enough to provide only the relevant two paths. After
creating a custom *.properties* file, the path to the file  needs to be given as
a parameter for the extraction:

    $ java -cp cermine.jar pl.edu.icm.cermine.ContentExtractor \
      -configuration file.properties -path path/to/directory/with/pdfs/

## Reference parsing

The task of reference parsing refers to extracting metadata from reference
strings. In fact, this is the metadata of the document the reference points to.
For example, parsing could transform the following string:

    Dominika Tkaczyk, Pawel Szostek, Mateusz Fedoryszak, Piotr Jan Dendek, Lukasz Bolikowski:
    CERMINE: automatic extraction of structured metadata from scientific literature. IJDAR 18(4): 317-335 (2015)

into a machine readable version (BibTex format):

    @article{DBLP:journals/ijdar/TkaczykSFDB15,
      author    = {Dominika Tkaczyk and
                   Pawel Szostek and
                   Mateusz Fedoryszak and
                   Piotr Jan Dendek and
                   Lukasz Bolikowski},
      title    = {CERMINE: automatic extraction of structured metadata from scientific literature},
      journal   = {IJDAR},
      volume    = {18},
      number    = {4},
      pages     = {317--335},
      year      = {2015}
    }

In CERMINE reference parsing is done in three steps: 1) tokenizing the reference
string, 2) assigning labels to the individual tokens, and 3) concatenating
tokens to form final metadata record. Tokenizing and concatenating steps are
straightforward, but assigning labels is a non-trivial problem. In CERMINE it is
solved by a supervised classifier, which analyses the tokens represented by bags
of features. The features include: terms (tokens themselves), general term
classes and orthographic properties.

Token classification in CERMINE is based on Conditional Random Fields and
implemented using [GRMM library](http://mallet.cs.umass.edu/grmm/index.php).

### Available resources

CERMINE contains a [default
model](https://github.com/CeON/CERMINE/tree/master/cermine-impl/src/main/resources/pl/edu/icm/cermine/bibref)
for reference token labelling. The model is composed of two files: a
GRMM-specific model file, and a list of common terms used as features.

Both files were prepared using [GROTOAP2-citations
dataset](https://repod.pon.edu.pl/dataset/grotoap-citations). The dataset
contains 6,858 citations in various formats: text (raw strings), BibTeX and NLM
JATS (parsed citations with metadata).

### Training

Similarly as in the case of zone classifier, it is possible to prepare a custom
model for reference parsing. This feature is useful if we deal with a new
citation format, not known to the default model.

First, we will need a dataset of citations in NLM JATS format. It is an
XML-based format, in which the raw citation string is preserved, and metadata
fields are present as XML elements with meaningful names:

    <mixed-citation>9.
      <string-name><surname>Karageorgiou</surname> <given-names>V</given-names></string-name>,
      <string-name><surname>Kaplan</surname> <given-names>D.</given-names></string-name>
      <article-title>Porosity of 3D biomaterial scaffolds and osteogenesis</article-title>.
      <source>Biomaterials</source>.
      <year>2005</year>;
      <volume>26</volume>
      (<issue>27</issue>):
      <fpage>5474</fpage>- <lpage>91</lpage>.
    </mixed-citation>

The dataset of parsed citations should be a single XML file with the following
format:

    <refs>
      <mixed-citation>...</mixed-citation>
      <mixed-citation>...</mixed-citation>
      ...
    </refs>

Preparing such a dataset can be done automatically by transforming citations
from another format, manually or using external XML editing tools.

Next, we can extract the features from the citation tokens and the dataset
should be represented in a GRMM-specific training format:

    $ java -cp cermine.jar pl.edu.icm.cermine.bibref.MalletTrainingFileGenerator \
      input/path/to/dataset output/path/to/GRMM/input output/path/to/terms/list

This will result in two files: a file with features for GRMM
(*output/path/to/GRMM/input*) and terms file (*output/path/to/terms/list*).

Download GRMM library and build it:

    $ wget http://mallet.cs.umass.edu/dist/grmm-0.1.3.tar.gz
    $ tar -xzf grmm-0.1.3.tar.gz
    $ cd grmm-0.1.3
    $ ant
    $ echo "new ACRF.BigramTemplate (0)" > tmpls.txt

The last command creates a file with CRF architecture specification, which is
required by GRMM. Now we can train the model:

    $ java -cp $GRMM/class:$GRMM/lib/mallet-deps.jar:$GRMM/lib/grmm-deps.jar \
      edu.umass.cs.mallet.grmm.learning.GenericAcrfTui \ 
      --training output/path/to/GRMM/input --testing output/path/to/GRMM/input \
      --model-file tmpls.txt

Since we are interested in training only, we should not need the *--testing*
parameter. However, GRMM throws an exception if it is not given. It should be
noted, however, that since we are training and testing on the same dataset, the
evaluation results from the command above are meaningless and should be
discarded.

This will result in *acrf.ser.gz* file, which is a GRMM model. This model, along
with the terms file generated previously, can be used during the analysis.
Similarly as in the case of zone classifiers, this is done by providing a custom
*.properties* file, with the following content:

    # path to bibref parsing model
    bibref.model=path/to/acrf.ser.gz
    # path to bibref terms list
    bibref.terms=/output/path/to/terms/list
