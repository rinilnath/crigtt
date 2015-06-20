package gov.hhs.onc.crigtt.validate;

import gov.hhs.onc.crigtt.beans.NamedBean;
import gov.hhs.onc.crigtt.xml.impl.XdmDocument;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Source;
import net.sf.saxon.s9api.XsltExecutable;
import org.springframework.beans.factory.InitializingBean;

public interface ValidatorSchematron extends NamedBean, InitializingBean {
    public XdmDocument transform(Source docSrc) throws Exception;

    public Map<String, List<ValidatorAssertion>> getActiveAssertions();

    public Map<String, List<ValidatorPattern>> getActivePatterns();
    
    public List<ValidatorPhase> getActivePhases();

    public Map<String, List<ValidatorRule>> getActiveRules();
    
    public ValidatorSchema getActiveSchema();

    public Map<String, ?> getParameters();

    public void setParameters(Map<String, ?> params);

    public String getQueryBinding();

    public void setQueryBinding(String queryBinding);

    public Map<String, Source> getReferencedDocuments();

    public void setReferencedDocuments(Map<String, Source> referencedDocs);

    public String getSchemaVersion();

    public void setSchemaVersion(String schemaVersion);

    public Source getSource();

    public void setSource(Source src);

    public XsltExecutable[] getXsltExecutables();

    public void setXsltExecutables(XsltExecutable ... xsltExecs);
}
