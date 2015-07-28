<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<h2>Report Generation Error</h2>
<p>The report ${reportName} with the Id ${reportId} cannot be generated</p>
<p>Please make sure that the report is added in the system. Click <a
		href="${pageContext}/module/${pom.parent.artifactId}/jreport.list">Here</a> to add the report.</p>
<p>For Documentation on how to add the report . Click <a
		href="https://wiki.openmrs.org/display/docs/Jasper+Report+Module">Here</a>.</p>
<%@ include file="/WEB-INF/template/footer.jsp"%>
