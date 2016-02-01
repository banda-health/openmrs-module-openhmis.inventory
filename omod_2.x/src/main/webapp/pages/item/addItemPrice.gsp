<div class="dialog-format">
    <h2>{{messageLabels['openhmis.inventory.general.add']}} {{messageLabels['openhmis.inventory.item.price.name']}}</h2>
    <fieldset class="dialog-format">
        <ul class="table-layout dialog-table-layout">
            <li>{{messageLabels['general.name']}}</li>
            <li><input type="text" ng-model="itemPrice.name" /></li>
        </ul>
        <ul class="table-layout dialog-table-layout">
            <li>{{messageLabels['openhmis.inventory.item.price.name']}}</li>
            <li><input type="number" ng-model="itemPrice.price" /></li>
        </ul>
        <div class="ngdialog-buttons">
            <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="closeThisDialog('Cancel')" />
            <input type="button" class="confirm right" value="{{messageLabels['general.save']}}" ng-disabled="itemPrice.price == '' || itemPrice.price == undefined"  ng-click="confirm('OK')" />
        </div>
    </fieldset>
</div>