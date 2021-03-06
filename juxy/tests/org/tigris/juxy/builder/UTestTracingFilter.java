package org.tigris.juxy.builder;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.tigris.juxy.TestUtil;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.Tracer;
import org.tigris.juxy.util.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * $Id: UTestTracingFilter.java,v 1.14 2006-11-09 18:59:07 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTracingFilter extends TestCase {
  private static final String XSLT_XMLNS = "xmlns:xsl='" + XSLTKeys.XSLT_NS + "'";
  private static final String AUGMENTED_STYLESHEET_TAG = "<xsl:stylesheet version='1.0' " + XSLT_XMLNS + ">";
  private static final String START_STYLESHEET_TAG = "<xsl:stylesheet version='1.0' " + XSLT_XMLNS + ">\n";
  private static final String END_STYLESHEET_TAG = "</xsl:stylesheet>";

  private String filteredStylesheet;
  private XSLTEngineSupport engineSupport;

  public UTestTracingFilter(String name) {
    super(name);
  }

  public static TestSuite suite() {
    if (!TestUtil.isTracingSupported()) {
      return new TestSuite();
    }

    return new TestSuite(UTestTracingFilter.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    engineSupport = new XSLTEngineSupport();
  }

  protected void tearDown() throws Exception {
    System.out.println(filteredStylesheet);
    // to check that filtered stylesheet is valid
    TransformerFactory.newInstance().
        newTransformer(new StreamSource(new ByteArrayInputStream(filteredStylesheet.getBytes())));
  }

  public void testInstructionOutOfTemplate() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "<xsl:variable name='var1'/>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "<xsl:variable name='var1'/>" +
        END_STYLESHEET_TAG);
  }

  public void testTemplate() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'></xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testValueOf() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <xsl:value-of select=\"''\"/>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("<xsl:value-of select=\"''\">", 3, 2) +
        "       <xsl:value-of select=\"''\"/>" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testAttribute() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <xsl:element name='root'>\n" +
        "           <xsl:attribute name='id'>\n" +
        "               <xsl:value-of select='5'/>\n" +
        "           </xsl:attribute>\n" +
        "       </xsl:element>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("<xsl:element name=\"root\">", 3, 2) +
        "       <xsl:element name='root'>" +
        "           <xsl:attribute name='id'>" +
        makeValueOf("<xsl:attribute name=\"id\">", 4, 3) +
        makeValueOf("<xsl:value-of select=\"5\">", 5, 4) +
        "               <xsl:value-of select=\"5\"/>" +
        "           </xsl:attribute>" +
        "       </xsl:element>" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testIf() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <xsl:if test='true()'>\n" +
        "           <root/>\n" +
        "       </xsl:if>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("<xsl:if test=\"true()\">", 3, 2) +
        "       <xsl:if test='true()'>\n" +
        makeValueOf("<root>", 4, 3) +
        "           <root/>\n" +
        "       </xsl:if>\n" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testChoose() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <xsl:choose>\n" +
        "           <xsl:when test='true()'/>\n" +
        "           <xsl:otherwise/>\n" +
        "       </xsl:choose>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("<xsl:choose>", 3, 2) +
        "       <xsl:choose>" +
        "           <xsl:when test='true()'>" +
        makeValueOf("<xsl:when test=\"true()\">", 4, 3) +
        "</xsl:when>" +
        "           <xsl:otherwise>" +
        makeValueOf("<xsl:otherwise>", 5, 3) +
        "           </xsl:otherwise>" +
        "       </xsl:choose>" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testForEach() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <xsl:for-each select='/root'>\n" +
        "           <item/>\n" +
        "       </xsl:for-each>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        "       <xsl:for-each select='/root'>" +
        makeValueOf("<xsl:for-each select=\"/root\">", 3, 2) +
        makeValueOf("<item>", 4, 3) +
        "           <item/>" +
        "       </xsl:for-each>" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testCallTemplate() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <xsl:call-template name='tpl2'>\n" +
        "           <xsl:with-param name='p1' select='v1'/>\n" +
        "       </xsl:call-template>\n" +
        "   </xsl:template>\n" +
        "   <xsl:template name='tpl2'>\n" +
        "       <xsl:param name='p1'/>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("<xsl:call-template name=\"tpl2\">", 3, 2) +
        "       <xsl:call-template name='tpl2'>" +
        "           <xsl:with-param name='p1' select='v1'/>" +
        "       </xsl:call-template>" +
        "   </xsl:template>" +
        "   <xsl:template name='tpl2'>" +
        "       <xsl:param name='p1'/>" +
        makeValueOf("<xsl:template name=\"tpl2\">", 7, 1) +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testTemplateParamsAndProcessingInstruction() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl2'><?pi?>\n" +
        "       <xsl:param name='p1'/>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl2'><?pi?>" +
        "       <xsl:param name='p1'/>" +
        makeValueOf("<xsl:template name=\"tpl2\">", 2, 1) +
        makeValueOf("<?pi?>", 2, 1) +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testTextNodes() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       some text\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("some text", 3, 1) +
        "       some text\n" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testTextNodesMixed() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       some text<xsl:value-of select='aaa'/> text continued\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("some text", 3, 1) +
        "       some text" +
        makeValueOf("<xsl:value-of select=\"aaa\">", 3, 2) +
        "       <xsl:value-of select='aaa'/>" +
        makeValueOf("text continued", 3, 1) +
        "       text continued" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testTextMustBeBeEscaped() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       some &lt;&amp; ' text\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("some <& ' text", 3, 1) +
        "       some &lt;&amp; ' text\n" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testMaximumLengthText() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       first line\n" +
        "       second line\n" +
        "       third line\n" +
        "       fourth line\n" +
        "       fifth line\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("first line second line third line fourth line fif ...", 3, 1) +
        "       first line\n" +
        "       second line\n" +
        "       third line\n" +
        "       fourth line\n" +
        "       fifth line\n" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testXslText() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       first line\n" +
        "       <xsl:text>text within xsl:text</xsl:text>\n" +
        "       last line" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("first line", 3, 1) +
        "       first line\n" +
        makeValueOf("<xsl:text>", 4, 2) +
        "       <xsl:text>text within xsl:text</xsl:text>\n" +
        makeValueOf("text within xsl:text", 4, 2) +
        makeValueOf("last line", 5, 1) +
        "       last line" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testProcessingInstruction() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <xsl:text><?instr param=\"val\"?></xsl:text>\n" +
        "   </xsl:template>\n" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("<xsl:text>", 3, 2) +
        "       <xsl:text><?instr param=\"val\"?></xsl:text>\n" +
        makeValueOf("<?instr param=\"val\"?>", 3, 2) +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  public void testPrefixesConflict() throws Exception {
    String originalStylesheet = "" +
        START_STYLESHEET_TAG +
        "   <xsl:template name='tpl'>\n" +
        "       <tracer:debug xmlns:tracer='tracer.uri'/>\n" +
        "       <juxy:debug xmlns:juxy='juxy.uri'/>\n" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG;
    filter(originalStylesheet);

/*
    if (TestUtil.isOracleXDK()) {
      // Oracle XML parser can create two xmlns attributes for same uri and prefix
      assertFilteredEquals("" +
          AUGMENTED_STYLESHEET_TAG +
          "   <xsl:template name='tpl' xmlns:tracer='tracer.uri' xmlns:juxy='juxy.uri'>" +
          makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
          makeValueOf("<tracer:debug xmlns:tracer=\"tracer.uri\" xmlns:tracer=\"tracer.uri\">", 3, 2) +
          "       <tracer:debug xmlns:tracer='tracer.uri'/>\n" +
          makeValueOf("<juxy:debug xmlns:juxy=\"juxy.uri\" xmlns:juxy=\"juxy.uri\">", 4, 2) +
          "       <juxy:debug xmlns:juxy='juxy.uri'/>\n" +
          "   </xsl:template>" +
          END_STYLESHEET_TAG);
    } else {
    }
*/
    assertFilteredEquals("" +
        AUGMENTED_STYLESHEET_TAG +
        "   <xsl:template name='tpl' xmlns:tracer='tracer.uri' xmlns:juxy='juxy.uri'>" +
        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
        makeValueOf("<tracer:debug xmlns:tracer=\"tracer.uri\">", 3, 2) +
        "       <tracer:debug xmlns:tracer='tracer.uri'/>\n" +
        makeValueOf("<juxy:debug xmlns:juxy=\"juxy.uri\">", 4, 2) +
        "       <juxy:debug xmlns:juxy='juxy.uri'/>\n" +
        "   </xsl:template>" +
        END_STYLESHEET_TAG);
  }

  private void assertFilteredEquals(String expectedStylesheet) throws SAXException {
    XMLComparator.assertEquals(expectedStylesheet, filteredStylesheet);
  }

  private String makeValueOf(String statement, int line, int col) throws IOException {
    String escapedStatement = StringUtil.escapeQuoteCharacter(
        StringUtil.escapeXMLText(StringUtil.replaceCharByEntityRef(statement, '\'')));
    return "<xsl:value-of select=\"Tracer:trace(" + line + ", " + col + ", '" + getSystemId() + "', '" + escapedStatement + "')\" " + tracerNS() + "/>";
  }

  private String tracerNS() {
    return "xmlns:Tracer='" + engineSupport.getJavaExtensionNamespace(Tracer.class) + "'";
  }

  private void filter(String originalStylesheet) throws Exception {
    XMLReader reader = SAXUtil.newXMLReader();
    String systemId = new File("stylesheet.xsl").getCanonicalFile().toURI().toString();
    InputSource src = TestUtil.makeInputSource(systemId, originalStylesheet);
    SAXSerializer s = new SAXSerializer();
    ByteArrayOutputStream bos = new ByteArrayOutputStream(50);
    s.setOutputStream(bos);

    TracingFilter filter = new TracingFilter(engineSupport);
    filter.setParent(reader);
    s.setParent(filter);

    s.parse(src);

    filteredStylesheet = bos.toString();
  }

  private String getSystemId() throws IOException {
    return new File("stylesheet.xsl").getCanonicalFile().toURI().toString(); /*replace('\\', '/');*/
  }
}
