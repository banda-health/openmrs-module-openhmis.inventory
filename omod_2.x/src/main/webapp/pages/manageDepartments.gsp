<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("appointmentschedulingui", "departments2x.css")
    ui.includeJavascript("appointmentschedulingui", "departments2x.js")
%>


<script type="text/javascript">
// TODO redo this file using angular?
var breadcrumbs = [
    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
    { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
    { label: "${ ui.message("openhmis.inventory.manage.module")}", link: '${ui.pageLink("openhmis.inventory", "manageModule")}' },
    { label: "${ ui.message("openhmis.inventory.admin.departments")}" }
];
</script>