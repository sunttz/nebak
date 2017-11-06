package usi.sys.key.util;

import org.springframework.core.NestedRuntimeException;

public class UniqueException  extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public UniqueException(String msg) {
		super(msg);
	}

	public UniqueException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
