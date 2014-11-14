<% ui.decorateWith("openhmis.inventory", "decorator") %>

<% ui.includeJavascript("openhmis.inventory", "angular.min.js") %>
<% ui.includeJavascript("openhmis.inventory", "table.js") %>

<% ui.includeCss("openhmis.inventory", "bootstrap.no-icons.min.css") %>
<% ui.includeCss("openhmis.inventory", "font-awesome.css") %>
<% ui.includeCss("openhmis.inventory", "table.css") %>

<body ng-app="myModule">
<div ng-controller="ctrlRead">
	<table class="table table-striped table-condensed table-hover">
		<thead>
			<tr>
				<th ng-repeat="headField in headFields" class="capitalize" custom-sort order="'{{headField}}'" sort="sort">{{headField}}&nbsp;</th>
			</tr>
		</thead>
		<tfoot>
			<td colspan="6">
				<div class="pagination pull-right">
					<ul>
						<li ng-class="{disabled: currentPage == 0}">
							<a href ng-click="prevPage()">« Prev</a>
						</li>
						<li ng-repeat="n in range(pagedItems.length, currentPage, currentPage + gap) "
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
		</tfoot>
		<tbody>
			<tr ng-repeat="item in pagedItems[currentPage] | orderBy:sort.sortingOrder:sort.reverse">
				<td ng-repeat="col in headFields">{{item[col]}}</td>
			</tr>
		</tbody>
	</table>
</div>
</body>

