<form onsubmit="return removeIndexFromItems()">
	<h1>{{messageLabels['h2SubString']}}</h1>

	<input type="hidden" ng-model="entity.uuid" />

	<fieldset class="format">

		<ul class="table-layout">
			<li>
				<h3>{{messageLabels['general.name']}}</h3>
			</li>
			<li>
				<input class="form-control" type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="{{messageLabels['general.name']}}" required />
				<p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>{{messageLabels['general.description']}}</h3>
			</li>
			<li>
				<textarea class="form-control" ng-model="entity.description" placeholder="{{messageLabels['general.description']}}"></textarea>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.inventory.operations.type.sourceLabel')}</h3>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasSource" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.inventory.operations.type.destinationLabel')}</h3>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasDestination" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.inventory.operations.type.recipientLabel')}</h3>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasRecipient" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.inventory.operations.type.availableWhenReservedLabel')}</h3>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.availableWhenReserved" disabled="disabled" />
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.inventory.operations.type.userLabel')}</h3>
			</li>
			<li>
				<select class="form">
					<option ng-repeat="user in users track by user.uuid" ng-selected="entity.user.name == user.name">
						{{user.name}}
					</option>
				</select>
			</li>
		</ul>
	</fieldset>
	<br />
	<fieldset class="format">
		<ul class="table-layout">
			<li>
				<span>
					<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" />
				</span>
			</li>
			<li>
				<span>
					<input type="button" class="confirm right" value="{{messageLabels['general.save']}}"  ng-disabled="entity.name == '' || entity.name == undefined" ng-click="removeItemTemporaryIds(); saveOrUpdate()" />
				</span>
			</li>
		</ul>
	</fieldset>

	<fieldset class="format">
		<h3 ng-hide="entity.uuid == ''">{{retireOrUnretire}}</h3>
		<p ng-hide="entity.uuid == ''">
			<span ng-show="entity.retired">{{messageLabels['openhmis.inventory.general.retired.reason']}}<b>{{entity.retireReason}}</b><br /></span>
			<span ng-hide="entity.retired"><input type="text" placeholder="{{messageLabels['general.retireReason']}}" style="min-width: 50%;" ng-model="entity.retireReason" ng-disabled="entity.retired" /></span>
			<input type="button" ng-disabled="entity.uuid == '' || entity.retireReason == '' || entity.retireReason == null" class="cancel" value="{{retireOrUnretire}}" ng-click="retireOrUnretireCall()" />
		</p>
		<p class="checkRequired" ng-hide="entity.retireReason != '' || retireReasonIsRequiredMsg == '' || retireReasonIsRequiredMsg == undefined">{{retireReasonIsRequiredMsg}}</p>
	</fieldset>
</form>
