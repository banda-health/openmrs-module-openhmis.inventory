<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%--@elvariable id="sources" type="java.util.List<SafeIdentifierSource>"--%>
<%--@elvariable id="settings" type="org.openmrs.module.openhmis.inventory.api.model.Settings"--%>

<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require allPrivileges="<%= PrivilegeConstants.TASK_MANAGE_INVENTORY_METADATA %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.SETTINGS_2X_PAGE %>"/>
<openmrs:htmlInclude file='<%= request.getContextPath() + ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/style.css" %>'/>
<openmrs:htmlInclude file='<%= request.getContextPath() + ModuleWebConstants.MODULE_COMMONS_RESOURCE_ROOT + "css/css_2.x/style2x.css" %>'/>
<openmrs:htmlInclude file='<%= request.getContextPath() + ModuleWebConstants.MODULE_COMMONS_RESOURCE_ROOT + "styles/bootstrap.css" %>' />
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
		<table class="table table-bordered table-striped removeBold">
			<c:if test="${hasIdgenModule == true}">
				<tr>
					<td>
						<spring:bind path="autoGenerateOperationNumber">
							<input id="autoGenerateOperationNumber" name="${status.expression}"
							       type="checkbox"
							       onClick="enableDisable()"
							       <c:if test="${settings.autoGenerateOperationNumber}">checked</c:if> />
							<label class="removeBold" for="autoGenerateOperationNumber"><spring:message
									code="openhmis.inventory.report.operation.number.label"/> </label>
						</spring:bind>
					</td>
					<td></td>
				</tr>
				<tr>
					<td>
						<label class="removeBold" for="operationNumberGeneratorSourceId"><spring:message
								code="openhmis.inventory.report.identifier.source.label"/> </label>
					</td>
					<td>
						<spring:bind path="operationNumberGeneratorSourceId">
							<select class="form-control" id="operationNumberGeneratorSourceId" name="${status.expression}" style="height:27px;padding:0px 0px;"
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
					<spring:bind path="autoCompleteOperations">
						<input id="autoCompleteOperations" name="${status.expression}" type="checkbox"
						       <c:if test="${settings.autoCompleteOperations}">checked</c:if> />
						<label class="removeBold"  for="autoCompleteOperations"><spring:message
								code="openhmis.inventory.report.auto.complete.operation.label"/></label>
					</spring:bind>
				</td>
				<td></td>
			</tr>
			<tr>
				<td>
					<label class="removeBold"  for="stockTakeReportId"><spring:message
							code="openhmis.inventory.report.select.stock.take.label"/> </label>
				</td>
				<td>
					<spring:bind path="stockTakeReportId">
						<select class="form-control" id="stockTakeReportId" name="${status.expression}" style="height:27px;padding:0px 0px;">
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
					<label  class="removeBold" for="stockCardReportId"><spring:message
							code="openhmis.inventory.report.select.stock.card.label"/></label>
				</td>
				<td>
					<spring:bind path="stockCardReportId">
						<select class="form-control" id="stockCardReportId" name="${status.expression}" style="height:27px;padding:0px 0px;">
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
					<label  class="removeBold" for="stockOperationsByStockroomReportId"><spring:message
							code="openhmis.inventory.report.select.stock.operation.label"/> </label>
				</td>
				<td>
					<spring:bind path="stockOperationsByStockroomReportId">
						<select class="form-control" id="stockOperationsByStockroomReportId" name="${status.expression}" style="height:27px;padding:0px 0px;">
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
					<label  class="removeBold" for="stockroomReportId"><spring:message
							code="openhmis.inventory.report.select.stockroom.label"/> </label>
				</td>
				<td>
					<spring:bind path="stockroomReportId">
						<select class="form-control" id="stockroomReportId" name="${status.expression}" style="height:27px;padding:0px 0px;">
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
					<label  class="removeBold" for="expiringStockReportId"><spring:message
							code="openhmis.inventory.report.select.expiring.stock.label"/> </label>
				</td>
				<td>
					<spring:bind path="expiringStockReportId">
						<select class="form-control" id="expiringStockReportId" name="${status.expression}" style="height:27px;padding:0px 0px;">
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
			<tr>
				<td>
					<spring:bind path="autoSelectItemStockFurthestExpirationDate">
						<input id="autoSelectItemStockFurthestExpirationDate" name="${status.expression}" type="checkbox"
							   <c:if test="${settings.autoSelectItemStockFurthestExpirationDate}">checked</c:if> />
						<label class="removeBold" for="autoSelectItemStockFurthestExpirationDate">
							<spring:message code="openhmis.inventory.settings.autoSelectItemStockFurthestExpirationDate"/>
						</label>
					</spring:bind>
				</td>
				<td></td>
			</tr>
		</table>
		<br/>
		<p><input class="submitButton confirm right" type="submit" value="<spring:message code="openhmis.inventory.settings.page.settings.save"/>">
		</p>
	</form:form>
</div>
