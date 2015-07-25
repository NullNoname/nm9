package cx.it.nullpo.nm9.engine.script;

import java.io.IOException;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import cx.it.nullpo.nm9.engine.common.NEUtil;

/**
 * NullpoMino Script Host - Rhino (JavaScript)
 * @author NullNoname
 */
public class RhinoScriptHost extends ScriptHost {
	protected ContextFactory cxf;
	protected Context cx;
	protected ScriptableObject scope;

	/**
	 * Create a script host using the optimization level of 9 (or -1 in Android)
	 */
	public RhinoScriptHost() {
		this(NEUtil.isAndroid() ? -1 : 9);
	}

	/**
	 * Create a script host
	 * @param optimizationLevel Rhino optimization level (Valid range is -1 between 9, must be -1 on Android)
	 */
	public RhinoScriptHost(int optimizationLevel) {
		cxf = new ContextFactory();
		cx = cxf.enterContext();
		cx.setOptimizationLevel(optimizationLevel);
		scope = cx.initStandardObjects();
	}

	@Override
	public void shutdown() {
		Context.exit();
	}

	public ContextFactory getContextFactory() {
		return cxf;
	}

	public Context getContext() {
		return cx;
	}

	public ScriptableObject getScope() {
		return scope;
	}

	@Override
	public Object call(String funcName, Object... args) throws InvalidFunctionException {
		Object funcObj = scope.get(funcName, scope);

		if(funcObj == null || funcObj == Scriptable.NOT_FOUND) {
			throw new NoSuchFunctionException("Function '" + funcName + "' doesn't exist");
		}
		if(!(funcObj instanceof Function)) {
			throw new InvalidFunctionException("'" + funcName + "' is not a function");
		}

		Function func = (Function)funcObj;
		return func.call(cx, scope, scope, args);
	}

	@Override
	public boolean callBoolean(String funcName, Object... args) {
		try {
			return Context.toBoolean(call(funcName, args));
		} catch (InvalidFunctionException e) {
			return false;
		}
	}

	@Override
	public double callDouble(String funcName, Object... args) {
		try {
			return Context.toNumber(call(funcName, args));
		} catch (InvalidFunctionException e) {
			return Double.NaN;
		}
	}

	@Override
	public Object evalString(String source, String sourceName, int lineno) {
		return cx.evaluateString(scope, source, sourceName, lineno, null);
	}

	@Override
	public Object evalReader(Reader reader, String sourceName, int lineno) throws IOException {
		return cx.evaluateReader(scope, reader, sourceName, lineno, null);
	}

	@Override
	public Object get(String varName) {
		return scope.get(varName, scope);
	}

	@Override
	public void set(String varName, Object value) {
		scope.put(varName, scope, value);
	}

	@Override
	public boolean has(String varName) {
		return scope.has(varName, scope);
	}

	@Override
	public boolean hasFunction(String funcName) {
		return (get(funcName) instanceof Function);
	}
}
