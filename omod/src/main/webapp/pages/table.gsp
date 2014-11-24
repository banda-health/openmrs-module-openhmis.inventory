${ ui.includeFragment "openhmis.inventory", "initialImports" }

<body ng-app="itemsModule">
	<div ng-controller="ctrlRead">
		<div id="search">
			${ ui.message("openhmis.inventory.search") }: <input type="text" ng-model="query">
			<span  class="nullable">
			    <select ng-model="departmentSelected" ng-options="department.name for department in departments">
			        <option value="">${ ui.message("openhmis.inventory.any") }</option>
			    </select>
			</span>
			<button ng-click="searchItem(query, departmentSelected, retiredChecked)">${ ui.message("openhmis.inventory.search") }</button>
		</div>
		<div>
			<table class="table table-striped table-condensed table-hover">
				<thead>
					<tr>
						<th custom-sort="name" order="'name'" sort="sort">${ ui.message("openhmis.inventory.name") }&nbsp;</th>
						<th custom-sort="department.name" order="'department.name'" sort="sort">${ ui.message("openhmis.inventory.department") }&nbsp;</th>
						<th custom-sort="codes[0].code" order="'codes[0].code'" sort="sort">${ ui.message("openhmis.inventory.codes") }&nbsp;</th>
						<th custom-sort="defaultPrice.price" order="'defaultPrice.price'" sort="sort">${ ui.message("openhmis.inventory.defaultPrice") }&nbsp;</th>
					</tr>
				</thead>
				<tfoot>
					${ ui.includeFragment "openhmis.inventory", "entityManagement/tableFooter" }
				</tfoot>
				<tbody>
					<tr ng-repeat="item in items | orderBy:sort.sortingOrder:sort.reverse" 
						ng-click="edit(item)" ng-class="{retired: item.retired==true}">
						<td>{{item.name}}</td>
						<td>{{item.department.name}}</td>
						<td>{{item.codes[0].code}}</td>
						<td>
							{{item.defaultPrice.price | currency:""}} ({{item.defaultPrice.name}})
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="edit" ng-hide="hideEditItem">
			<div>
				<input type="text" ng-model="editItem.name">
			</div>
			<div class="button">
				<button ng-model="cancelEdit" ng-click="hideEditItem = !hideEditItem">${ ui.message("openhmis.inventory.cancel") }</button>
			</div>
			${ ui.includeFragment("openhmis.inventory", "entityManagement/retire") }
		</div>
	</div>
</body>

