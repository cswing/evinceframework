/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([
    "dojo/_base/lang",
    "evf/dataTypeFactory",
    "dijit/form/ValidationTextBox"
], function(lang, factory, ValidationTextBox) {

    var dataType = factory.registerDataType({
        key:            'text',
        widgetFactory:  function(node, context) {
            var prms = {
                value:      context.value,
                required:   context.required
            };
            if(context.pattern) {
                prms.pattern = context.pattern;
            }
            
            return new ValidationTextBox(prms, node);
        }
    });
    
    dojo.setObject("evf.dataTypes.text", dataType);

    /*=====
    dataType.__Context = declare(null, {
        // value: String?
        //      the initial value to set on the textbox
        // required: Boolean
		//		user is required to enter data into this field
        // pattern: String
		//		pattern string used to validate the input
    });
    =====*/
    
    return dataType;
});