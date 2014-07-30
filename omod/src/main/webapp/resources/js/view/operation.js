define(
	[
		openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.backboneBase + 'js/view/openhmis',
		openhmis.url.inventoryBase + 'js/model/operation',
		openhmis.url.inventoryBase + 'js/model/stockroom',
		'link!' + openhmis.url.inventoryBase + 'css/style.css'
	],
	function(openhmis) {
		openhmis.OperationTypeEditView = openhmis.CustomizableInstanceTypeAddEditView.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
			tmplSelector: '#detail-template'
		});

        openhmis.MyOperationListView = openhmis.GenericSearchableListView.extend({
            fetch: function(options, sender) {
                options = options ? options : {};

                // The 'my' query is set to return the list of user operations on the operation resource
                options.queryString = openhmis.addQueryStringParameter(options.queryString, "q=my");

                openhmis.GenericListView.prototype.fetch.call(this, options, sender);
            }
        });

        openhmis.OperationItemListItemView = openhmis.GenericListItemView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#operation-item-list-item'
        });

        openhmis.OperationDetailView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#view-operation-detail',

            events: {
                'click .completeOp': 'completeOperation',
                'click .cancelOp': 'cancelOperation',
                'click .cancel': 'cancel'
            },

            initialize: function(options) {
                openhmis.GenericAddEditView.prototype.initialize.call(this, options);

                this.itemsView = new openhmis.GenericListView({
                    model: new openhmis.GenericCollection([], {
                        model: openhmis.OperationItem
                    }),
                    showRetiredOption: false,
                    showRetired: true,
                    listTitle: "Operation Items",
                    listFields: ['item', 'quantity', 'batchOperation', 'expiration'],
                    itemView: openhmis.OperationItemListItemView
                });

                this.itemsView.on("fetch", this.fetch);
            },

            completeOperation: function() {
                // TODO: Ensure that the current user can complete the operation

                // Post the status change and then reload the model
                //  Using the normal save mechanism can result in issues as it sends the entire object hierarchy which
                //  can result in issues with the REST converters
                this.updateStatus("COMPLETED");
            },

            cancelOperation: function() {
                // TODO: Ensure that the current user can cancel the operation

                // Post the status change and then reload the model
                this.updateStatus("CANCELLED");
            },

            prepareModelForm: function(model, options) {
              // We don't want to use the backbone form as we're just displaying the operation information here
                return undefined;
            },

            fetch: function(options) {
                options.queryString = openhmis.addQueryStringParameter(options.queryString, "operation_uuid=" + this.model.id);
            },

            render: function() {
                openhmis.GenericAddEditView.prototype.render.call(this);

                if (this.model.id) {
                    // Fetch and render the operation items list
                    this.itemsView.fetch(undefined, undefined);

                    var itemsEl = $("#operation-items");
                    itemsEl.append(this.itemsView.el);
                    itemsEl.show();
                }
            },

            updateStatus: function(status) {
                var self = this;

                // Post the status change using the raw ajax request. This just sends up the changed property, status.
                $.ajax({
                    type: 'POST',
                    url: this.model.url(),
                    data: '{"status":"' + status + '"}',
                    success: function(data) {
                        // Fetch the updated model
                        self.model.fetch({
                            success: function() {
                                // Once the fetch is complete, sync the changes back to the list and close this edit view
                                self.model.trigger("sync");

                                self.cancel();
                            }
                        });
                    },
                    error: function(model, resp) { openhmis.error(resp); },
                    contentType: "application/json",
                    dataType: 'json'
                });
            }
        });

        openhmis.NewOperationView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#new-operation',

            events: {
                'click .cancel': 'cancel'
            },

            prepareModelForm: function(model, options) {
                // We don't want to use the backbone form as we are controlling the whole layout
                return undefined;
            },

            showDialog: function() {
                var dialog = $()
                // Render new operation form
                this.render();

                // Turn display form as dialog
                this.$el.dialog();
            }
        });

		return openhmis;
	}
);
