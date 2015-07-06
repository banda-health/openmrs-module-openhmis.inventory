<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%--
  ~ The contents of this file are subject to the OpenMRS Public License
  ~ Version 2.0 (the "License"); you may not use this file except in
  ~ compliance with the License. You may obtain a copy of the License at
  ~ http://license.openmrs.org
  ~
  ~ Software distributed under the License is distributed on an "AS IS"
  ~ basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
  ~ the License for the specific language governing rights and
  ~ limitations under the License.
  ~
  ~ Copyright (C) OpenHMIS.  All Rights Reserved.
  --%>

(function () {
	<%--Creating a map of all the keys in the bundled messages files--%>
	var messageMap = {
		<c:forEach var="key" items="${keys}">'<spring:message text='${key}' javaScriptEscape='true'/>' : '<spring:message code='${key}' javaScriptEscape='true' />',</c:forEach>
	};

	function Messages(messages) {
		this.messages = messages;
		this.getMessage = function (messageCode) {
			var result = undefined;
			<%--Check whether the message key/code passed from the javascript files is null and also check whether the message map is empty--%>
			if (messageCode != null && messages != null) {
				<%--Passing the value of specific message key/code into the variable result--%>
				result = messages[messageCode];
			}
			<%--Check whether the variable result is still undefined if so then it returns the key passed from the  javascript files--%>
			if (result === undefined) {
				result = messageCode;
			}

			return result;
		};
	}
	<%--Pass the messagemap to the funtion variable message--%>
	var messages = new Messages(messageMap);
	<%--Associating the result with the openhmis global variable to enable global access--%>
	openhmis.getMessage = messages.getMessage;
})();
