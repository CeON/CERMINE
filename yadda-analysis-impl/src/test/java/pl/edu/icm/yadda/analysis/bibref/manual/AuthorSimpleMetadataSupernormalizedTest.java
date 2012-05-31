package pl.edu.icm.yadda.analysis.bibref.manual;

import org.junit.Test;
import static org.junit.Assert.*;
import pl.edu.icm.yadda.tools.bibref.model.AuthorSimpleMetadata;

public class AuthorSimpleMetadataSupernormalizedTest {

    @Test
    public void lastSurnamePartTest() {
        AuthorSimpleMetadataSupernormalized meta1 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Nowak-Kowalski", "Jan Andrzej"));
        AuthorSimpleMetadataSupernormalized meta2 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Nowak Kowalski", "Jan Andrzej"));
        AuthorSimpleMetadataSupernormalized meta3 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Kowalski", "Jan Andrzej"));
        
        assertEquals("Kowalski", meta1.getLastSurnamePart());
        assertEquals("Kowalski", meta2.getLastSurnamePart());
        assertEquals("Kowalski", meta3.getLastSurnamePart());
    }
    
    @Test
    public void supernormalizedTest() {
        AuthorSimpleMetadataSupernormalized meta1 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Nowak-Kowalski", "Jan Andrzej"));
        AuthorSimpleMetadataSupernormalized meta2 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Nowak Kowalski", "Jan Andrzej"));
        AuthorSimpleMetadataSupernormalized meta3 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Nowak-Kowalski", "J. A."));
        AuthorSimpleMetadataSupernormalized meta4 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Nowak Kowalski", "Jan A."));
        AuthorSimpleMetadataSupernormalized meta5 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Kowalski", "J. A."));
        AuthorSimpleMetadataSupernormalized meta6 = new AuthorSimpleMetadataSupernormalized(new AuthorSimpleMetadata("Kowalski", "Jan Andrzej"));
        
        assertEquals("JANKowalski", meta1.getSupernormalized());
        assertEquals("JANKowalski", meta2.getSupernormalized());
        assertEquals("JANKowalski", meta3.getSupernormalized());
        assertEquals("JANKowalski", meta4.getSupernormalized());
        assertEquals("JAKowalski", meta5.getSupernormalized());
        assertEquals("JAKowalski", meta6.getSupernormalized());
    }

    
}
