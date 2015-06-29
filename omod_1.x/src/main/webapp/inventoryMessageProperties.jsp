<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
