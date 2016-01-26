<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%--@elvariable id="sources" type="java.util.List<SafeIdentifierSource>"--%>
<%--@elvariable id="settings" type="org.openmrs.module.openhmis.inventory.api.model.Settings"--%>

<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.SETTINGS_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.SETTINGS_PAGE %>"/>

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/style.css" %>'/>
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_COMMONS_RESOURCE_ROOT + "css/css_2.x/style2x.css" %>'/>
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_COMMONS_RESOURCE_ROOT + "css/css_2.x/bootstrap.css" %>' />
<%@ include file="/WEB-INF/view/module/openhmis/commons/template/common/customizedHeader.jsp"%>

<%@ include file="template/localHeader.jsp" %>

<%@ include file="template/customizedLinksHeader.jsp" %>

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

<div id="body-wrapper">
	<h2>
		<spring:message code="openhmis.inventory.admin.settings"/>
	</h2>

	<form:form method="POST" modelAttribute="settings">
		<table class="table table-bordered table-striped">
			<c:if test="${hasIdgenModule == true}">
				<tr>
					<td>
						<spring:bind path="autoGenerateOperationNumber">
							<input id="autoGenerateOperationNumber" name="${status.expression}"
							       type="checkbox"
							       onClick="enableDisable()"
							       <c:if test="${settings.autoGenerateOperationNumber}">checked</c:if> />
							<label class="labelremovebold" for="autoGenerateOperationNumber"><spring:message
									code="openhmis.inventory.report.operation.number.label"/> </label>
						</spring:bind>
					</td>
					<td></td>
				</tr>
				<tr>
					<td>
						<label class="labelremovebold" for="operationNumberGeneratorSourceId"><spring:message
								code="openhmis.inventory.report.identifier.source.label"/> </label>
					</td>
					<td>
						<spring:bind path="operationNumberGeneratorSourceId">
							<select class="form-control" id="operationNumberGeneratorSourceId" name="${status.expression}"
							        <c:if test="${!settings.autoGenerateOperationNumber}">disabled</c:if>>
								<option value=""></option>
								<c:forEach items="${sources}" var="source">
									<option value="${source.id}"
									        <c:if test="${settings.operationNumberGeneratorSourceId == source.id}">selected</c:if>>
											${source.name}
									</option>
								</c:forEach>
							</select>
						</spring:bind>
					</td>
				</tr>
			</c:if>
			<tr>
				<td>
					<br/>
					<spring:bind path="autoCompleteOperations">
						<input id="autoCompleteOperations" name="${status.expression}" type="checkbox"
						       <c:if test="${settings.autoCompleteOperations}">checked</c:if> />
						<label class="labelremovebold"  for="autoCompleteOperations"><spring:message
								code="openhmis.inventory.report.auto.complete.operation.label"/></label>
					</spring:bind>
				</td>
				<td></td>
			</tr>
			<tr>
				<td>
					<br/>
					<label class="labelremovebold"  for="stockTakeReportId"><spring:message
							code="openhmis.inventory.report.select.stock.take.label"/> </label>
				</td>
				<td>
					<spring:bind path="stockTakeReportId">
						<select class="form-control" id="stockTakeReportId" name="${status.expression}">
							<option value=""></option>
							<c:forEach items="${reports}" var="report">
								<option value="${report.reportId}"
								        <c:if test="${settings.stockTakeReportId == report.reportId}">selected</c:if>>
										${report.name}
								</option>
							</c:forEach>
						</select>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td>
					<br/>
					<label  class="labelremovebold" for="stockCardReportId"><spring:message
							code="openhmis.inventory.report.select.stock.card.label"/></label>
				</td>
				<td>
					<spring:bind path="stockCardReportId">
						<select class="form-control" id="stockCardReportId" name="${status.expression}">
							<option value=""></option>
							<c:forEach items="${reports}" var="report">
								<option value="${report.reportId}"
								        <c:if test="${settings.stockCardReportId == report.reportId}">selected</c:if>>
										${report.name}
								</option>
							</c:forEach>
						</select>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td>
					<br/>
					<label  class="labelremovebold" for="stockOperationsByStockroomReportId"><spring:message
							code="openhmis.inventory.report.select.stock.operation.label"/> </label>
				</td>
				<td>
					<spring:bind path="stockOperationsByStockroomReportId">
						<select class="form-control" id="stockOperationsByStockroomReportId" name="${status.expression}">
							<option value=""></option>
							<c:forEach items="${reports}" var="report">
								<option value="${report.reportId}"
								        <c:if test="${settings.stockOperationsByStockroomReportId == report.reportId}">selected</c:if>>
										${report.name}
								</option>
							</c:forEach>
						</select>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td>
					<br/>
					<label  class="labelremovebold" for="stockroomReportId"><spring:message
							code="openhmis.inventory.report.select.stockroom.label"/> </label>
				</td>
				<td>
					<spring:bind path="stockroomReportId">
						<select class="form-control" id="stockroomReportId" name="${status.expression}">
							<option value=""></option>
							<c:forEach items="${reports}" var="report">
								<option value="${report.reportId}"
								        <c:if test="${settings.stockroomReportId == report.reportId}">selected</c:if>>
										${report.name}
								</option>
							</c:forEach>
						</select>
					</spring:bind>
				</td>
			</tr>
			<tr>
				<td>
					<br/>
					<label  class="labelremovebold" for="expiringStockReportId"><spring:message
							code="openhmis.inventory.report.select.expiring.stock.label"/> </label>
				</td>
				<td>
					<spring:bind path="expiringStockReportId">
						<select class="form-control" id="expiringStockReportId" name="${status.expression}">
							<option value=""></option>
							<c:forEach items="${reports}" var="report">
								<option value="${report.reportId}"
								        <c:if test="${settings.expiringStockReportId == report.reportId}">selected</c:if>>
										${report.name}
								</option>
							</c:forEach>
						</select>
					</spring:bind>
				</td>
			</tr>
		</table>
		<br/>
		<p><input class="submitButton confirm right" value="Save" type="submit" value="<openmrs:message code="Role.save"/>">
		</p>
	</form:form>
</div>
