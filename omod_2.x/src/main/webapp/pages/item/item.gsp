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
            <h3>${ui.message('openhmis.inventory.department.name')}</h3>
        </li>
        <li>
            <select>
                <option ng-repeat="department in departments track by department.uuid" ng-selected="entity.department.name == department.name">
                    {{department.name}}
                </option>
            </select>
        </li>
    </ul>
    <ul class="table-layout">
        <li>
            <h3>${ui.message('openhmis.inventory.item.hasExpiration')}</h3>
        </li>
        <li>
            <input type="checkbox" ng-model="entity.hasExpiration"  />
        </li>
    </ul>
    <ul class="table-layout" ng-show="entity.hasExpiration">
        <li>
            <h3>${ui.message('openhmis.inventory.item.defaultExpirationPeriod')}</h3>
        </li>
        <li>
            <input type="number" ng-model="entity.defaultExpirationPeriod" />
        </li>
    </ul>
    <ul class="table-layout">
        <li>
            <h3>${ui.message('Concept')}</h3>
        </li>
        <li>
            <input type="text" ng-change="searchConcepts()" ng-model="entity.concept"
                   placeholder="{{messageLabels['openhmis.inventory.item.enterConceptName']}}"
                   typeahead="concept.display for concept in concepts"
                   typeahead-no-results="noResults" class="form-control"
                   typeahead-loading="loadingConcepts"
            />
            <i ng-show="loadingConcepts"></i>
            <div ng-show="noResults">
                <i></i> No Results Found
            </div>
        </li>
    </ul>
    <ul class="table-layout">
        <li>
            <h3>${ui.message('openhmis.inventory.item.hasPhysicalInventory')}</h3>
        </li>
        <li>
            <input type="checkbox" ng-model="entity.hasPhysicalInventory" />
        </li>
    </ul>
    <ul class="table-layout">
        <li>
            <h3>${ui.message('openhmis.inventory.item.minimumQuantity')}</h3>
        </li>
        <li>
            <input type="text" ng-model="entity.minimumQuantity" />
        </li>
    </ul>
    <ul class="table-layout">
        <li>
            <h3>${ui.message('openhmis.inventory.item.buyingPrice')}</h3>
        </li>
        <li>
            <input type="text" ng-model="entity.buyingPrice" />
        </li>
    </ul>
    <ul class="table-layout">
        <li>
            <h3>${ui.message('openhmis.inventory.item.code.namePlural')}</h3>
        </li>
        <li>
            <div class="bbf-editor">
                <div class="bbf-list" name="codes">
                    <ul>
                        <li ng-repeat="itemCode in entity.codes track by itemCode.uuid || itemCode.id">
                            <button ng-click="removeItemCode(itemCode)" type="button" data-action="remove" class="bbf-remove" title="Remove">×</button>
                            <div>{{itemCode.code}}</div>
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
        <li>
            <h3>${ui.message('openhmis.inventory.item.prices')}</h3>
        </li>
        <li>
            <div class="bbf-editor">
                <div class="bbf-list" name="prices">
                    <ul>
                        <li ng-repeat="itemPrice in entity.prices track by (itemPrice.uuid || itemPrice.id)">
                            <button ng-click="removeItemPrice(itemPrice)" type="button" data-action="remove" class="bbf-remove" title="Remove">×</button>
                            <div>{{itemPrice.price | number:2}} <span ng-if="itemPrice.name != ''">({{itemPrice.name}})</span></div>
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
        <li>
            <h3>${ui.message('openhmis.inventory.item.defaultPrice')}</h3>
        </li>
        <li>
            <select ng-model="entity.defaultPrice" ng-options='((itemPrice.price | number:2) + " (" + itemPrice.name + ")" ) for itemPrice in entity.prices track by (itemPrice.uuid || itemPrice.id)'>
                <option></option>
            </select>
        </li>
    </ul>
        </fieldset>
    <br />
    <fieldset ng-hide="itemStock == ''" class="format">
        <table style="margin-bottom:5px;">
            <thead>
            <tr>
                <th>Stockroom</th>
                <th>Quantity</th>
            </tr>
            </thead>
            <tbody>
            <tr class="clickable-tr" ng-repeat="stock in itemStock track by stock.uuid" >
                <td>{{stock.stockroom.name}}</td>
                <td>{{stock.quantity}}</td>
            </tr>
            </tbody>
        </table>
        <span style="float:left;">
            <select id="pageSize" ng-model="itemStockLimit" ng-change="loadItemStock()">
                <option value="" ng-if="false"></option>
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
                <option value="100">100</option>
            </select>
        </span>
    </fieldset>
    <fieldset class="format">
    <ul class="table-layout">
        <li>
            <span>
                <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" />
            </span>
        </li>
        <li>
            <span>
                <input type="button" class="confirm right" value="{{messageLabels['general.save']}}"  ng-disabled="entity.name == '' || entity.name == undefined" ng-click="saveOrUpdate()" />
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
