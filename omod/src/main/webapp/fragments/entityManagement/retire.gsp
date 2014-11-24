<div class="retired-purge">
	<div ng-switch="editItem.retired">
		<div ng-switch-when="false" class="retire">
			<div>
				<b>Retire Item</b>
			</div>
			Reason: <input type="text" ng-model="retireReason">
			<button ng-model="retire" ng-click="retire(editItem, retireReason)">Retire</button>
		</div>
		<div ng-switch-when="true" class="unretire">
			<div>
				<b>Unretire Item</b>
			</div>
			<button ng-model="retire" ng-click="unretire(editItem)">Unretire</button>
		</div>
	</div>
	<div class="purge">
		<button ng-model="retire" ng-click="purge(editItem)">Purge</button>
	</div>
</div>