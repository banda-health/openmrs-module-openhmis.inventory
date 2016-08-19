<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require allPrivileges="<%= PrivilegeWebConstants.INVENTORY_PAGE_PRIVILEGES %>"
                 otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.INVENTORY_REPORTS_PAGE %>" />
<openmrs:message var="pageTitle" code="openhmis.inventory.title" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp"%>

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/inventoryReports.js" %>' />

<input id="reportUrl" type="hidden" value="<%= ModuleWebConstants.JASPER_REPORT_PAGE %>" />

<h2><spring:message code="openhmis.inventory.admin.reports" /></h2>

<table style="width: 99%">
  <tr>
    <td style="vertical-align: top; width: 250px;">
      <br />
      <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INVENTORY_PAGE %>"><spring:message code="openhmis.inventory.admin.pending"/></a><br />
      <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INVENTORY_CREATION_PAGE %>"><spring:message code="openhmis.inventory.admin.create"/></a><br />
      <c:if test="${showStockTakeLink}">
          <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INVENTORY_STOCK_TAKE_PAGE %>"><spring:message code="openhmis.inventory.admin.stockTake"/></a><br />
      </c:if>
      <b>
      <spring:message code="openhmis.inventory.admin.reports"/>
      </b>
    </td>
    <td>
      <c:if test="${stockTakeReport == null && stockCardReport == null && stockroomReport == null && expiringStockReport == null}" >
        <div style="color: grey">No inventory reports have been defined.</div>
      </c:if>
      <c:if test="${stockTakeReport != null}" >
        <h3>${stockTakeReport.name}</h3>
        <div style="color: grey">${stockTakeReport.description}</div>
        <br />
        <div>
          <form id="stockTakeReport" onsubmit="return false;">
            <label for="stockroomId">Stockroom: </label>
            <select id="stockroomId">
              <option value=""></option>
              <c:forEach var="stockroom" items="${stockrooms}">
                <option value="${stockroom.id}">${stockroom.name}</option>
              </c:forEach>
            </select>
            <input id="stockTakeReportId" type="hidden" value="${stockTakeReport.reportId}" />
            <br /><br />
            <input id="generateTakeReport" type="submit" value="Generate Report"/>
          </form>
        </div>
        <br />
        <hr>
      </c:if>

      <c:if test="${stockCardReport != null}" >
        <h3>${stockCardReport.name}</h3>
        <div style="color: grey">${stockCardReport.description}</div>
        <br />
        <div>
          <form id="stockCardReport" onsubmit="return false;">
            <label for="itemSearch">Item: </label>
            <input id="itemSearch" style="width: 350px" type="text" placeholder="Item Name" />
            <input id="item-uuid" type="hidden" />
            <br />
            <br />
            <label for="beginDate">Begin Date</label>
            <input id="beginDate" class="date" type="text" />

            <label for="endDate">End Date</label>
            <input id="endDate" class="date" type="text" />

            <input id="stockCardReportId" type="hidden" value="${stockCardReport.reportId}" />
            <br /><br />
            <input id="generateCardReport" type="submit" value="Generate Report" />
          </form>
        </div>
        <hr>
      </c:if>

      <c:if test="${stockOperationsByStockroomReport != null}" >
        <h3>${stockOperationsByStockroomReport.name}</h3>
        <div style="color: grey">${stockOperationsByStockroomReport.description}</div>
        <br />
        <div>
          <form id="stockOperationsByStockroomReport" onsubmit="return false;">
            <label for="stockroomIdOperationsByStockroom">Stockroom: </label>
            <select id="stockroomIdOperationsByStockroom">
              <option value=""></option>
              <c:forEach var="stockroom" items="${stockrooms}">
                <option value="${stockroom.id}">${stockroom.name}</option>
              </c:forEach>
            </select>
            <br/>
            <br/>
            <label for="itemSearchOperationsByStockroom">Item: </label>
            <input id="itemSearchOperationsByStockroom" style="width: 350px" type="text" placeholder="Item Name" />
            <input id="item-uuid-searchOperationsByStockroom" type="hidden" />
            <br />
            <br />
            <label for="beginDate-operationsByStockroom">Begin Date</label>
            <input id="beginDate-operationsByStockroom" class="date" type="text" />

            <label for="endDate-operationsByStockroom">End Date</label>
            <input id="endDate-operationsByStockroom" class="date" type="text" />

            <input id="stockOperationsByStockroomReportId" type="hidden" value="${stockOperationsByStockroomReport.reportId}" />
            <br /><br />
            <input id="generateOperationsByStockroomReport" type="submit" value="Generate Report" />
          </form>
        </div>
        <hr>
      </c:if>

      <c:if test="${stockroomReport != null}" >
        <h3>${stockroomReport.name}</h3>
        <div style="color: grey">${stockroomReport.description}</div>
        <br />
        <div>
          <form id="stockroomReport" onsubmit="return false;">
            <label for="stockroomReport-StockroomId">Stockroom: </label>
            <select id="stockroomReport-StockroomId">
              <option value=""></option>
              <c:forEach var="stockroom" items="${stockrooms}">
                <option value="${stockroom.id}">${stockroom.name}</option>
              </c:forEach>
            </select>
            <br />
            <br />

            <label for="stockroomReport-beginDate">Begin Date</label>
            <input id="stockroomReport-beginDate" class="date" type="text" />

            <label for="stockroomReport-endDate">End Date</label>
            <input id="stockroomReport-endDate" class="date" type="text" />

            <input id="stockroomReportId" type="hidden" value="${stockroomReport.reportId}" />
            <br /><br />
            <input id="generateStockroomReport" type="submit" value="Generate Report"/>
          </form>
        </div>
        <br />
        <hr>
      </c:if>

      <c:if test="${expiringStockReport != null}" >
        <h3>${expiringStockReport.name}</h3>
        <div style="color: grey">${expiringStockReport.description}</div>
        <br />
        <div>
          <form id="expiringStockReport" onsubmit="return false;">

            <label for="expiringStockReport-StockroomId">Stockroom: </label>
            <select id="expiringStockReport-StockroomId">
              <option value="">Any</option>
              <c:forEach var="stockroom" items="${stockrooms}">
                <option value="${stockroom.id}">${stockroom.name}</option>
              </c:forEach>
            </select>
            <br />
            <br />
            <label for="expiresBy">Expires by</label>
            <input id="expiresBy" class="date" type="text" />
            <input id="expiringStockReportId" type="hidden" value="${expiringStockReport.reportId}" />
            <br /><br />
            <input id="generateExpiringStockReport" type="submit" value="Generate Report" />
          </form>
        </div>
        <hr>
      </c:if>
    </td>
  </tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>
