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
	<table>
		<div class="row">
			<div class="col-md-3">
				<span>${ui.message('openhmis.inventory.stockroom.name')}</span>
			</div>

			<div class="col-md-6">
				<select class="form-control" ng-model="entity.stockroom"
				        ng-options='stockroom.name for stockroom in stockrooms track by stockroom.uuid' ng-change="loadStockDetails()">
					<option value="" selected="selected">Any</option>
				</select>
			</div>

			<div class="col-md-3">
				<input type="button" value="Search" class="confirm form-control" ng-click="loadStockDetails()">
			</div>
		</div>
		<br/>
	</table>
	<form class="detail-section-border-top" ng-show="totalNumOfResults != 0">
		<br/>
		<table style="margin-bottom:5px;" class="manage-entities-table table-condensed">
			<thead>
			<tr>
				<th>${ui.message('openhmis.inventory.item.name')}</th>
				<th>${ui.message('openhmis.inventory.department.name')}</th>
				<th>${ui.message('openhmis.inventory.stockroom.expiration')}</th>
				<th>${ui.message('openhmis.inventory.item.quantity')}</th>
				<th>${ui.message('openhmis.inventory.item.actual.quantity')}</th>
			</tr>
			</thead>
			<tbody>
			<tr class="clickable-tr" dir-paginate="entity in fetchedEntities | itemsPerPage: limit"
			    total-items="totalNumOfResults">
				<td >{{entity.item.name}}</td>
				<td >{{entity.item.department.name}}</td>
				<td >{{entity.expiration | date: "yyyy-MM-dd"}}</td>
				<td >{{entity.quantity}}</td>
				<td ><input type="number" class="form-control"></td>
			</tr>
			</tbody>
		</table>
		<br/>
		<div class="detail-section-border-top">
			<br/>
			<span>
				<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" />
				<input type="button" class="confirm right" value="{{messageLabels['general.save']}} ${ui.message('openhmis.inventory.admin.stockTake')}" ng-click="saveOrUpdate()" />
			</span>
		</div>
	</form>
</form>
