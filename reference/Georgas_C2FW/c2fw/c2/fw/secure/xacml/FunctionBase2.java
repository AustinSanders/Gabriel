/*
 * Created on Jul 14, 2005
 *
 */
package c2.fw.secure.xacml;

import com.sun.xacml.cond.FunctionBase;

abstract public class FunctionBase2 extends FunctionBase {
    /**
     * The standard namespace where all the spec-defined functions live
     */
    public static final String FUNCTION_NS_2 =
        "urn:oasis:names:tc:xacml:2.0:function:";
    
    /**
     * The function name of the string_equal function
     */
    public static final String SHORT_NAME_STRING_EQUAL = "string_equal";
    /**
     * The full name of the string_equal function
     */
    public static final String NAME_STRING_EQUAL = FUNCTION_NS + SHORT_NAME_STRING_EQUAL;
    
    /**
     * The function name of the string_one_and_only function
     */
    public static final String SHORT_NAME_STRING_ONE_AND_ONLY = "string_one_and_only";
    /**
     * The full name of the string_one_and_only function
     */
    public static final String NAME_STRING_ONE_AND_ONLY = FUNCTION_NS + SHORT_NAME_STRING_ONE_AND_ONLY;
    
    /**
     * The function name of the string_regexp_match function
     */
    public static final String SHORT_NAME_STRING_REGEXP_MATCH = "string_regexp_match";
    /**
     * The full name of the string_regexp_match function
     */
    public static final String NAME_STRING_REGEXP_MATCH = FUNCTION_NS + SHORT_NAME_STRING_REGEXP_MATCH;
    
    /**
     * Constructor that sets up the function as having some number of
     * parameters all of the same given type. If <code>numParams</code> is
     * -1, then the length is variable
     *
     * @param functionName the name of this function as used by the factory
     *                     and any XACML policies
     * @param functionId an optional identifier that can be used by your
     *                   code for convenience
     * @param paramType the type of all parameters to this function, as used
     *                  by the factory and any XACML documents
     * @param paramIsBag whether or not every parameter is actually a bag
     *                   of values
     * @param numParams the number of parameters required by this function,
     *                  or -1 if any number are allowed
     * @param returnType the type returned by this function, as used by
     *                   the factory and any XACML documents
     * @param returnsBag whether or not this function returns a bag of values
     */
    public FunctionBase2(String functionName, int functionId, String paramType,
                        boolean paramIsBag, int numParams,
                        String returnType, boolean returnsBag) {
    	super(functionName, functionId, paramType, paramIsBag, numParams, 
    			returnType, returnsBag);
    }

    /**
     * Constructor that sets up the function as having some number of
     * parameters all of the same given type. If <code>numParams</code> is
     * -1, then the length is variable, and then <code>minParams</code> may
     * be used to specify a minimum number of parameters. If
     * <code>numParams</code> is not -1, then <code>minParams</code> is
     * ignored.
     *
     * @param functionName the name of this function as used by the factory
     *                     and any XACML policies
     * @param functionId an optional identifier that can be used by your
     *                   code for convenience
     * @param paramType the type of all parameters to this function, as used
     *                  by the factory and any XACML documents
     * @param paramIsBag whether or not every parameter is actually a bag
     *                   of values
     * @param numParams the number of parameters required by this function,
     *                  or -1 if any number are allowed
     * @param minParams the minimum number of parameters required if 
     *                  <code>numParams</code> is -1
     * @param returnType the type returned by this function, as used by
     *                   the factory and any XACML documents
     * @param returnsBag whether or not this function returns a bag of values
     */
    public FunctionBase2(String functionName, int functionId, String paramType,
                        boolean paramIsBag, int numParams, int minParams,
                        String returnType, boolean returnsBag) {
    	super(functionName, functionId, paramType, paramIsBag, numParams, 
    			minParams, returnType, returnsBag);
    }


    /**
     * Constructor that sets up the function as having different types for
     * each given parameter.
     *
     * @param functionName the name of this function as used by the factory
     *                     and any XACML policies
     * @param functionId an optional identifier that can be used by your
     *                   code for convenience
     * @param paramTypes the type of each parameter, in order, required by
     *                   this function, as used by the factory and any XACML
     *                    documents
     * @param paramIsBag whether or not each parameter is actually a bag
     *                   of values
     * @param returnType the type returned by this function, as used by
     *                   the factory and any XACML documents
     * @param returnsBag whether or not this function returns a bag of values
     */
    public FunctionBase2(String functionName, int functionId,
                        String [] paramTypes, boolean [] paramIsBag,
                        String returnType, boolean returnsBag) {
    	super(functionName, functionId, paramTypes, paramIsBag, returnType, returnsBag);
    }

    /**
     * Constructor that sets up some basic values for functions that will
     * take care of parameter checking on their own. If you use this
     * constructor for your function class, then you must override the
     * two check methods to make sure that parameters are correct.
     *
     * @param functionName the name of this function as used by the factory
     *                     and any XACML policies
     * @param functionId an optional identifier that can be used by your
     *                   code for convenience
     * @param returnType the type returned by this function, as used by
     *                   the factory and any XACML documents
     * @param returnsBag whether or not this function returns a bag of values
     */
    public FunctionBase2(String functionName, int functionId,
                        String returnType, boolean returnsBag) {
        super(functionName, functionId, returnType, returnsBag);
    }
}
