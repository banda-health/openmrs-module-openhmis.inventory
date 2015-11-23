<form novalidate >
	<h1>{{messageLabels['h2SubString']}}</h1>
	
	<input type="hidden" ng-model="entity.uuid" />
		
	<h3>{{messageLabels['general.name']}}</h3>
	<input type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="{{messageLabels['general.name']}}" required />
	<p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
				
	<h3>{{messageLabels['general.description']}}</h3>
	<input type="text" ng-model="entity.description" size="80" placeholder="{{messageLabels['general.description']}}" />
	<br />
	<p>
		<span><input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" /></span>
		<span><input type="button" class="confirm right" value="{{messageLabels['general.save']}}"  ng-disabled="entity.name == '' || entity.name == undefined" ng-click="saveOrUpdate()" /></span>
	</p>
	<br />
	<h3 ng-hide="entity.uuid == ''">{{retireOrUnretire}}</h3>
	<p ng-hide="entity.uuid == ''">
		<span ng-show="entity.retired">{{messageLabels['openhmis.inventory.general.retired.reason']}}<b>{{entity.retireReason}}</b><br /></span>
		<span ng-hide="entity.retired"><input type="text" placeholder="{{messageLabels['general.retireReason']}}" style="min-width: 50%;" ng-model="entity.retireReason" ng-disabled="entity.retired" /></span>
		<input type="button" ng-disabled="entity.uuid == '' || entity.retireReason == '' || entity.retireReason == null" class="cancel" value="{{retireOrUnretire}}" ng-click="retireOrUnretireCall()" />
	</p>
	<p class="checkRequired" ng-hide="entity.retireReason != '' || retireReasonIsRequiredMsg == '' || retireReasonIsRequiredMsg == undefined">{{retireReasonIsRequiredMsg}}</p>
	
	<h3 ng-hide="entity.uuid == ''">
		{{messageLabels['delete.forever']}}
	</h3>
	<p>
		<input type="button" ng-hide="entity.uuid == ''" class="cancel" value="{{messageLabels['general.purge']}}" ng-click="purge()"/>
	</p>
	
</form>
