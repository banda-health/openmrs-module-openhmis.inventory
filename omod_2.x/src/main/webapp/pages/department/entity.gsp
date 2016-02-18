<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page")}",
            link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
        { label: "${ ui.message("openhmis.inventory.manage.module")}",
            link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
        { label: "${ ui.message("openhmis.inventory.admin.departments")}",
            link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/department/departments.page'},
        { label: "${ ui.message("openhmis.inventory.general.edit")} ${ui.message("openhmis.inventory.department.name")}"}
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>
<form name="entityForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">
    <h1>{{messageLabels['h2SubString']}}</h1>

    <input type="hidden" ng-model="entity.uuid" />

    <fieldset class="format">
        <ul class="table-layout">
            <li class="required">
                <span>{{messageLabels['general.name']}}</span>
            </li>
            <li>
                <input name="entityName" type="text" ng-model="entity.name" class="maximized" placeholder="{{messageLabels['general.name']}}" required />
                <p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
            </li>
        </ul>
        <ul class="table-layout">
            <li style="vertical-align: top" class="not-required">
                <span>{{messageLabels['general.description']}}</span>
            </li>
            <li>
                <textarea ng-model="entity.description" placeholder="{{messageLabels['general.description']}}" rows="3"
                          cols="40">
                </textarea>
            </li>
        </ul>
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
            <span ng-hide="entity.retired">
                <input type="text" placeholder="{{messageLabels['general.retireReason']}}" style="min-width: 50%;"
                       ng-model="entity.retireReason" ng-disabled="entity.retired" />
            </span>
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
