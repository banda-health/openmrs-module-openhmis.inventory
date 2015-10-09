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
	
	emr.loadMessages([
		"general.edit",
		"general.new",
		"general.retire",
		"general.unretire",
		"openhmis.inventory.department.success.retired",
		"openhmis.inventory.department.confirm.delete",
		"openhmis.inventory.department.success.saved",
		"general.retiredReason.empty",
		"Module.error.nameCannotBeEmpty",
		"openhmis.inventory.depatment.error.notFound",
		"openhmis.inventory.department.success.updated",
		"openhmis.inventory.department.success.unretired"
	]);
</script>

<form id="current-department" ng-app="departmentApp" ng-controller="departmentController" novalidate >
	<h1>{{h2SubString}} ${ ui.message('openhmis.inventory.department.name') }</h1>
	
	<p class="successfulMessage" ng-hide="successfulMsg == ''">{{successfulMsg}}</p>
	
	<input type="hidden" ng-model="uuid" />
		
	<h3>${ ui.message('general.name') }</h3>
	<input type="text" style="min-width: 50%;" ng-model="name" placeholder="${ ui.message('general.name') }" required />
	<p class="checkRequired" ng-hide="name != ''">{{nameIsRequiredMsg}}</p>
				
	<h3>${ ui.message('general.description') }</h3>
	<input type="text" ng-model="description" size="80" placeholder="${ ui.message('general.description') }" />
	<br />
	<p>
		<span><input type="button" class="cancel" value="${ ui.message('general.cancel') }" ng-click="cancel()" /></span>
		<span><input type="button" class="confirm right" value="${ ui.message('general.save') }" ng-click="save()" /></span>
	</p>
	<br />
	<h3 ng-hide="thisIsANewBill">{{retireOrUnretire}} ${ ui.message('openhmis.inventory.department.name') }</h3>
	<p ng-hide="thisIsANewBill">
		<span ng-show="retired">${ ui.message('openhmis.inventory.department.retired.reason') } <b>{{retireReason}}</b><br /></span>
		<span ng-hide="retired"><input type="text" ng-hide="retired" style="min-width: 50%;" placeholder="${ ui.message('general.retireReason') }" size="80" ng-model="retireReason" /></span>
		<input type="button" ng-disabled="canNotRetire()" class="cancel" value="{{retireOrUnretire}}" ng-click="retire()" />
	</p>
	<p class="checkRequired" ng-hide="retireReason != ''">{{retireReasonIsRequiredMsg}}</p>
	
	<p>
		<input type="button" ng-hide="thisIsANewBill" class="cancel" value="${ ui.message('general.purge') }" ng-click="purge()"/>
	</p>
</form>
