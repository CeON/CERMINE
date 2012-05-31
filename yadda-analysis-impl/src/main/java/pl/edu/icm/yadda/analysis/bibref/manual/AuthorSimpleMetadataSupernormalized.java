package pl.edu.icm.yadda.analysis.bibref.manual;

import org.apache.commons.lang.StringUtils;

import pl.edu.icm.yadda.tools.bibref.model.AuthorSimpleMetadata;

public class AuthorSimpleMetadataSupernormalized {
    private final AuthorSimpleMetadata innerMetadata;
    private final String lastSurnamePart;
    private final String supernormalized;

    public AuthorSimpleMetadataSupernormalized(final AuthorSimpleMetadata innerMetadata) {
        this.innerMetadata = innerMetadata;
        final String niceSurname = innerMetadata.getSurname().replaceAll("\\P{L}", " ");
        final String[] surnameParts = niceSurname.split(" ");
        final String surnameBeginning;
        if (surnameParts.length > 0) {
            lastSurnamePart = surnameParts[surnameParts.length - 1];
            surnameBeginning = StringUtils.join(surnameParts, ' ', 0, surnameParts.length - 1);
        } else {
            lastSurnamePart = "";
            surnameBeginning = "";
        }
        final String begining = innerMetadata.getGivennames() + surnameBeginning;
        supernormalized = begining.replaceAll("\\P{Lu}", "") + lastSurnamePart;
    }

    public String getGivennames() {
        return innerMetadata.getGivennames();
    }

    public String getSurname() {
        return innerMetadata.getSurname();
    }

    public String getNormalized() {
        return innerMetadata.getNormalized();
    }

    public String getLastSurnamePart() {
        return lastSurnamePart;
    }

    public String getSupernormalized() {
        return supernormalized;
    }
}
