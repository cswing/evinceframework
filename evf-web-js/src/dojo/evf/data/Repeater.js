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
 	'dojo/_base/array',
 	'dojo/_base/declare',
	'dijit/_Container',
	'evf/ComplexWidget',
    'evf/data/util'
], function(array, declare, Container, ComplexWidget, dataUtil) {

	return declare('evf.data.Repeater', [ComplexWidget, Container], {
		// summary:
		//		A container widget that will render an instance of the itemClass
		//		for each item that is in the dataSource.

        itemClass: null,

        dataSource: null,

        postCreate: function(){
            this.inherited(arguments);

            this.domClass.add(this.domNode, 'evfRepeater')

            var ItemCtor = this.ensureCtor(this.itemClass);
            array.forEach(this.dataSource.data, this.hitch(function(itm, idx) {
                var w = new ItemCtor({
                    item: itm,
                    dataSource: this.dataSource,
                    index: idx
                });

                dataUtil.applyRowCss(w.domNode, 'evfRepeaterRow', idx, this.dataSource.data.length);

                this.addChild(w);
            }));
        }
    });
});