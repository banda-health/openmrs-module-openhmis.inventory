<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require allPrivileges="<%= PrivilegeWebConstants.INVENTORY_PAGE_PRIVILEGES %>"
                 otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.INVENTORY_PAGE %>" />
<openmrs:message var="pageTitle" code="openhmis.inventory.title" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp"%>

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/operations.css" %>' />

<script type="text/javascript">
  function printReport(reportId, form) {
    var timesheetId = jQuery("input[name=timesheetId]:checked").val();

    if (!timesheetId) {
      alert("You must select a timesheet to run the report.");
      return false;
    }

    var url = openhmis.url.openmrs + "<%= ModuleWebConstants.JASPER_REPORT_PAGE %>.form?"
    url += "reportId=" + reportId  + "&timesheetId=" + timesheetId;
    window.open(url, "pdfDownload");
    return false;
  }
</script>

<%@ include file="template/linksHeader.jsp"%>
<h2><spring:message code="openhmis.inventory.title" /></h2>

<c:if test="${stockTakeReport != null}" >
  <h3>${stockTakeReport.name}</h3>
  <div class="">${stockTakeReport.description}</div>
  <div>
    <form id="stockTakeReport" method="post">
      <fieldset>
        <label for="stockroomId">Stockroom: </label>
        <select id="stockroomId">
          <option value=""></option>
          <c:forEach var="stockroom" items="${stockrooms}">
            <option value="${stockroom.id}">${stockroom.name}</option>
          </c:forEach>
        </select>
        <input type="hidden" id="stockTakeReportId" value="${stockTakeReport.id}" />
      </fieldset>
      <input type="submit" value="Generate" onclick="printReport($('stockTakeReportId').val(), $('#stockTakeReport'))" />
    </form>
  </div>
  <hr>
</c:if>

<c:if test="${stockCardReport != null}" >
  <h3>${stockCardReport.name}</h3>
  <div class="">${stockCardReport.description}</div>
  <div>
    <form id="stockCardReport" method="post">
      <fieldset>
        <label for="stockroomId">Item: </label>
        <select id="stockroomId">
          <option value=""></option>
          <c:forEach var="stockroom" items="${stockrooms}">
            <option value="${stockroom.id}">${stockroom.name}</option>
          </c:forEach>
        </select>

        <label for="beginDate">Begin Date</label>
        <input type="date" id="beginDate" />

        <label for="endDate">End Date</label>
        <input type="date" id="endDate" />

        <input type="hidden" id="stockCardReportId" value="${stockCardReport.id}" />
      </fieldset>
      <input type="submit" value="Generate" onclick="printReport($('stockCardReportId').val(), $('#stockCardReport'))" />
    </form>
  </div>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp" %>
