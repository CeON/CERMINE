package pl.edu.icm.yadda.tools.reparser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;


public class NodeCategoryTest {

    @Test
    public void correctBounds() {
        List<Token> tokens = NodeCategory.tokenize("A. Smith, Foo  Bar \n Baz, J. Sci. Foo. (1998)\n");
        assertEquals("A", tokens.get(0).getContent());
        assertEquals(".", tokens.get(1).getContent());
        assertEquals("Smith", tokens.get(2).getContent());
        assertEquals(",", tokens.get(3).getContent());
        assertEquals("Foo", tokens.get(4).getContent());
        assertEquals("Bar", tokens.get(5).getContent());
        assertEquals("Baz", tokens.get(6).getContent());
        assertEquals(",", tokens.get(7).getContent());
        assertEquals("J", tokens.get(8).getContent());
        assertEquals(".", tokens.get(9).getContent());
        assertEquals("Sci", tokens.get(10).getContent());
        assertEquals(".", tokens.get(11).getContent());
        assertEquals("Foo", tokens.get(12).getContent());
        assertEquals(".", tokens.get(13).getContent());
        assertEquals("(", tokens.get(14).getContent());
        assertEquals("1998", tokens.get(15).getContent());
        assertEquals(")", tokens.get(16).getContent());
        
    }
    
}
