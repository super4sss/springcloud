package com.ysd.springcloud.front.problem;
/**
 * 问题文件导入校验异常类
 */
public class ImportVerifyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImportVerifyException() {
		super();
	}
	
	public ImportVerifyException(String message) {
		super(message);
	}

	public ImportVerifyException(Throwable cause) {
		super(cause);
	}

	public ImportVerifyException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
