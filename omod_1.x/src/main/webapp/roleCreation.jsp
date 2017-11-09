<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%--@elvariable id="roles" type="java.util.List<org.openmrs.Role>"--%>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.MANAGE_METADATA %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.ROLE_CREATION_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/linksHeader.jsp"%>

<script type="text/javascript">
	function enableDisable() {
		var radioAdd = $j('#addPriv');
		var radioRemove = $j('#removePriv');
		var radioNew = $j('#newRole');
		var add = $j('#addToRole');
		var remove = $j('#removeFromRole');
		var newRole = $j('#newRoleName');

		if (radioAdd.checked) {
			add.disabled=false;
			remove.disabled=true;
			newRole.value='';
			newRole.disabled=true;
		} else if (radioRemove.checked) {
			add.disabled=true;
			remove.disabled=false;
			newRole.value='';
			newRole.disabled=true;
		} else if (radioNew.checked) {
			add.disabled=true;
			remove.disabled=true;
			newRole.disabled=false;
		}
	}
</script>

<spring:hasBindErrors name="role">
	<openmrs:message code="fix.error" htmlEscape="false"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<openmrs:message code="${error.code}" text="${error.defaultMessage}"/><br/>
		</c:forEach>
	</div>
	<br />
</spring:hasBindErrors>

<h2>
	<spring:message code="openhmis.inventory.admin.role" />
</h2>

<p>
	<spring:message code="openhmis.inventory.roleCreation.page.instruction" />
</p>

<form method="post">
	<table>
		<tr>
			<td>
				<input id="addPriv" type="radio" value="add" name="action" onClick="enableDisable();" />
				<label for="addPriv"><spring:message code="openhmis.inventory.roleCreation.page.label.add" /></label>
			</td>
			<td>
				<select id="addToRole" name="addToRole">
					<c:forEach items="${roles}" var="role">
						<option value="${role.uuid}">${role.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<input id="removePriv" type="radio" value="remove" name="action" onClick="enableDisable();" />
				<label for="removePriv"><spring:message code="openhmis.inventory.roleCreation.page.label.remove" /></label>
			</td>
			<td>
				<select id="removeFromRole" name="removeFromRole">
					<c:forEach items="${roles}" var="role">
						<option value="${role.uuid}">${role.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<input id="newRole" type="radio" value="new" name="action" checked onClick="enableDisable();" />
				<label for="newRole"><spring:message code="openhmis.inventory.roleCreation.page.label.new" /></label>
			</td>
			<td>
				<input id="newRoleName" name="newRoleName" type="text" />
			</td>
		</tr>
	</table>

	<p><input type="submit" value="<openmrs:message code="Role.save"/>"></p>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
