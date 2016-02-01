<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
        { label: "${ ui.message("openhmis.inventory.manage.module")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
        { label: "${ ui.message("openhmis.inventory.admin.items")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/item/items.page'},
        { label: "Item"}
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>
<form name="itemForm" class="entity-form" ng-class="{'submitted': submitted}">
    <h1>{{messageLabels['h2SubString']}}</h1>

    <input type="hidden" ng-model="entity.uuid" />

    <fieldset class="format">

        <ul class="table-layout">
            <li class="required">
                <span>{{messageLabels['general.name']}}</span>
            </li>
            <li>
                <input type="text" ng-model="entity.name" style="min-width: 50%;" placeholder="{{messageLabels['general.name']}}" required />
                <p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.department.name']}}</span>
            </li>
            <li>
                <select ng-model="entity.department"
                        ng-options='department.name for department in departments track by department.uuid'>
                </select>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.hasExpiration']}}</span>
            </li>
            <li>
                <input type="checkbox" ng-model="entity.hasExpiration"  />
            </li>
        </ul>
        <ul class="table-layout" ng-show="entity.hasExpiration">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.defaultExpirationPeriod']}}</span>
            </li>
            <li>
                <input type="number" ng-model="entity.defaultExpirationPeriod" />
            </li>
        </ul>
        <ul class="table-layout autocomplete-table-layout">
            <li class="not-required">
                <span>{{messageLabels['Concept']}}</span>
            </li>
            <li>
                <input type="text" ng-change="searchConcepts()" ng-model="entity.concept"
                       placeholder="{{messageLabels['openhmis.inventory.item.enterConceptName']}}"
                       typeahead="concept.display for concept in concepts"
                       class="form-control"
                       typeahead-on-select="selectConcept(\$item)"
                       typeahead-loading="loadingConcepts"/>
                <i ng-show="loadingConcepts"></i>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.hasPhysicalInventory']}}</span>
            </li>
            <li>
                <input type="checkbox" ng-model="entity.hasPhysicalInventory" />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.minimumQuantity']}}</span>
            </li>
            <li>
                <input type="text" ng-model="entity.minimumQuantity" />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.buyingPrice']}}</span>
            </li>
            <li>
                <input type="number" ng-model="entity.buyingPrice" />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.code.namePlural']}}</span>
            </li>
            <li>
                <div class="bbf-editor">
                    <div class="bbf-list" name="codes">
                        <ul>
                            <li ng-repeat="itemCode in entity.codes track by itemCode.uuid || itemCode.id">
                                <button ng-click="removeItemCode(itemCode)" type="button" data-action="remove" class="bbf-remove" title="Remove">×</button>
                                <div><a ng-click="editItemCode(itemCode)">{{itemCode.code}}</a></div>
                            </li>
                        </ul>
                        <div class="bbf-actions">
                            <button type="button" data-action="add" ng-click="addItemCode()">Add</button>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="required">
                <span>{{messageLabels['openhmis.inventory.item.prices']}}</span>
            </li>
            <li>
                <div class="bbf-editor">
                    <div class="bbf-list" name="prices">
                        <ul>
                            <li ng-repeat="itemPrice in entity.prices track by (itemPrice.uuid || itemPrice.id)">
                                <button ng-click="removeItemPrice(itemPrice)" type="button" data-action="remove" class="bbf-remove" title="Remove">×</button>
                                <span><a ng-click="editItemPrice(itemPrice)">{{itemPrice.price | number:2}} <span ng-if="itemPrice.name != ''">({{itemPrice.name}})</span></a></span>
                            </li>
                        </ul>
                        <div class="bbf-actions">
                            <button type="button" data-action="add" ng-click="addItemPrice()">Add</button>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.defaultPrice']}}</span>
            </li>
            <li>
                <select ng-model="entity.defaultPrice"
                        ng-options='((itemPrice.price | number:2) + " (" + itemPrice.name + ")" ) for itemPrice in entity.prices track by (itemPrice.uuid || itemPrice.id)'>
                </select>
            </li>
        </ul>

        <div ng-repeat="itemAttributeType in itemAttributeTypes track by itemAttributeType.uuid" ng-init="index = \$index">
            <ul class="table-layout">
                <li class="required" ng-if="itemAttributeType.required">
                    <span>{{itemAttributeType.name}}</span>
                </li>
                <li class="not-required" ng-if="!itemAttributeType.required">
                    <span>{{itemAttributeType.name}}</span>
                </li>
                <li>
                    <input ng-if="itemAttributeType.required" name="attributeType{{index}}" type="text" ng-model="attributes[itemAttributeType.uuid]" required />
                    <input ng-if="!itemAttributeType.required" type="text" ng-model="attributes[itemAttributeType.uuid]" />
                </li>
            </ul>
        </div>
    </fieldset>

    <br />
    <fieldset ng-hide="itemStock == ''" class="format">
        <table style="margin-bottom:5px;">
            <thead>
                <tr>
                    <th>{{messageLabels['openhmis.inventory.stockroom.name']}}</th>
                    <th>{{messageLabels['openhmis.inventory.item.quantity']}}</th>
                </tr>
            </thead>
            <tbody>
                <tr class="clickable-tr" ng-repeat="stock in itemStock track by stock.uuid" >
                    <td>{{stock.stockroom.name}}</td>
                    <td>{{stock.quantity}}</td>
                </tr>
            </tbody>
        </table>
    </fieldset>
    <fieldset class="format">
        <span>
            <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" />
            <input type="button" class="confirm right" value="{{messageLabels['general.save']}}" ng-click="saveOrUpdate()" />
        </span>
    </fieldset>
    <fieldset class="format" ng-hide="entity.uuid == ''">
        <h3>{{retireOrUnretire}}</h3>
        <p>
            <span ng-show="entity.retired">{{messageLabels['openhmis.inventory.general.retired.reason']}}<b>{{entity.retireReason}}</b><br /></span>
            <span ng-hide="entity.retired"><input type="text" placeholder="{{messageLabels['general.retireReason']}}" style="min-width: 50%;" ng-model="entity.retireReason" ng-disabled="entity.retired" /></span>
            <input type="button" class="cancel" value="{{retireOrUnretire}}" ng-click="retireUnretire()" />
        </p>
        <p class="checkRequired" ng-hide="entity.retireReason != '' || retireReasonIsRequiredMsg == '' || retireReasonIsRequiredMsg == undefined">{{retireReasonIsRequiredMsg}}</p>
    </fieldset>
    <fieldset class="format" ng-hide="entity.uuid == ''">
        <h3>
            {{messageLabels['delete.forever']}}
        </h3>
        <p>
            <input type="button" ng-hide="entity.uuid == ''" class="cancel" value="{{messageLabels['general.purge']}}" ng-click="delete()"/>
        </p>
    </fieldset>
</form>
