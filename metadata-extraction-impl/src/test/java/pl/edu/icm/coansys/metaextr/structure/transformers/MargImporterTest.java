package pl.edu.icm.coansys.metaextr.structure.transformers;

import pl.edu.icm.coansys.metaextr.structure.transformers.MargToTextrImporter;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

/**
 *
 * @author kura
 */
public class MargImporterTest {

    public MargImporterTest() {
    }

    @Test
    public void testImporter() throws IOException,  ParserConfigurationException, SAXException,
            TransformationException {
       BxPage page=new MargToTextrImporter().read(new InputStreamReader(this.getClass().getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/imports/MargImporterTest1.xml"))).get(0);
       boolean contains=false;
       boolean rightText=false;
       boolean rightSize=false;
       for (BxZone zone:page.getZones()) {
          if (zone.getLabel()!=null) {
           if (zone.getLabel().equals(BxZoneLabel.MET_AUTHOR)) {
               contains=true;
               System.out.println(zone.toText());
               // takie cos na toplevelu                 Howard M. Schachter,* Ba' Pham,* Jim King,tt  Stephanie Langford,* David Moher*$
              if (zone.toText().trim().equalsIgnoreCase("Howard M  Schachter   Ba  Pham   Jim King tt\nStephanie Langford   David Moher".trim())) {
                  rightText=true;
              }
              if (zone.getBounds().getX()==72 && zone.getBounds().getY()==778 && zone.getBounds().getWidth()==989 && zone.getBounds().getHeight()==122) {
                    rightSize=true;
                  } else {
                   System.out.println(zone.getBounds().getX()+ " " + zone.getBounds().getY() +" "+zone.getBounds().getWidth()+ " "+zone.getBounds().getHeight());
                  }
           }
         } else {
              System.out.println("Zone with no label: "+zone.toText());
         }
       }
       assertTrue(contains);
       assertTrue(rightText);
       assertTrue(rightSize);
    }



    @Test
    public void testHeight() throws IOException,  ParserConfigurationException, SAXException, TransformationException {
       BxPage page=new MargToTextrImporter().read(new InputStreamReader(this.getClass().getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/006.xml"))).get(0);

       for (BxZone zone:page.getZones()) {
           assertTrue("Zero heigh zone: "+zone.toText()+" : "+zone.getLabel().name(),zone.getBounds().getHeight()>0);

       }
    
    }

}