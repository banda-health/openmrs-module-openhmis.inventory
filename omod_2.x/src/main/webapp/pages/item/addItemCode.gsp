<div>
    <ul class="table-layout">
        <li>Code</li>
        <li><input type="text" ng-model="itemCode.code" /></li>
    </ul>
    <div class="ngdialog-buttons">
        <button type="button" class="ngdialog-button ngdialog-button-secondary" ng-click=closeThisDialog("Cancel")>Cancel</button>
        <button type="button" class="ngdialog-button ngdialog-button-primary" ng-click=confirm("OK")>OK</button>
    </div>
</div>