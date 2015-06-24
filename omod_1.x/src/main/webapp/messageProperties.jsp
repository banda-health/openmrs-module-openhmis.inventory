<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
(function () {
	var messageMap = {
		<c:forEach var="key" items="${keys}">'<spring:message text='${key}' javaScriptEscape='true'/>' : '<spring:message code='${key}' javaScriptEscape='true' />',</c:forEach>
	};

	function Messages(messages) {
		this.messages = messages;
		this.getMessage = function (messageCode) {
			var result = undefined;
			if (messageCode != null && messages != null) {
				result = messages[messageCode];
			}

			if (result === undefined) {
				result = messageCode;
			}

			return result;
		};
	}

	var messages = new Messages(messageMap);
	openhmis.inventoryMessages = messages.getMessage;
})();
