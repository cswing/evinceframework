/*
 * Copyright 2013 the original author or authors.
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
    'dojo/_base/declare', 
    'dijit/_TemplatedMixin',
    'dijit/form/ValidationTextBox',
    'dijit/form/Button',
    'dijit/form/CheckBox',
    'evf/ComplexWidget',
    '../rights',
    '../topics', 
    'dojo/text!./templates/LoginForm.html',
    'dojo/i18n!../nls/membership'
], function(declare, Templated, ValidationTextBox, Button, CheckBox, ComplexWidget, rights, topics, template, i18n){
    
    return declare('evfMembership.forms.LoginForm', [ComplexWidget, Templated], {
        
        templateString: template,
        
        loginTextBox: null,
        
        passwordTextBox: null,
        
        rememberMeCheckBox: null,
        
        postMixInProperties: function() {
            this.inherited(arguments);
            this.i18n = i18n;
        },
        
        postCreate: function(){
            this.inherited(arguments);
            
            this.loginTextBox = this.constructWidget(ValidationTextBox, {
                name:           'loginId',
                value:          '',
                placeHolder:    i18n.loginId
            }, this.loginIdNode);
            
            this.passwordTextBox = this.constructWidget(ValidationTextBox, {
                name:           'password',
                value:          '',
                placeHolder:    i18n.password
            }, this.passwordNode);
            
            if(this.hasSecurityRight(rights.rememberMe)) {
                this.rememberMeCheckBox = this.constructWidget(CheckBox, {
                    name: 'rememberMe'
                }, this.rememberMeNode);
                
            } else {
                this.domStyle.set(this.rememberMeNode, 'display', 'none');
            }
            
            this.submitButton = this.constructWidget(Button, {
                label: i18n.loginAction
            }, this.submitNode);
            this.listen(this.submitButton, 'click', function() {
            
                // TODO validate
            
                this.publish(topics.requestAuthentication, { 
                    loginId:    this.loginTextBox.get('value'),
                    password:   this.passwordTextBox.get('value')
                });
            });
            
            // Forgot Password link
            if(this.hasSecurityRight(rights.resetPassword)) {
                this.forgotButton = this.constructWidget(Button, {
                    label: i18n.forgotAction
                }, this.forgotPasswordNode);
            } else {
                this.domStyle.set(this.forgotPasswordNode, 'display', 'none');
            }
            
            // Register link
            if(this.hasSecurityRight(rights.register)) {
                this.forgotButton = this.constructWidget(Button, {
                    label: i18n.registerAction
                }, this.registerNode);
            } else {
                this.domStyle.set(this.registerContainerNode, 'display', 'none');
            }
        }
        
    });
});