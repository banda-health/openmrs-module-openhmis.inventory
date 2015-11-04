<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.institution.name") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeCss("openhmis.inventory", "institutions2x.css")
%>

<script data-main="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/institution.main.js" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

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
        "general.edit",
        "general.new"
    ]);
</script>

<form id="institutionApp" ng-controller="InstitutionController" novalidate >
	<h1>{{h2SubString}} ${ ui.message('openhmis.inventory.institution.name') }</h1>
	
	<input type="hidden" ng-model="entity.uuid" />
		
	<h3>${ ui.message('general.name') }</h3>
	<input type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="${ ui.message('general.name') }" required />
	<p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
				
	<h3>${ ui.message('general.description') }</h3>
	<input type="text" ng-model="entity.description" size="80" placeholder="${ ui.message('general.description') }" />
	<br />
	<p>
		<span><input type="button" class="cancel" value="${ ui.message('general.cancel') }" ng-click="cancel()" /></span>
		<span><input type="button" class="confirm right" value="${ ui.message('general.save') }"  ng-disabled="entity.name == '' || entity.name == undefined" ng-click="saveOrUpdate()" /></span>
	</p>
	<br />
	<h3 ng-hide="entity.uuid == ''">{{retireOrUnretire}}</h3>
	<p ng-hide="entity.uuid == ''">
		<span ng-show="entity.retired">${ ui.message('openhmis.general.retired.reason') } <b>{{entity.retireReason}}</b><br /></span>
		<span ng-hide="entity.retired"><input type="text" placeholder="${ ui.message('general.retireReason') }" style="min-width: 50%;" ng-model="entity.retireReason" ng-disabled="entity.retired" /></span>
		<input type="button" ng-disabled="entity.uuid == '' || entity.retireReason == '' || entity.retireReason == null" class="cancel" value="{{retireOrUnretire}}" ng-click="retireOrUnretireCall()" />
	</p>
	<p class="checkRequired" ng-hide="entity.retireReason != '' || retireReasonIsRequiredMsg == '' || retireReasonIsRequiredMsg == undefined">{{retireReasonIsRequiredMsg}}</p>
	
	<h3 ng-hide="entity.uuid == ''">
		{{deleteForeverMsg}}
	</h3>
	<p>
		<input type="button" ng-hide="entity.uuid == ''" class="cancel" value="${ ui.message('general.purge') }" ng-click="purge()"/>
	</p>
	
</form>
