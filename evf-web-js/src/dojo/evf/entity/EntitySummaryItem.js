/*
 * Copyright 2013 Craig Swing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0<div>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([
	'dojo/_base/declare',
	'dijit/_TemplatedMixin',
	'dijit/Destroyable',
	'dijit/layout/TabContainer',
	'dgrid/_StoreMixin',
	'dgrid/Grid', 
	'dgrid/extensions/DijitRegistry',
	'evf/ComplexWidget',
	'evf/_UtilMixin',
	'evf/dialog/util',
	'./AddFieldDialog',
	'./EditFieldDialog',
	'./serviceFactory',
	'./roles',
	'dojo/text!./templates/EntitySummaryItem.html',
	'dojo/i18n!../nls/common',
	'dojo/i18n!./nls/entity'
], function(declare, Templated, Destroyable, TabContainer, StoreMixin, Grid, DijitRegistry, 
        ComplexWidget, UtilMixin, dialogUtil, AddFieldDialog, EditFieldDialog, serviceFactory, roles, template, commonI18N, entityI18N) {

    var _GridSummary = declare([Grid, StoreMixin, DijitRegistry, UtilMixin, Destroyable], {

        store: null,

        entity: null, 

        addWidgetClass: null,
        addWidgetProps: null,
        _addWidget: null,

        editWidgetClass: null,
        editWidgetProps: null,
        _editWidget: null,

        confirmDelete: null,

        startup: function() {
            if(this._started) return;
            this.inherited(arguments);

            this.set('entity', this.entity);
        },

        renderPrimaryActionsHeader: function(td) {
            this.domClass.add(td, 'actionContainer');

            if(this.hasSecurityRole(roles.addField)) {
                var add = this.domConstruct.create('div', { 
                    'class': 'dijitInline iconActionSmall add',
                    title: commonI18N.action_add 
                }, td);
                this.own(this.dojoOn(add, 'click', this.hitch(function() {
                    if(!this._addWidget) {

                        var props = this.dojoLang.mixin({
                            entity: this.entity
                        }, this.addWidgetProps || {});

                        this._addWidget = new this.addWidgetClass(props);
                    }
                    this._addWidget.show();
                })));
            }
        },

        renderPrimaryActions: function(object, data, td, options) {
            this.domClass.add(td, 'actionContainer');
            
            var del = this.domConstruct.create('div', { 'class': 'dijitInline iconActionSmall delete'}, td);

            if(data._canBeDeleted && this.hasSecurityRole(roles.deleteField)) {
                this.own(this.dojoOn(del, 'click', this.hitch(function() {
                    dialogUtil.showConfirmation(this.confirmDelete, this.hitch(function() {
                        this.performDelete(data);
                    }));
                })));
            } else {
                this.domClass.add(del, 'disabled');
            }

            if(this.hasSecurityRole(roles.updateField)) {
                var edit = this.domConstruct.create('div', { 'class': 'dijitInline iconActionSmall edit'}, td);
                this.own(this.dojoOn(edit, 'click', this.hitch(function() {

					if(!this._editWidget) {
						var props = this.dojoLang.mixin({
							entity: this.entity
						}, this.editWidgetProps || {});

						this._editWidget = new this.editWidgetClass(props);
					}

					this._editWidget.show(); // resets form, so set data after calling
					this._editWidget.set('data', data);
                })));
            }
        },

        performDelete: function(data) { throw 'not implemented'; }

    });

    var FieldSummary = declare([_GridSummary], {
        
        addWidgetClass: AddFieldDialog,
        editWidgetClass: EditFieldDialog,

		postMixInProperties: function(){
			this.inherited(arguments);
			this.confirmDelete = entityI18N.deleteField_confirm_msg;
			this.columns = [
				{
					label: '', 
					autoSave: true, 
					sortable: false, 
					renderHeaderCell:   this.hitch('renderPrimaryActionsHeader'),
					renderCell:         this.hitch('renderPrimaryActions')
				},
				{ label: entityI18N.entitySummary_fieldGrid_nameColumn, field: 'name', sortable: false },
				{ label: entityI18N.entitySummary_fieldGrid_dataTypeColumn, field: 'dataType', sortable: false }
			];
		},

        startup: function() {
            this.set('sort', 'name');
            this.inherited(arguments);
        },

		_setEntity: function(entity) {
			this.entity = entity;
			if(!this._started) return;

			var result = this.store.query({ _type: 'evf.meta.entityField', entity: this.entity }, { sort: this.get('sort') });
			this.renderArray(result);
		},

        performDelete: function(field) {
            var fn = serviceFactory.findService(serviceFactory.deleteField);
            return fn && fn(this.entity, field);
        }
    });

	var ViewSummary = declare([_GridSummary], {

		postMixInProperties: function(){
			this.inherited(arguments);
			this.columns = [
				{
					label: '', 
					autoSave: true, 
					sortable: false, 
					renderHeaderCell:   this.hitch('renderPrimaryActionsHeader'),
					renderCell:         this.hitch('renderPrimaryActions')
				},
				{ label: 'Name', field: 'name' }
			];
		},

		postCreate: function(){
			this.inherited(arguments);
		}
	});

    var FormSummary = declare([_GridSummary], {
        
        postMixInProperties: function(){
            this.inherited(arguments);
            this.columns = [
                { label: 'Name', field: 'name' }
            ];
        },

        postCreate: function(){
            this.inherited(arguments);
        }
    });

    var TemplateSummary = declare([_GridSummary], {
        
        postMixInProperties: function(){
            this.inherited(arguments);
            this.columns = [
                { label: 'Name', field: 'name' }
            ];
        },

        postCreate: function(){
            this.inherited(arguments);
        }
    });

	return declare('evf.entity.EntitySummaryItem', [ComplexWidget, Templated], {

        templateString: template,

		titleNode: null,

		descriptionNode: null,

        postMixInProperties: function() {
            this.inherited(arguments);
        },

        postCreate: function() {
            this.inherited(arguments);

            this.stack = this.constructWidget(TabContainer, {
                tabPosition: 'left-h'
            }, this.stackNode);
            this.domStyle.set(this.stack.domNode, 'width', '400px');
            this.domStyle.set(this.stack.domNode, 'height', '200px');

            this.fieldsTab = new FieldSummary({
                title:  entityI18N.entitySummary_title_fields,
                store:  this.viewModel,
                entity: this.item
            });
            this.stack.addChild(this.fieldsTab);

            this.viewsTab = new ViewSummary({
                title: 'Views'
            });
            this.stack.addChild(this.viewsTab);

            this.formsTab = new FormSummary({
                title: 'Forms'
            });
            this.stack.addChild(this.formsTab);
            
            this.templatesTab = new TemplateSummary({
                title: 'Templates'
            });
            this.stack.addChild(this.templatesTab);

        },

        startup: function(){
        	if(this._started) return;
        	this.inherited(arguments);

        	this.set('item', this.item);

            // The grid doesn't render correctly (at least in Chrome) so resize
            // - there is a space between the header and the first data row
            this.stack.resize();
        },

        _setItemAttr: function(item) {
        	this.item = item;
        	if(!this._started) return;

        	var itm = item || {};
        	this.domHtml.set(this.titleNode, itm.name||'');
        	this.domHtml.set(this.descriptionNode, itm.description||'');
        }

    });
});