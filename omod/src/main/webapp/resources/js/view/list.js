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
define(
    [
        openhmis.url.backboneBase + 'js/lib/backbone',
        openhmis.url.backboneBase + 'js/lib/backbone-forms',
        openhmis.url.backboneBase + 'js/lib/backbone.bootstrap-modal',
        openhmis.url.backboneBase + 'js/view/list'
    ],
    function (Backbone) {

        var Form = Backbone.Form,
            editors = Form.editors;

        /**
         * LIST
         *
         * An array editor. Creates a list of other editor items.
         *
         * Special options:
         * @param {String} [options.schema.itemType]          The editor type for each item in the list. Default: 'Text'
         * @param {String} [options.schema.confirmDelete]     Text to display in a delete confirmation dialog. If falsey, will not ask for confirmation.
         */
        editors.InventoryList = editors.List.extend({

            /**
             * Add a new item to the list
             * @param {Mixed} [value]           Value for the new item editor
             * @param {Boolean} [userInitiated] If the item was added by the user clicking 'add'
             */
            addItem: function (value, userInitiated) {
                var self = this;

                //Create the item
                var item = new editors.InventoryList.Item({
                    list: this,
                    schema: this.schema,
                    value: value,
                    Editor: this.Editor,
                    key: this.key
                }).render();

                var _addItem = function () {
                    self.items.push(item);
                    self.$list.append(item.el);

                    item.editor.on('all', function (event) {
                        if (event == 'change') {
                            return;
                        }

                        // args = ["key:change", itemEditor, fieldEditor]
                        args = _.toArray(arguments);
                        args[0] = 'item:' + event;
                        args.splice(1, 0, self);
                        // args = ["item:key:change", this=listEditor, itemEditor, fieldEditor]

                        editors.InventoryList.prototype.trigger.apply(this, args);
                    }, self);

                    item.editor.on('change', function () {
                        if (!item.addEventTriggered) {
                            item.addEventTriggered = true;
                            this.trigger('add', this, item.editor);
                        }
                        this.trigger('item:change', this, item.editor);
                        this.trigger('change', this);
                    }, self);

                    item.editor.on('focus', function () {
                        if (this.hasFocus) {
                            return;
                        }

                        this.trigger('focus', this);
                    }, self);
                    item.editor.on('blur', function () {
                        if (!this.hasFocus) {
                            return;
                        }

                        var self = this;
                        setTimeout(function () {
                            if (_.find(self.items, function (item) {
                                    return item.editor.hasFocus;
                                })) {
                                return;
                            }

                            self.trigger('blur', self);
                        }, 0);
                    }, self);

                    if (userInitiated || value) {
                        item.addEventTriggered = true;
                    }

                    if (userInitiated) {
                        self.trigger('add', self, item.editor);
                        self.trigger('change', self);
                    }
                };

                //Check if we need to wait for the item to complete before adding to the list
                if (this.Editor.isAsync) {
                    item.editor.on('readyToAdd', _addItem, this);
                } else {
                    //Most editors can be added automatically
                    _addItem();
                }

                return item;
            }
        });

        /**
         * A single item in the list
         *
         * @param {editors.List} options.list The List editor instance this item belongs to
         * @param {Function} options.Editor   Editor constructor function
         * @param {String} options.key        Model key
         * @param {Mixed} options.value       Value
         * @param {Object} options.schema     Field schema
         */
        editors.InventoryList.Item = editors.List.Item.extend({
            events: {
                'click [data-action="remove"]': function (event) {
                    event.preventDefault();
                    var name = this.key;
                    name =name.substring(0, name.length -1);
                    var message='Are you sure you want to delete this';
                    if(name == 'price'){
                        if(confirm(message+' price '+this.value+' ?')){
                            this.list.removeItem(this);
                        }
                    }else if(name == 'code'){
                        if(confirm(message+' code '+this.value+'?')){
                            this.list.removeItem(this);
                        }
                    }else {
                        if(confirm(message+' '+this.key+' ?')){
                            this.list.removeItem(this);
                        }
                    }
                }
            }
        });



    }
);
