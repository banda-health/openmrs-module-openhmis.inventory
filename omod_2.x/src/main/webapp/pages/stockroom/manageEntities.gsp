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
		{label: "${ ui.message("openhmis.inventory.admin.stockrooms")}",}
	];

	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>

<div id="entities-body">
	<br/>

	<div id="manage-entities-header">
		<span class="h1-substitue-left" style="float:left;">
			${ui.message('openhmis.inventory.admin.stockrooms')}
		</span>
		<span style="float:right;">
			<a class="button confirm" ui-sref="new">
				<i class="icon-plus"></i>
				{{newEntityLabel}}
			</a>
		</span>
	</div>
	<br/><br/><br/>

	<div ng-controller="ManageStockroomsController">
		<div id="entities">
			<div class="btn-group">
				${ui.message('openhmis.inventory.location.name')}:
				<ul class="search-area">
					<li>
						<select ng-model="location" ng-change="searchStockrooms(currentPage)" style="height:33px;"
						        ng-options='location.display for location in locations track by location.uuid'>
							<option value="" selected="selected">Any</option>
						</select>
					</li>
					<li>
						${ ui.includeFragment("openhmis.commons", "searchFragment", [
								model: "searchField",
								onChangeEvent: "searchStockroomsByName(currentPage)",
								class: ["field-display ui-autocomplete-input form-control searchinput"],
								placeholder: [ui.message("openhmis.inventory.stockroom.searchStockroom")]
						])}
					</li>
				</ul>
			</div>

			<br/><br/>
			<table style="margin-bottom:5px;" class="manage-stockrooms-table">
				<thead>
				<tr>
					<th>${ui.message('general.name')}</th>
					<th>${ui.message('openhmis.inventory.location.name')}</th>
				</tr>
				</thead>
				<tbody>
				<tr class="clickable-tr" dir-paginate="entity in fetchedEntities | itemsPerPage: limit"
				    total-items="totalNumOfResults" current-page="currentPage" ui-sref="edit({uuid: entity.uuid})">
					<td ng-style="strikeThrough(entity.retired)">{{entity.name}}</td>
					<td ng-style="strikeThrough(entity.retired)">{{entity.location.display}}</td>
				</tr>
				</tbody>
			</table>

			<div class="not-found" ng-show="fetchedEntities.length == 0 && searchField == ''">
				${ui.message('openhmis.inventory.stockroom.notFound')}
			</div>

			<div ng-show="fetchedEntities.length == 0 && searchField != ''">
				<br/>
				${ui.message('openhmis.commons.general.preSearchMessage')} - <b> {{searchField}} </b> - {{postSearchMessage}}
				<br/><br/>
				<span><input type="checkbox" ng-checked="includeRetired" ng-model="includeRetired"
				             ng-change="searchStockrooms()"></span>
				<span>${ui.message('openhmis.commons.general.includeRetired')}</span>
			</div>
			${ui.includeFragment("openhmis.commons", "paginationFragment" ,[onChange: "searchStockrooms(currentPage)", onPageChange: "searchStockrooms(currentPage)"])}
		</div>
	</div>
</div>
