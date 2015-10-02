<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.institution.name") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeCss("openhmis.inventory", "institutions2x.css")
    ui.includeJavascript("openhmis.inventory", "institutionsController.js")
%>


<script type="text/javascript">
	var breadcrumbs = [
	    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	    { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
	    { label: "${ ui.message("openhmis.inventory.manage.module")}", link: 'inventory/manageModule.page' },
	    { label: "${ ui.message("openhmis.inventory.admin.institutions")}"}
	];
	
	/*emr.loadMessages([
        "openhmis.inventory.institution.name",
        "general.edit",
        "general.new"
    ]);*/
</script>
