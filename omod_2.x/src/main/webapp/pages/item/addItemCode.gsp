<div class="dialog-format">
    <h2>{{messageLabels['openhmis.inventory.general.add']}} {{messageLabels['openhmis.inventory.item.code.name']}}</h2>
    <fieldset class="dialog-format">
        <ul class="table-layout dialog-table-layout">
            <li>{{messageLabels['openhmis.inventory.item.code.name']}}</li>
            <li><input type="text" ng-model="itemCode.code" /></li>
        </ul>
        <div class="ngdialog-buttons">
            <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="closeThisDialog('Cancel')" />
            <input type="button" class="confirm right" value="{{messageLabels['general.save']}}"  ng-click="confirm('OK')" />
        </div>
    </fieldset>
</div>