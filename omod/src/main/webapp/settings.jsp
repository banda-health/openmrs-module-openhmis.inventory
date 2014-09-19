<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>
<%--@elvariable id="sources" type="java.util.List<IdentifierSource>"--%>
<%--@elvariable id="settings" type="org.openmrs.module.openhmis.inventory.api.model.Settings"--%>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.SETTINGS_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.SETTINGS_PAGE %>" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<%@ include file="template/linksHeader.jsp"%>

<script type="text/javascript">
	function enableDisable() {
		var generateEl = $j("#autoGenerateOperationNumber");
		var sourceEl = $j("#operationNumberGeneratorSource");

		// Cannot use .prop because the jquery version we're using is ooooooold
		if (generateEl.is(':checked')) {
			sourceEl.removeAttr('disabled');
		} else {
			sourceEl.attr('disabled', 'true');
		}
	}
</script>

<h2>
	<spring:message code="openhmis.inventory.admin.settings" />
</h2>

<form:form method="POST" modelAttribute="settings">
	<table><tr><td>
			<spring:bind path="autoGenerateOperationNumber">
				<input id="autoGenerateOperationNumber" type="checkbox" onClick="enableDisable();"
					<c:if test="${settings.autoGenerateOperationNumber}">checked</c:if> />
				<label for="autoGenerateOperationNumber">Auto Generate Operation Number</label>
			</spring:bind>
		</td></tr>
		<tr><td>
			<label for="operationNumberGeneratorSourceId">Select Identifier Source</label>
			<spring:bind path="operationNumberGeneratorSourceId">
			<select id="operationNumberGeneratorSourceId" name="${status.expression}"
			        <c:if test="${!settings.autoGenerateOperationNumber}">disabled</c:if>>
				<c:forEach items="${sources}" var="source">
					<option value="${source.id}"
					        <c:if test="${settings.operationNumberGeneratorSourceId == source.id}">selected</c:if>>
							${source.name}
					</option>
				</c:forEach>
			</select>
			</spring:bind>
	</td></tr></table>
	<input type="submit" value="Save" >
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
