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
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/institution/entities.page##/'
		},
		{label: "${ui.message("openhmis.inventory.institution.name")}"}
	];

	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>

<div ng-show="loading" class="loading-msg">
	<span>${ui.message("openhmis.commons.general.processingPage")}</span>
	<br />
	<span class="loading-img">
		<img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
	</span>
</div>

<form ng-hide="loading" name="entityForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">
	${ ui.includeFragment("openhmis.commons", "editEntityHeaderFragment")}
	
	<input type="hidden" ng-model="entity.uuid"/>

	<fieldset class="format">
		<ul class="table-layout">
			<li class="required">
				<span>{{messageLabels['general.name']}}</span>
			</li>
			<li>
				<input type="text" class="form-control" ng-model="entity.name" style="min-width: 50%;" placeholder="{{messageLabels['general.name']}}"
				       required/>
				<p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
			</li>
		</ul>
		<ul class="table-layout">
			<li style="vertical-align: top" class="not-required">
				<span>{{messageLabels['general.description']}}</span>
			</li>
			<li>
				<textarea ng-model="entity.description" placeholder="{{messageLabels['general.description']}}" rows="3"
				          cols="40">
				</textarea>
			</li>
		</ul>
	</fieldset>
	<fieldset class="format">
		<span>
			<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" />
			<input type="button" class="confirm right" value="{{messageLabels['general.save']}}" ng-click="saveOrUpdate()" />
		</span>
	</fieldset>
</form>
${ ui.includeFragment("openhmis.commons", "retireUnretireDeleteFragment") }
