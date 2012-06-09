package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.XmlDataSetProvider;

public class WebDeleteXmlPage extends WebDeletePage {

	public WebDeleteXmlPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		setDataProvider(XmlDataSetProvider.Instance);
	}

}
