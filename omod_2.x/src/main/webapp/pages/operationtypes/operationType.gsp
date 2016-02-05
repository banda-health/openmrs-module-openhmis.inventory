<form onsubmit="return removeIndexFromItems()">
	<h1>{{messageLabels['h2SubString']}}</h1>

	<input type="hidden" ng-model="entity.uuid"/>

	<fieldset class="format">

		<ul class="table-layout">
			<li>
				<h3>{{messageLabels['general.name']}}</h3>
			</li>
			<li>
				<input class="form-control" type="text" ng-model="entity.name" style="min-width: 50%;"
				       placeholder="{{messageLabels['general.name']}}" required/>

				<p class="checkRequired"
				   ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>{{messageLabels['general.description']}}</h3>
			</li>
			<li>
				<textarea class="form-control" ng-model="entity.description"
				          placeholder="{{messageLabels['general.description']}}"></textarea>
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
				<input type="checkbox" ng-model="entity.availableWhenReserved" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.inventory.operations.type.userLabel')}</h3>
			</li>
			<li>
				<select class="form-control">
					<option ng-repeat="user in users track by user.uuid" ng-selected="entity.user.display == user.display">
						{{user.display}}
					</option>
				</select>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.inventory.operations.type.roleLabel')}</h3>
			</li>
			<li>
				<select class="form-control">
					<option ng-repeat="role in roles track by role.uuid" ng-selected="entity.role.display == role.display">
						{{role.display}}
					</option>
				</select>
			</li>
		</ul>
		<br/>
		<ul class="table-layout">
			<li>
				<h3>${ui.message('openhmis.backboneforms.attribute.type.namePlural')}</h3>
			</li>
			<li>
				<div class="bbf-editor">
					<div class="bbf-list" name="attributeTypes">
						<ul>
							<li ng-repeat="itemCode in entity.codes track by itemCode.uuid || itemCode.id">
								<button ng-click="removeItemCode(itemCode)" type="button" data-action="remove"
								        class="bbf-remove" title="Remove">×</button>

								<div>{{itemCode.code}}</div>
							</li>
						</ul>

						<div class="bbf-actions">
							<button type="button" data-action="add" ng-click="addAttributeTypes()">Add</button>
						</div>

						<div id="attribute-types-dialog" class="dialog" style="display:none;">
							<div class="dialog-header">
								<span ng-show="addAttributeTypeTitle != ''">
									<i class="icon-plus-sign"></i>

									<h3>{{messageLabels['addAttributeTypeTitle']}}</h3>
								</span>
								<span ng-show="editAttributeTypeTitle != ''">
									<i class="icon-edit"></i>

									<h3>{{messageLabels['editAttributeTypeTitle']}}</h3>
								</span>
							</div>

							<div class="dialog-content form">
								<ul class="table-layout dialog-table-layout">
									<li class="required">
										<span>{{messageLabels['general.name']}}</span>
									</li>
									<li>
										<input type="text" ng-model="entity.name" style="min-width: 100%;"
										       placeholder="{{messageLabels['general.name']}}" required/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>{{messageLabels['general.description']}}</span>
									</li>
									<li>
										<input type="text" ng-model="entity.description" style="min-width: 50%;"
										       placeholder="{{messageLabels['general.description']}}"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>${ui.message('PersonAttributeType.format')}</span>
									</li>
									<li>
										<select ng-model="entity.format"
										        ng-options="field for field in formatFields track by field">
											<option value="" ng-if="false"></option>
											<option ng-selected="entity.format == field">
											</option>
										</select>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>${ui.message('PersonAttributeType.foreignKey')}</span>
									</li>
									<li>
										<input type="number" ng-model="entity.foreignKey"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>${ui.message('PatientIdentifierType.format')}</span>
									</li>
									<li>
										<input type="text" ng-model="entity.regExp"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>${ui.message('FormField.required')}</span>
									</li>
									<li>
										<input type="checkbox" ng-model="entity.required"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>${ui.message('Field.attributeName')} ${ui.message('Obs.order')}</span>
									</li>
									<li>
										<input type="number" ng-model="entity.attributeOrder"/>
									</li>
								</ul>

								<div class="ngdialog-buttons">
									<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}"
									       ng-click="cancel()"/>
									<input type="button" class="confirm right" value="{{messageLabels['general.save']}}"
									       ng-click="saveOrUpdate()"/>
								</div>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</fieldset>
	<br/>
	<fieldset class="format">
		<ul class="table-layout">
			<li>
				<span>
					<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()"/>
				</span>
			</li>
			<li>
				<span>
					<input type="button" class="confirm right" value="{{messageLabels['general.save']}}"
					       ng-disabled="entity.name == '' || entity.name == undefined"
					       ng-click="removeItemTemporaryIds(); saveOrUpdate()"/>
				</span>
			</li>
		</ul>
	</fieldset>

	<fieldset class="format">
		<h3 ng-hide="entity.uuid == ''">{{retireOrUnretire}}</h3>

		<p ng-hide="entity.uuid == ''">
			<span ng-show="entity.retired">{{messageLabels['openhmis.inventory.general.retired.reason']}}<b>{{entity.retireReason}}</b><br/>
			</span>
			<span ng-hide="entity.retired"><input type="text" placeholder="{{messageLabels['general.retireReason']}}"
			                                      style="min-width: 50%;" ng-model="entity.retireReason"
			                                      ng-disabled="entity.retired"/></span>
			<input type="button" ng-disabled="entity.uuid == '' || entity.retireReason == '' || entity.retireReason == null"
			       class="cancel" value="{{retireOrUnretire}}" ng-click="retireOrUnretireCall()"/>
		</p>

		<p class="checkRequired"
		   ng-hide="entity.retireReason != '' || retireReasonIsRequiredMsg == '' || retireReasonIsRequiredMsg == undefined">{{retireReasonIsRequiredMsg}}</p>
	</fieldset>
</form>
