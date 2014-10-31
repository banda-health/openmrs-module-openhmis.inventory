<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>
<%--@elvariable id="sources" type="java.util.List<SafeIdentifierSource>"--%>
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
		var sourceEl = $j("#operationNumberGeneratorSourceId");

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

<c:if test="${hasIdgenModule == true}">
<form:form method="POST" modelAttribute="settings">
	<table><tr><td>
			<spring:bind path="autoGenerateOperationNumber">
				<input id="autoGenerateOperationNumber" name="${status.expression}" type="checkbox"
				       onClick="enableDisable()"
					<c:if test="${settings.autoGenerateOperationNumber}">checked</c:if> />
				<label for="autoGenerateOperationNumber">Auto Generate Operation Number</label>
			</spring:bind>
		</td></tr>
		<tr><td>
			<br />
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
	<br />
	<input type="submit" value="Save" >
</form:form>
</c:if>

<c:if test="${hasIdgenModule == false}">
There are currently no settings to change because the IDGen module is not loaded.
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp" %>
