<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>
<%--@elvariable id="sources" type="java.util.List<SafeIdentifierSource>"--%>
<%--@elvariable id="settings" type="org.openmrs.module.openhmis.inventory.api.model.Settings"--%>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.SETTINGS_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.SETTINGS_PAGE %>" />

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/style.css" %>' />
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/style2x.css" %>' />

<%@	include file="template/customizedHeader.jsp" %>

<%@ include file="template/localHeader.jsp"%>

<%@ include file="template/customizedLinksHeader.jsp"%>

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
        <spring:message code="openhmis.inventory.admin.settings" />
    </h2>

    <form:form method="POST" modelAttribute="settings">
        <table class="settings">
            <c:if test="${hasIdgenModule == true}">
                <tr><td>
                    <spring:bind path="autoGenerateOperationNumber">
                        <input id="autoGenerateOperationNumber" name="${status.expression}" type="checkbox"
                               onClick="enableDisable()"
                               <c:if test="${settings.autoGenerateOperationNumber}">checked</c:if> />
                        <label for="autoGenerateOperationNumber"><spring:message code="openhmis.inventory.report.operation.number.label"/> </label>
                    </spring:bind>
                </td></tr>
                <tr><td>
                    <label for="operationNumberGeneratorSourceId"><spring:message code="openhmis.inventory.report.identifier.source.label"/> </label>
                    <spring:bind path="operationNumberGeneratorSourceId">
                        <select id="operationNumberGeneratorSourceId" name="${status.expression}"
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
                </td></tr>
            </c:if>
            <tr><td>
                <br />
                <spring:bind path="autoCompleteOperations">
                    <input id="autoCompleteOperations" name="${status.expression}" type="checkbox"
                           <c:if test="${settings.autoCompleteOperations}">checked</c:if> />
                    <label for="autoCompleteOperations"><spring:message code="openhmis.inventory.report.auto.complete.operation.label"/></label>
                </spring:bind>
            </td></tr>
            <tr><td>
                <br />
                <label for="stockTakeReportId"><spring:message code="openhmis.inventory.report.select.stock.take.label"/> </label>
                <spring:bind path="stockTakeReportId">
                    <select id="stockTakeReportId" name="${status.expression}">
                        <option value=""></option>
                        <c:forEach items="${reports}" var="report">
                            <option value="${report.reportId}"
                                    <c:if test="${settings.stockTakeReportId == report.reportId}">selected</c:if>>
                                    ${report.name}
                            </option>
                        </c:forEach>
                    </select>
                </spring:bind>
            </td></tr>
            <tr><td>
                <br />
                <label for="stockCardReportId"><spring:message code="openhmis.inventory.report.select.stock.card.label"/></label>
                <spring:bind path="stockCardReportId">
                    <select id="stockCardReportId" name="${status.expression}">
                        <option value=""></option>
                        <c:forEach items="${reports}" var="report">
                            <option value="${report.reportId}"
                                    <c:if test="${settings.stockCardReportId == report.reportId}">selected</c:if>>
                                    ${report.name}
                            </option>
                        </c:forEach>
                    </select>
                </spring:bind>
            </td></tr>
            <tr><td>
                <br />
                <label for="stockOperationsByStockroomReportId"><spring:message code="openhmis.inventory.report.select.stock.operation.label"/> </label>
                <spring:bind path="stockOperationsByStockroomReportId">
                    <select id="stockOperationsByStockroomReportId" name="${status.expression}">
                        <option value=""></option>
                        <c:forEach items="${reports}" var="report">
                            <option value="${report.reportId}"
                                    <c:if test="${settings.stockOperationsByStockroomReportId == report.reportId}">selected</c:if>>
                                    ${report.name}
                            </option>
                        </c:forEach>
                    </select>
                </spring:bind>
            </td></tr>
            <tr><td>
                <br />
                <label for="stockroomReportId"><spring:message code="openhmis.inventory.report.select.stockroom.label"/> </label>
                <spring:bind path="stockroomReportId">
                    <select id="stockroomReportId" name="${status.expression}">
                        <option value=""></option>
                        <c:forEach items="${reports}" var="report">
                            <option value="${report.reportId}"
                                    <c:if test="${settings.stockroomReportId == report.reportId}">selected</c:if>>
                                    ${report.name}
                            </option>
                        </c:forEach>
                    </select>
                </spring:bind>
            </td></tr>
            <tr><td>
                <br />
                <label for="expiringStockReportId"><spring:message code="openhmis.inventory.report.select.expiring.stock.label"/> </label>
                <spring:bind path="expiringStockReportId">
                    <select id="expiringStockReportId" name="${status.expression}">
                        <option value=""></option>
                        <c:forEach items="${reports}" var="report">
                            <option value="${report.reportId}"
                                    <c:if test="${settings.expiringStockReportId == report.reportId}">selected</c:if>>
                                    ${report.name}
                            </option>
                        </c:forEach>
                    </select>
                </spring:bind>
            </td></tr>
        </table>
        <br />
        <input type="submit" value="Save" >
    </form:form>
</div>
