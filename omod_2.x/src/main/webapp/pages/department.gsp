<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.department.name") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeCss("openhmis.inventory", "departments2x.css")
    ui.includeJavascript("openhmis.inventory", "departmentController.js")
%>


<script type="text/javascript">
	var breadcrumbs = [
	    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	    { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
	    { label: "${ ui.message("openhmis.inventory.manage.module")}", link: 'inventory/manageModule.page' },
	    { label: "${ ui.message("openhmis.inventory.admin.departments")}", link: 'manageDepartments.page' },
	    { label: "${ ui.message("openhmis.inventory.department.name")}" }
	];
</script>

<div id="current-department" ng-app="departmentApp" ng-controller="departmentController">
	<h3>${ ui.message('openhmis.inventory.department.name') }</h3>
	
	<input type="hidden" ng-model="uuid"><br />
		
	${ ui.message('general.name') }
	<input type="text" ng-model="name"><br /><br />
		
	${ ui.message('general.description') }
	<input type="text" ng-model="description">
</div>