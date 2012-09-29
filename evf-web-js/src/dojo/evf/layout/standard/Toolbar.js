define([
    "dojo/_base/declare",
    "dijit/_Widget",
    "dijit/_Templated",
    "evf/layout/navigation/menuUtil",
    "dojo/text!./templates/Toolbar.html"
], function(declare, Widget, Templated, menuUtils, template){

    return declare("evf.layout.standard.Toolbar", [Widget, Templated], {
        
        viewModel:      null,
        
        templateString: template,
        
        postMixInProperties: function(){
            this.inherited(arguments);
            this.startupWidgets = [];
        },
        
        postCreate: function(){
            this.inherited(arguments);
            
            var qr = this.viewModel.query({ 
                _type:          'evf.siteNav'
            });
            
            if (qr.length == 0){
                return null;
            }
            
            if (qr.length > 1)
                throw 'multiple navigation mechanisms are not supported.';
            
            menuUtils.buildNavigation(qr[0], this.innerNode, 'toolbarMenuPopup');
        }
    });/*declare*/
});/*define*/