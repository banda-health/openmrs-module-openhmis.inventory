<td>
	<div class="showEntries includeRetired">
		<select ng-model="itemsPerPage" ng-options="itemsPerPage for itemsPerPage in limits" ng-change="searchItem()"></select>
		<input type="checkbox" ng-model="retiredChecked" ng-click="searchItem()">
		<b>Show retired</b>
	</div>
</td>
<td colspan="5">
	<div class="pagination pull-right">
		<ul>
			<li ng-class="{disabled: currentPage == 0}">
				<a href ng-click="prevPage()">« Prev</a>
			</li>
			<li ng-repeat="n in range(pages, currentPage, currentPage + gap) "
				ng-class="{active: n == currentPage}"
				ng-click="setPage()">
				<a href ng-bind="n + 1">1</a>
			</li>
			<li ng-class="{disabled: (currentPage) == pagedItems.length - 1}">
				<a href ng-click="nextPage()">Next »</a>
			</li>
		</ul>
	</div>
</td>