<form>
    <h1>{{messageLabels['h2SubString']}}</h1>

    <input type="hidden" ng-model="entity.uuid" />

    <fieldset class="format">

        <ul class="table-layout">
            <li>
                <h3>{{messageLabels['general.name']}}</h3>
            </li>
            <li>
                <input type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="{{messageLabels['general.name']}}" required />
                <p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
            </li>
        </ul>
        <ul class="table-layout">
            <li>
                <h3>{{messageLabels['general.description']}}</h3>
            </li>
            <li>
                <input type="text" ng-model="entity.description" style="min-width: 50%;" placeholder="{{messageLabels['general.description']}}" required />
            </li>
        </ul>
        <ul class="table-layout">
            <li>
                <h3>${ui.message('PersonAttributeType.format')}</h3>
            </li>
            <li>
                <select ng-model="entity.format" ng-options="field for field in formatFields track by field">
                    <option value="" ng-if="false"></option>
                    <option ng-selected="entity.format == field">
                    </option>
                </select>
            </li>
        </ul>
        <ul class="table-layout">
            <li>
                <h3>${ui.message('PersonAttributeType.foreignKey')}</h3>
            </li>
            <li>
                <input type="number" ng-model="entity.foreignKey"  />
            </li>
        </ul>
        <ul class="table-layout">
            <li>
                <h3>${ui.message('PatientIdentifierType.format')}</h3>
            </li>
            <li>
                <input type="text" ng-model="entity.regExp" />
            </li>
        </ul>
        <ul class="table-layout">
            <li>
                <h3>${ui.message('FormField.required')}</h3>
            </li>
            <li>
                <input type="checkbox" ng-model="entity.required" />
            </li>
        </ul>
        <ul class="table-layout">
            <li>
                <h3>${ui.message('Field.attributeName')} ${ui.message('Obs.order')}</h3>
            </li>
            <li>
                <input type="number" ng-model="entity.attributeOrder" />
            </li>
        </ul>
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

        <h3 ng-hide="entity.uuid == ''">
            {{messageLabels['delete.forever']}}
        </h3>
        <p>
            <input type="button" ng-hide="entity.uuid == ''" class="cancel" value="{{messageLabels['general.purge']}}" ng-click="purge()"/>
        </p>
    </fieldset>
</form>
