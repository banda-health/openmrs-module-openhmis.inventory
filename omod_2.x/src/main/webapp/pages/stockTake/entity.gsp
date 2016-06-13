<script type="text/javascript">
	var breadcrumbs = [
		{
			icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'
		},
		{
			label: "${ ui.message("openhmis.inventory.page")}",
			link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'
		},
		{
			label: "${ ui.message("openhmis.inventory.admin.task.dashboard")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/inventoryTasksDashboard.page'
		},
		{
			label: "${ui.message("openhmis.inventory.admin.stockTake")}"
		}
	];

	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>

<form name="entityForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">
	<table class="header-title">
		<span class="h1-substitue-left" style="float:left;">
			${ui.message('openhmis.inventory.admin.stockTake')}
		</span>
		<span style="float:right;">

		</span>
	</table>
	<br/>
	<fieldset class="format">
		<div class="row">
			<div class="col-md-3">
				<span>${ui.message('openhmis.inventory.stockroom.name')}</span>
			</div>

			<div class="col-md-6">
				<select class="form-control" ng-model="entity.stockroom"
				        ng-options='stockroom.name for stockroom in stockrooms track by stockroom.uuid'>
					<option value="" selected="selected">Any</option>
				</select>
			</div>

			<div class="col-md-3">
				<input type="button" value="Search" class="confirm form-control">
			</div>
		</div>
		<br/>
	</fieldset>
</form>
