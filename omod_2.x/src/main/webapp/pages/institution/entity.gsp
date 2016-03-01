<script type="text/javascript">
	var breadcrumbs = [
		{icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
		{
			label: "${ ui.message("openhmis.inventory.page")}",
			link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'
		},
		{
			label: "${ ui.message("openhmis.inventory.manage.module")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page'
		},
		{
			label: "${ ui.message("openhmis.inventory.admin.institutions")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/institution/entities.page'
		},
		{label: "${ ui.message("openhmis.inventory.general.edit")} ${ui.message("openhmis.inventory.institution.name")}"}
	];

	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>

<form novalidate>
	${ ui.includeFragment("openhmis.commons", "editEntityHeaderFragment")}
	
	<input type="hidden" ng-model="entity.uuid"/>

	<h3>{{messageLabels['general.name']}}</h3>
	<input type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="{{messageLabels['general.name']}}"
	       required/>

	<p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>

	<h3>{{messageLabels['general.description']}}</h3>
	<input type="text" ng-model="entity.description" size="80" placeholder="{{messageLabels['general.description']}}"/>
	<br/>

	<p>
		<span><input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()"/></span>
		<span><input type="button" class="confirm right" value="{{messageLabels['general.save']}}"
		             ng-disabled="entity.name == '' || entity.name == undefined" ng-click="saveOrUpdate()"/></span>
	</p>
	<br/>

	${ ui.includeFragment("openhmis.commons", "retireUnretireDeleteFragment") }

</form>
