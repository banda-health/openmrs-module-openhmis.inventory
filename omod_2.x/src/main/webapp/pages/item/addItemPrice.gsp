<div>
    <ul class="table-layout">
        <li>{{messageLabels['general.name']}}</li>
        <li><input type="text" ng-model="itemPrice.name" /></li>
    </ul>
    <ul class="table-layout">
        <li>Price</li>
        <li><input type="number" ng-model="itemPrice.price" /></li>
    </ul>
    <div class="ngdialog-buttons">
        <button type="button" class="ngdialog-button ngdialog-button-secondary" ng-click=closeThisDialog("Cancel")>Cancel</button>
        <button type="button" class="ngdialog-button ngdialog-button-primary" ng-click=confirm("OK")>OK</button>
    </div>
</div>