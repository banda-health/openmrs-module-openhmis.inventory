<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require allPrivileges="<%= PrivilegeWebConstants.INVENTORY_PAGE_PRIVILEGES %>"
                 otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.INVENTORY_PAGE %>" />
<openmrs:message var="pageTitle" code="openhmis.inventory.title" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript">
  function printTakeReport() {
    var stockroomId = jQuery("#stockroomId").val();
    if (!stockroomId) {
      alert("You must select a stockroom to generate the report.");
      return false;
    }

    var reportId = jQuery('#stockTakeReportId').val();

    return printReport(reportId, "stockroomId=" + stockroomId);
  }

  function printCardReport() {
    var stockroomId = jQuery("input[name=stockroomId]").val();
    if (!stockroomId) {
      alert("You must select a stockroom to generate the report.");
      return false;
    }

    var reportId = jQuery('#stockCardReportId').val();

    return printReport(reportId, "stockroomId=" + stockroomId);
  }

  function printReport(reportId, parameters) {
    var url = openhmis.url.openmrs + "<%= ModuleWebConstants.JASPER_REPORT_PAGE %>.form?";
    url += "reportId=" + reportId  + "&" + parameters;
    window.open(url, "pdfDownload");

    return false;
  }
</script>

<%@ include file="template/linksHeader.jsp"%>
<h2><spring:message code="openhmis.inventory.admin.reports" /></h2>

<c:if test="${stockTakeReport != null}" >
  <h3>${stockTakeReport.name}</h3>
  <div class="">${stockTakeReport.description}</div>
  <div>
    <form id="stockTakeReport" onsubmit="return false;">
      <fieldset>
        <label for="stockroomId">Stockroom: </label>
        <select id="stockroomId">
          <option value=""></option>
          <c:forEach var="stockroom" items="${stockrooms}">
            <option value="${stockroom.id}">${stockroom.name}</option>
          </c:forEach>
        </select>
        <input type="hidden" id="stockTakeReportId" value="${stockTakeReport.reportId}" />
        <br /><br />
        <input type="submit" value="Generate" onclick="printTakeReport()" />
      </fieldset>
    </form>
  </div>
  <br />
  <hr>
</c:if>

<c:if test="${stockCardReport != null}" >
  <h3>${stockCardReport.name}</h3>
  <div class="">${stockCardReport.description}</div>
  <div>
    <form id="stockCardReport" onsubmit="return false;">
      <fieldset>
        <label for="beginDate">Begin Date</label>
        <input type="date" id="beginDate" />

        <label for="endDate">End Date</label>
        <input type="date" id="endDate" />

        <input type="hidden" id="stockCardReportId" value="${stockCardReport.reportId}" />
        <br /><br />
        <input type="submit" value="Generate" onclick="printCardReport()" />
      </fieldset>
    </form>
  </div>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp" %>
