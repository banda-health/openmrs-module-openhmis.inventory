/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
curl(
    { baseUrl: openhmis.url.resources },
    [
        openhmis.url.backboneBase + 'js/lib/jquery',
        openhmis.url.backboneBase + 'js/openhmis',
        openhmis.url.backboneBase + 'js/lib/backbone-forms',
        openhmis.url.inventoryBase + 'js/model/operation',
        openhmis.url.backboneBase + 'js/view/generic',

        openhmis.url.backboneBase + 'js/view/list',
        openhmis.url.backboneBase + 'js/view/editors',
        openhmis.url.inventoryBase + 'js/view/editors',
        openhmis.url.inventoryBase + 'js/view/operation'
    ],
    function($, openhmis) {
        $(function() {
            // Set up new operation link and template
            var collection = new openhmis.GenericCollection([], {
                url: openhmis.Operation.prototype.meta.restUrl,
                model: openhmis.NewOperation
            });

            var newView = new openhmis.NewOperationView({
                collection: collection,
                element: $("#newOperationDialog"),
                loaded: function() {
                    // Display the form once everything is set
                    newView.showForm();
                }
            });

            // When a new operation is saved, load the pending operations page
            newView.on("save", function() {
                window.location = openhmis.url.getPage("inventoryBase") + "inventory.form";
            });
        });
    }
);
