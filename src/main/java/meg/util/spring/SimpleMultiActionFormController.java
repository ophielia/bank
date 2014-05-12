package meg.util.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;

/**
 * Based on a post by <b>stueccles</b> on forum.springframework.org.uk.
 * 
 * This class can be extended to provide support for multiple submit buttons
 * on a simple form. Particularly useful for many-to-one relationships with the 
 * object being edited.
 */
public abstract class SimpleMultiActionFormController extends AbstractFormController {
    private String formView;
    private String successView;
    private HiddenMethodNameResolver methodNameResolver; 
    
    // getters and setters (for spring wiring)
    public final String getFormView() {
        return formView;
    }

    public final void setFormView(String formView) {
        this.formView = formView;
    }
    
    public final void setMethodNameResolver(
            HiddenMethodNameResolver methodNameResolver) {
        this.methodNameResolver = methodNameResolver;
    }

    public final String getSuccessView() {
        return successView;
    }

    public final void setSuccessView(String successView) {
        this.successView = successView;
    }
	    
    /**
     * This method looks for validators/binders for the action by appending the
     * word Validator onto the method name. 
     * 
     * e.g. If the properties file defined a method foo for submit button this 
     * routine will look for the method:
     *
     *       public void fooValidator(HttpServletRequest request,
     *                Object command, BindException errors);
     */
    protected final void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

        defaultBindAndValidate(request,command,errors);
        
        String methodName = this.methodNameResolver.getHandlerMethodName(request) + "Validator";
        
        Method m;
        try {
            m = (Method) this.getClass().getMethod(methodName,
                  new Class[] {
            				HttpServletRequest.class,
            				Object.class,
                            BindException.class});
        } catch (SecurityException e) {
            throw new ServletException(e);
        } catch (NoSuchMethodException e) {
            // fine 
            return;
        }
        Object[] params = new Object[3];
        params[0]=request;
        params[1]=command;
        params[2]=errors;
        try {
            m.invoke(this, params);
        } catch (IllegalArgumentException e) {
            throw new ServletException("The method: " + methodName + "Validator"
                    + " should be in form 'public void fooValidator(HttpServletRequest req, Object command, BindException errors)'",e);
        } catch (IllegalAccessException e) {
            throw new ServletException(e);
        } catch (InvocationTargetException e) {
        	throw new ServletException("The method " + methodName 
            		+ " blew up with: " + e.getCause().getMessage(), e.getCause());
        }
    }
    
    /**
     * Subclasses can override this to provide cross action/method validation.
     * @param request
     * @param command
     * @param errors
     */
    protected void defaultBindAndValidate(HttpServletRequest request, Object command, BindException errors) {
        // do nothing
    }
    
    /**
     * Subclasses must implement the methods setup to respond the the
     * various submit buttons.
     * 
     * Consider the following config file:
     * 
     *  <bean id="submitActionParamResolver"
     *     class="com.foo.SubmitMethodNameResolver"> 
     *    <property name="mappings">
     *      <props> 
     *        <prop key="_addOrganisation">addOrganisationSubmit</prop> 
     *        <prop key="_addCapability">addCapabilitySubmit</prop> 
     *        <prop key="_finish">finalSubmit</prop> 
     *      </props> 
     *    </property> 
     *    <property name="formSubmitMethod''>
     *      <value>finalSubmit</value>
     *    </property> 
     * </bean>  
     * 
     * The subclass would need to contain the methods:
     *    public void addOrganisationSubmit(Object command, BindException errors)
     *    public void addCapabilitySubmit(Object command, BindException errors)
     * 
     * The one method that actually submits the form (the formSubmittedMethod) calls the  
     * onSubmit method as per the SimpleFormController.
     */ 
    protected final ModelAndView processFormSubmission(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        if (errors.hasErrors()) {
            if (logger.isDebugEnabled()) {
               logger.debug("Data binding errors: " + errors.getErrorCount());
            }
         }
         else {
            String methodName = this.methodNameResolver.getHandlerMethodName(request);
            if (this.methodNameResolver.isFormSubmitMethod(methodName)) {
            	return onSubmit(request,response,command,errors);
            }
            Method m;
            try {
                m = (Method) this.getClass().getMethod(methodName,
                      new Class[] {Object.class,
                                BindException.class});
            } catch (SecurityException e) {
                throw new ServletException(e);
            } catch (NoSuchMethodException e) {
                throw new ServletException("Please implement the method " + methodName);
            }
            Object[] params = new Object[2];
            params[0]=command;
            params[1]=errors;
            try {
                m.invoke(this, params);
            } catch (IllegalArgumentException e) {
                throw new ServletException("The method: " + methodName 
                        + " should be in form 'public void foo(Object command, BindException errors)'",e);
            } catch (IllegalAccessException e) {
                throw new ServletException(e);
            } catch (InvocationTargetException e) {
                throw new ServletException("The method " + methodName 
                		+ " blew up with: " + e.getCause().getMessage(), e.getCause());
            }
         } 
        return showForm(request,response,errors);
    }

    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {
    	return showForm(request, errors, getFormView());
    }

    abstract protected ModelAndView onSubmit(
			HttpServletRequest request,	HttpServletResponse response, Object command,	BindException errors)
			throws Exception;
}