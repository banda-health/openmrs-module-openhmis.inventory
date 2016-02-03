<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
        { label: "${ ui.message("openhmis.inventory.manage.module")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
        { label: "${ ui.message("openhmis.inventory.admin.item.attribute.types")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/itemAttributes/itemAttributeTypes.page'},
        { label: "${ ui.message("openhmis.inventory.itemAttributeType")}"}
    ];
    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>
<form name="itemAttributeForm" class="entity-form" ng-class="{'submitted': submitted}">
    <h1>{{messageLabels['h2SubString']}}</h1>
    <input type="hidden" ng-model="entity.uuid" />
    <fieldset class="format">
        <ul class="table-layout">
            <li class="required">
                <span>{{messageLabels['general.name']}}</span>
            </li>
            <li>
                <input type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="{{messageLabels['general.name']}}" required />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['general.description']}}</span>
            </li>
            <li>
                <input type="text" ng-model="entity.description" style="min-width: 50%;" placeholder="{{messageLabels['general.description']}}" />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>${ui.message('PersonAttributeType.format')}</span>
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
            <li class="not-required">
                <span>${ui.message('PersonAttributeType.foreignKey')}</span>
            </li>
            <li>
                <input type="number" ng-model="entity.foreignKey"  />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>${ui.message('PatientIdentifierType.format')}</span>
            </li>
            <li>
                <input type="text" ng-model="entity.regExp" />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>${ui.message('FormField.required')}</span>
            </li>
            <li>
                <input type="checkbox" ng-model="entity.required" />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>${ui.message('Field.attributeName')} ${ui.message('Obs.order')}</span>
            </li>
            <li>
                <input type="number" ng-model="entity.attributeOrder" />
            </li>
        </ul>
    </fieldset>
    <fieldset class="format">
        <span>
            <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" />
            <input type="button" class="confirm right" value="{{messageLabels['general.save']}}" ng-click="saveOrUpdate()" />
        </span>
    </fieldset>

    <fieldset ng-hide="entity.uuid == ''" class="format">
        <h3>{{retireOrUnretire}}</h3>
        <p>
            <span ng-show="entity.retired">{{messageLabels['openhmis.inventory.general.retired.reason']}}<b>{{entity.retireReason}}</b><br /></span>
            <span ng-hide="entity.retired"><input type="text" placeholder="{{messageLabels['general.retireReason']}}" style="min-width: 50%;" ng-model="entity.retireReason" ng-disabled="entity.retired" /></span>
            <input type="button" class="cancel" value="{{retireOrUnretire}}" ng-click="retireOrUnretireCall()" />
        </p>
        <p class="checkRequired">{{retireReasonIsRequiredMsg}}</p>
    </fieldset>
    <fieldset ng-hide="entity.uuid == ''" class="format">
        <h3>
            {{messageLabels['delete.forever']}}
        </h3>
        <p>
            <input type="button" class="cancel" value="{{messageLabels['general.purge']}}" ng-click="purge()"/>
        </p>
    </fieldset>
</form>
