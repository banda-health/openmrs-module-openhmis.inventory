<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.institutions") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeCss("openhmis.inventory", "bootstrap.css")
    ui.includeCss("openhmis.inventory", "institutions2x.css")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
%>

<script type="text/javascript">
	emr.loadMessages([
        "openhmis.inventory.institution.name",
        "openhmis.inventory.general.new",
        "openhmis.inventory.general.error.notFound",
        "openhmis.inventory.general.created.success",
        "openhmis.inventory.general.updated.success",
        "openhmis.inventory.general.retired.success",
        "openhmis.inventory.general.unretired.success",
        "openhmis.inventory.general.confirm.delete",
        "openhmis.inventory.general.deleted.success",
        "openhmis.inventory.general.name.required",
        "openhmis.inventory.general.retireReason.required",
        "openhmis.inventory.general.unretire",
        "openhmis.inventory.general.retire",
        "openhmis.inventory.general.delete",
        "openhmis.inventory.general.error.duplicate",
        "openhmis.inventory.general.retired.reason",
        "general.edit",
        "general.new"
    ]);
</script>

<script type="text/javascript" src="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/config.js"></script>
<script data-main="institution/configs/institutions.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

<script type="text/javascript">
	var breadcrumbs = [
	    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	    { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
	    { label: "${ ui.message("openhmis.inventory.manage.module")}", link: 'inventory/manageModule.page' },
	    { label: "${ ui.message("openhmis.inventory.admin.institutions")}"}
	];
</script>

<div id="institutionsAp">
	<div ui-view></div>
</div>