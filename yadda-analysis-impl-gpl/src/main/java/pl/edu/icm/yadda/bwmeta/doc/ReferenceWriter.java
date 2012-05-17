package pl.edu.icm.yadda.bwmeta.doc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.edu.icm.yadda.bwmeta.doc.ElementType.Kind;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class ReferenceWriter {
	protected static final String logoResource = "pl/edu/icm/yadda/bwmeta/doc/yadda.png";
	
	protected static final int SPACE = 5;
	
	protected byte[] readBytesFromStream(InputStream stream) throws IOException {
		int BUFFER_SIZE = 65536;
		
		int size = 0;
		List<byte[]> pieces = new ArrayList<byte[]>();
		while (true) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = stream.read(buffer);
			pieces.add(buffer);
			size += read;
			if (read < buffer.length)
				break;
		}
		
		byte[] result = new byte[size];
		int offset = 0;
		for (byte[] piece : pieces) {
			for (int i = 0; i < piece.length && offset + i < size; i++)
				result[offset + i] = piece[i];
			offset += piece.length;
		}
		
		return result;
	}
	
	protected PdfPageEventHelper peh = new PdfPageEventHelper() {
		protected PdfTemplate total;
		protected BaseFont helv;
		protected PdfGState gstate;
		protected Image image;
		
		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(100, 100);
			total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
			try {
				helv = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
				byte[] bytes = readBytesFromStream(this.getClass().getClassLoader().getResourceAsStream(logoResource));
				image = Image.getInstance(bytes);
			} catch (Exception e) {
				throw new ExceptionConverter(e);
			}
			gstate = new PdfGState();
			gstate.setFillOpacity(0.3f);
			gstate.setStrokeOpacity(0.3f);
		}
		
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte contentunder = writer.getDirectContentUnder();
				contentunder.saveState();
				contentunder.setGState(gstate);
				contentunder.addImage(image, 2*image.getWidth(), 0, 0, 2*image.getHeight(),
						(document.getPageSize().getWidth() - 2*image.getWidth()) / 2,
						(document.getPageSize().getHeight() - 2*image.getHeight()) / 2);
				contentunder.restoreState();
			} catch (DocumentException e) {
				throw new RuntimeException(e);
			}

			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();
			String textPage = "Page " + writer.getPageNumber() + " of ";
			String textCopy = "(C) 2010 ICM, University of Warsaw";
			float textBase = document.bottom() - 20;
			float textPageSize = helv.getWidthPoint(textPage, 10);

			cb.beginText();
			cb.setFontAndSize(helv, 10);
			cb.setTextMatrix(document.left(), textBase);
			cb.setColorFill(BaseColor.GRAY);
			cb.showText(textCopy);
			cb.endText();

			float adjust = helv.getWidthPoint("0", 10);
			cb.beginText();
			cb.setFontAndSize(helv, 10);
			cb.setTextMatrix(document.right() - textPageSize - adjust, textBase);
			cb.setColorFill(BaseColor.GRAY);
			cb.showText(textPage);
			cb.endText();
			cb.addTemplate(total, document.right() - adjust, textBase);

			cb.restoreState();
		}
		
		public void onCloseDocument(PdfWriter writer, Document document) {
			total.beginText();
			total.setFontAndSize(helv, 10);
			total.setTextMatrix(0, 0);
			total.showText(String.valueOf(writer.getPageNumber() - 1));
			total.endText();
		}
	};
	
	
	
	public void writePDF(XsdSchema schema, String fileName) {
		com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
			writer.setPageEvent(peh);
			
			document.addTitle("BWmeta Schema Reference");
			document.addAuthor("Interdisciplinary Centre for Mathematical and Computational Modelling (University of Warsaw)");
			document.addCreator("YADDA platform");
			document.open();

			Font titleFont = new Font(FontFamily.HELVETICA, 24, Font.BOLD);
			Font subtitleFont = new Font(FontFamily.HELVETICA, 8, Font.ITALIC);
			Font docFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);

			String title = "BWmeta 1.2.0 Schema Reference";
			Paragraph titlePara = new Paragraph(title, titleFont);
			titlePara.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(titlePara);
			if (schema.getRevision() > 0) {
				String rev = "(Revision " + schema.getRevision() + ")";
				Paragraph revPara = new Paragraph(rev, subtitleFont);
				revPara.setAlignment(Paragraph.ALIGN_CENTER);
				document.add(revPara);
			}

			document.add(new Paragraph(" ", new Font(FontFamily.HELVETICA, SPACE)));

			Paragraph mainDocPara = new Paragraph(schema.getDocumentation(), docFont);
			mainDocPara.setAlignment(Paragraph.ALIGN_JUSTIFIED);
			document.add(mainDocPara);

						
			for (String tagName : schema.getElements().keySet()) {
				for (Paragraph para : documentTag(schema, tagName))
					document.add(para);
			}
			
			document.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected List<Paragraph> documentTag(XsdSchema schema, String tagName) {
		Font nodeFont = new Font(FontFamily.COURIER, 18, Font.BOLD | Font.UNDERLINE);
		Font sectionFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		Font docFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
		Font attrNameFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

		List<Paragraph> paras = new ArrayList<Paragraph>();
		XsdElement tag = schema.getElements().get(tagName);
		
		Paragraph descPara = null;
		paras.add(new Paragraph(" ", new Font(FontFamily.HELVETICA, 3*SPACE)));

		descPara = new Paragraph();
		descPara.setFont(nodeFont);
		Anchor anchor = new Anchor(tag.getName(), nodeFont);
		anchor.setName(tag.getName());
		descPara.add(anchor);
		paras.add(descPara);

		paras.add(new Paragraph(" ", new Font(FontFamily.HELVETICA, SPACE)));
		
		descPara = new Paragraph();
		descPara.setAlignment(Paragraph.ALIGN_JUSTIFIED);
		descPara.setFont(docFont);
		descPara.add(tag.getDocumentation());
		paras.add(descPara);
		
		if (!tag.getAttributes().isEmpty()) {
			paras.add(new Paragraph(" ", new Font(FontFamily.HELVETICA, SPACE)));
			descPara = new Paragraph();
			descPara.setAlignment(Paragraph.ALIGN_JUSTIFIED);
			descPara.setFont(docFont);

			descPara.add(new Chunk("Attributes:", sectionFont));
			com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED, 10);
			list.setListSymbol(new Chunk("4", new Font(FontFamily.ZAPFDINGBATS, 10)));
			for (XsdAttribute attr : tag.getAttributes()) {
				ListItem li = new ListItem();
				li.add(new Phrase(attr.getName(), attrNameFont));
				li.add(new Phrase(" (" + (attr.isRequired() ? "required" : "optional") + "): ", docFont));
				li.add(new Phrase(attr.getDocumentation(), docFont));
				list.add(li);
			}
			descPara.add(list);
			paras.add(descPara);
		}

		List<Element> typeDoc = documentType(tag.getType(), docFont);
		if (!typeDoc.isEmpty()) {
			paras.add(new Paragraph(" ", new Font(FontFamily.HELVETICA, SPACE)));
			descPara = new Paragraph();
			descPara.setAlignment(Paragraph.ALIGN_JUSTIFIED);
			descPara.setFont(docFont);
			descPara.add(new Chunk("Contents:", sectionFont));
			paras.add(descPara);

			descPara = new Paragraph();
			descPara.setAlignment(Paragraph.ALIGN_JUSTIFIED);
			descPara.setFont(docFont);
			for (Element e : typeDoc)
				descPara.add(e);
			paras.add(descPara);
		}
		
		return paras;
	}

	protected List<Element> documentType(ElementType type, Font font) {
		List<Element> list = new ArrayList<Element>();

		Font simpleFont = new Font(font);
		simpleFont.setStyle(simpleFont.getStyle() | Font.ITALIC);

		if (type == null) {
			list.add(new Chunk("(empty)", simpleFont));
			return list;
		}
		
		if (type.getKind() == Kind.Element) {
			Font anchorFont = new Font(font);
			anchorFont.setColor(BaseColor.BLUE);
			Anchor anchor = new Anchor(type.getName(), anchorFont);
			anchor.setReference("#" + type.getName());
			list.add(anchor);
			if (type.getMinOccurs() == 0 && type.getMaxOccurs() == -1)
				list.add(new Chunk("*", font));
			if (type.getMinOccurs() == 0 && type.getMaxOccurs() == 1)
				list.add(new Chunk("?", font));
			if (type.getMinOccurs() == 1 && type.getMaxOccurs() == -1)
				list.add(new Chunk("+", font));
			return list;
		}
		
		if (type.getKind() == Kind.Choice) {
			list.add(new Chunk("(", font));
			boolean first = true;
			for (ElementType child : type.getChildren()) {
				if (!first)
					list.add(new Chunk(" | ", font));
				first = false;
				list.addAll(documentType(child, font));
			}
			list.add(new Chunk(")", font));
			if (type.getMinOccurs() == 0 && type.getMaxOccurs() == -1)
				list.add(new Chunk("*", font));
			if (type.getMinOccurs() == 0 && type.getMaxOccurs() == 1)
				list.add(new Chunk("?", font));
			if (type.getMinOccurs() == 1 && type.getMaxOccurs() == -1)
				list.add(new Chunk("+", font));
			return list;
		}
		
		if (type.getKind() == Kind.Sequence) {
			list.add(new Chunk("(", font));
			boolean first = true;
			for (ElementType child : type.getChildren()) {
				if (!first)
					list.add(new Chunk(", ", font));
				first = false;
				list.addAll(documentType(child, font));
			}
			list.add(new Chunk(")", font));
			if (type.getMinOccurs() == 0 && type.getMaxOccurs() == -1)
				list.add(new Chunk("*", font));
			if (type.getMinOccurs() == 0 && type.getMaxOccurs() == 1)
				list.add(new Chunk("?", font));
			if (type.getMinOccurs() == 1 && type.getMaxOccurs() == -1)
				list.add(new Chunk("+", font));
			return list;
		}

		if (type.getKind() == Kind.Simple) {
			list.add(new Chunk("(text content)", simpleFont));
			return list;
		}
		
		return Collections.emptyList();
	}
}
