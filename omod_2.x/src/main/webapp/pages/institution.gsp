<form novalidate >
	<h1>{{h2SubString}}</h1>
	
	<input type="hidden" ng-model="entity.uuid" />
		
	<h3>{{generalNameLbl}}</h3>
	<input type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="{{generalNameLbl}}" required />
	<p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
				
	<h3>{{generalDescriptionLbl}}</h3>
	<input type="text" ng-model="entity.description" size="80" placeholder="{{generalDescriptionLbl}}" />
	<br />
	<p>
		<span><input type="button" class="cancel" value="{{generalCancelLbl}}" ng-click="cancel()" /></span>
		<span><input type="button" class="confirm right" value="{{generalSaveLbl}}"  ng-disabled="entity.name == '' || entity.name == undefined" ng-click="saveOrUpdate()" /></span>
	</p>
	<br />
	<h3 ng-hide="entity.uuid == ''">{{retireOrUnretire}}</h3>
	<p ng-hide="entity.uuid == ''">
		<span ng-show="entity.retired">{{retireReasonLbl}}<b>{{entity.retireReason}}</b><br /></span>
		<span ng-hide="entity.retired"><input type="text" placeholder="{{generalRetireReasonLbl}}" style="min-width: 50%;" ng-model="entity.retireReason" ng-disabled="entity.retired" /></span>
		<input type="button" ng-disabled="entity.uuid == '' || entity.retireReason == '' || entity.retireReason == null" class="cancel" value="{{retireOrUnretire}}" ng-click="retireOrUnretireCall()" />
	</p>
	<p class="checkRequired" ng-hide="entity.retireReason != '' || retireReasonIsRequiredMsg == '' || retireReasonIsRequiredMsg == undefined">{{retireReasonIsRequiredMsg}}</p>
	
	<h3 ng-hide="entity.uuid == ''">
		{{deleteForeverLbl}}
	</h3>
	<p>
		<input type="button" ng-hide="entity.uuid == ''" class="cancel" value="{{generalPurge}}" ng-click="purge()"/>
	</p>
	
</form>
