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
	    { label: "${ ui.message("openhmis.inventory.admin.institutions")}", link: 'manageInstitutions.page' },
	    { label: "${ ui.message("openhmis.inventory.institution.name")}" }
	];
	
	emr.loadMessages([
        "openhmis.inventory.institution.name",
        "openhmis.inventory.institution.error.notFound",
        "openhmis.inventory.institution.created.success",
        "openhmis.inventory.institution.updated.success",
        "openhmis.inventory.institution.retired.success",
        "openhmis.inventory.institution.unretired.success",
        "openhmis.inventory.institution.confirm.delete",
        "openhmis.inventory.institution.deleted.success",
        "openhmis.inventory.institution.name.required",
        "openhmis.inventory.institution.retireReason.required",
        "openhmis.inventory.institution.unretire",
        "openhmis.inventory.institution.retire",
        "openhmis.inventory.institution.error.duplicate",
        "general.edit",
        "general.new"
    ]);
</script>

<form id="current-institution" ng-app="institutionApp" ng-controller="InstitutionController" novalidate >
	<h1>{{h2SubString}} ${ ui.message('openhmis.inventory.institution.name') }</h1>
	
	<input type="hidden" ng-model="institution.uuid" />
		
	<h3>${ ui.message('general.name') }</h3>
	<input type="text" ng-model="institution.name" size="80" placeholder="${ ui.message('general.name') }" required />
	<p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
				
	<h3>${ ui.message('general.description') }</h3>
	<input type="text" ng-model="institution.description" size="80" placeholder="${ ui.message('general.description') }" />
	<br />
	<h3 ng-hide="institution.uuid == ''">{{retireOrUnretire}} ${ ui.message('openhmis.inventory.institution.name') }</h3>
	<p ng-hide="institution.uuid == ''">
		<input type="text" placeholder="${ ui.message('general.retireReason') }" size="80" ng-model="institution.retireReason" ng-disabled="institution.retired" />
		<input type="button" ng-disabled="institution.uuid == '' || institution.retireReason == '' || institution.retireReason == null" class="cancel" value="{{retireOrUnretire}}" ng-click="retireOrUnretireFunction()" />
	</p>
	<p class="checkRequired" ng-hide="institution.retireReason != '' || retireReasonIsRequiredMsg == '' || retireReasonIsRequiredMsg == undefined">{{retireReasonIsRequiredMsg}}</p>
	
	<p>
		<input type="button" ng-hide="institution.uuid == ''" class="cancel" value="${ ui.message('general.purge') }" ng-click="purge()"/>
	</p>
	
	<p>
		<span><input type="button" class="cancel" value="${ ui.message('general.cancel') }" ng-click="cancel()" /></span>
		<span><input type="button" class="confirm right" value="${ ui.message('general.save') }"  ng-disabled="institution.name == '' || institution.name == undefined" ng-click="saveOrUpdate()" /></span>
	</p>
</form>

