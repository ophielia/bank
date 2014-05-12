package meg.util.spring;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.util.WebUtils;

/**
 * Based on a post by <b>stueccles</b> on forum.springframework.org.uk.
 */
public class HiddenMethodNameResolver implements
        MethodNameResolver {

    private Properties mappings;
    private String formSubmitMethod;
    private String foundMethod = null;
	private String actionparam;
    
    /**
     * Set URL to method name mappings from a Properties object.
     * 
     * @param mappings
     *            properties with submit button name as key and method name as value
     */
    public void setMappings(Properties mappings) {
        this.mappings = mappings;
    }
    
    public void setActionParameter(String paramname) {
    	this.actionparam = paramname;
    }

    public void afterPropertiesSet() {
        if (this.mappings == null || this.mappings.isEmpty()) {
            throw new IllegalArgumentException(
                    "'mappings' property is required");
        }
    }

    public String getHandlerMethodName(HttpServletRequest request)
            throws NoSuchRequestHandlingMethodException {
        String method = null;
        String submitParamter = request.getParameter(actionparam);
        method = (String) this.mappings.get(submitParamter);
        if (method == null) {
            throw new NoSuchRequestHandlingMethodException(request);
        }
        return method;
    }
    
    public boolean isFormSubmitMethod(String method) {
        return (formSubmitMethod.equals(method));
    }
    
    public final void setFormSubmitMethod(String formSubmitMethod) {
        this.formSubmitMethod = formSubmitMethod;
    }
}

