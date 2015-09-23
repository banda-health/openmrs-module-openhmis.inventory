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
	<h2 align="center">{{h2SubString}} ${ ui.message('openhmis.inventory.department.name') }</h2>
	
	<input type="hidden" ng-model="uuid">
		
	<h3>${ ui.message('general.name') }</h3>
	<input type="text" ng-model="name" size="80" placeholder="${ ui.message('general.name') }">
		
	<h3>${ ui.message('general.description') }</h3>
	<input type="text" ng-model="description" size="80" placeholder="${ ui.message('general.description') }">
	<br /><br />
	<input type="button" class="confirm" value="${ ui.message('general.save') }" />
</div>