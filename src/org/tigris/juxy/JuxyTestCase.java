package org.tigris.juxy;

import junit.framework.TestCase;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.validator.ValidationFailedException;
import org.tigris.juxy.xpath.XPathAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.URIResolver;

/**
 * <p/>
 *
 * @author Pavel Sher
 */
public abstract class JuxyTestCase extends TestCase {
  private JuxyAdapter delegate = new JuxyAdapter();

  protected JuxyTestCase() {
  }

  protected JuxyTestCase(String name) {
    super(name);
  }

  /**
   * Creates a new RunnerContext object. RunnerContext holds all information required
   * for calling / applying templates.
   *
   * @param systemId system id of the stylesheet (path to a stylesheet file)
   * @return new RunnerContext object
   */
  public RunnerContext newContext(String systemId) {
    return delegate.newContext(systemId);
  }

  /**
   * Creates a new RunnerContext object. RunnerContext holds all information required
   * for calling / applying templates.
   *
   * @param systemId system id of the stylesheet
   * @param resolver URIResolver to use for URI resolution during transformation
   * @return new RunnerContext object
   */
  public RunnerContext newContext(String systemId, URIResolver resolver) {
    return delegate.newContext(systemId, resolver);
  }

  /**
   * Returns current RunnerContext object.
   *
   * @return current RunnerContext object
   */
  public RunnerContext context() {
    return delegate.context();
  }

  /**
   * Sets RunnerContext object to use as the current context.
   * @param context
   */
  public void setContext(RunnerContext context) {
    delegate.setContext(context);
  }

  /**
   * Creates new XPathExpr object.
   *
   * @param xpathExpr an XPath expression
   * @return new XPathExpr object
   * @throws Exception
   */
  public XPathExpr xpath(String xpathExpr) throws Exception {
    return delegate.xpath(xpathExpr);
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext)}
   */
  public Node applyTemplates() throws Exception {
    return delegate.applyTemplates();
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext,org.tigris.juxy.xpath.XPathExpr)}
   */
  public Node applyTemplates(XPathExpr xpath) throws Exception {
    return delegate.applyTemplates(xpath);
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext,org.tigris.juxy.xpath.XPathExpr,String)}
   */
  public Node applyTemplates(XPathExpr xpath, String mode) throws Exception {
    return delegate.applyTemplates(xpath, mode);
  }

  /**
   * See {@link Runner#callTemplate(RunnerContext,String)}
   */
  public Node callTemplate(String name) throws Exception {
    return delegate.callTemplate(name);
  }

  /**
   * Asserts two documents are equal. Meaningless spaces will be ignored during this assertion.
   *
   * @param expected XML document which is expected
   * @param actual   document root node of actual transformation result
   * @throws Exception
   */
  public void assertXMLEquals(Node expected, Node actual) throws Exception {
    delegate.assertXMLEquals(expected, actual);
  }

  /**
   * Asserts two documents are equal. Meaningless spaces will be ignored during this assertion.
   *
   * @param expectedDocument XML document which is expected
   * @param actual           document root node of actual transformation result
   * @throws Exception
   */
  public void assertXMLEquals(String expectedDocument, Node actual) throws Exception {
    delegate.assertXMLEquals(expectedDocument, actual);
  }

  /**
   * Asserts two documents are equal. Meaningless spaces will be ignored during this assertion.
   *
   * @param expectedDocument XML document which is expected
   * @param actualDocument   actual xml document
   * @throws Exception
   */
  public void assertXMLEquals(String expectedDocument, String actualDocument) throws Exception {
    delegate.assertXMLEquals(expectedDocument, actualDocument);
  }

  /**
   * See {@link StringUtil#normalizeSpaces(String)}
   *
   * @param str string to normalize
   * @return normalized string
   */
  public String normalizeSpaces(String str) {
    return delegate.normalizeSpaces(str);
  }

  /**
   * See {@link StringUtil#normalizeAll(String)}
   *
   * @param str string to normalize
   * @return normalized string
   */
  public String normalizeAll(String str) {
    return delegate.normalizeAll(str);
  }

  /**
   * Prints fragment of the document to System.out starting from the specified node.
   *
   * @param node node to display
   * @throws Exception
   */
  public void print(Node node) throws Exception {
    delegate.print(node);
  }

  /**
   * Serializes fragment of the document to String, starting from the specified node.
   *
   * @param node node to display
   * @return xml document corresponding to the specified node
   * @throws Exception
   */
  public String asString(Node node) throws Exception {
    return delegate.asString(node);
  }

  /**
   * Parses specified string into org.w3c.dom.Document.
   *
   * @param document xml document
   * @return DOM Document
   * @throws Exception
   */
  public Document parse(String document) throws Exception {
    return delegate.parse(document);
  }

  /**
   * See {@link Runner#enableTracing()}.
   */
  public void enableTracing() {
    delegate.enableTracing();
  }

  /**
   * See {@link Runner#disableTracing()}.
   */
  public void disableTracing() {
    delegate.disableTracing();
  }

  /**
   * Validates specified node using XML schema with specified path.
   *
   * @param node         node to validate
   * @param pathToSchema path to W3C XML schema
   * @throws org.tigris.juxy.validator.ValidationFailedException
   */
  public void validateWithSchema(Node node, String pathToSchema) throws ValidationFailedException {
    delegate.validateWithSchema(node, pathToSchema);
  }

  /**
   * Evaluates XPath assertions.
   *
   * @param node       node to evaluate assertions on
   * @param assertions XPath assertions to evaluate
   * @throws XPathExpressionException
   * @throws AssertionError
   */
  public void evalAssertions(Node node, XPathAssert[] assertions) throws XPathExpressionException, AssertionError {
    delegate.evalAssertions(node, assertions);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String)}
   */
  public XPathAssert xpathAssert(String xpathExpr) {
    return delegate.xpathAssert(xpathExpr);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,int)}
   */
  public XPathAssert xpathAssert(String xpathExpr, int expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,boolean)}
   */
  public XPathAssert xpathAssert(String xpathExpr, boolean expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,String)}
   */
  public XPathAssert xpathAssert(String xpathExpr, String expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,String,boolean)}
   */
  public XPathAssert xpathAssert(String xpathExpr, String expectedResult, boolean normalize) {
    return delegate.xpathAssert(xpathExpr, expectedResult, normalize);
  }

  /**
   * See {@link org.tigris.juxy.xpath.XPathAssert#XPathAssert(String,double,double)}
   */
  public XPathAssert xpathAssert(String xpathExpr, double expectedResult, double error) {
    return delegate.xpathAssert(xpathExpr, expectedResult, error);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,Node)}
   */
  public XPathAssert xpathAssert(String xpathExpr, Node expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }
}
