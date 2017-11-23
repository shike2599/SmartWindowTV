package com.hisu.alicloud.gs.dj.webbrowser.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Java实现类似C/C++中的__FILE__、__FUNC__、__LINE__主要用于日志等功能中
 * 
 * @version 1.0 2011-07-13
 * 
 */
public abstract class CommonFunction {

	/**
	 * 打印日志时获取当前的程序文件名、行号、方法名 输出格式为：[FileName | LineNumber | MethodName]
	 * 
	 * @return
	 */
	public static String getFileLineMethod() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		StringBuffer toStringBuffer = new StringBuffer("[")
				.append(traceElement.getFileName()).append(" | ")
				.append(traceElement.getLineNumber()).append(" | ")
				.append(traceElement.getMethodName()).append("]");
		return toStringBuffer.toString();
	}

	// ��ǰ�ļ�
	public static String _FILE_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getFileName();
	}

	// ��ǰ����
	public static String _FUNC_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getMethodName();
	}

	// ��ǰ�к�
	public static int _LINE_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getLineNumber();
	}

	// ��ǰʱ��
	public static String _TIME_() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(now);
	}
}
