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
 var profile = (function(){

    var baseRequires = [
        "evf/_lang",
        "evf/dataTypeFactory",
        "evf/dataTypes/defaults",
        "evf/dialog/Dialog",
        "evf/dialog/util",
        "evf/layout/Messages",
        "evf/store/ViewModel"
    ];

    return {
        basePath:       "./",
        releaseDir:     "../release",
        action:         "release",
        //layerOptimize:  "closure",
        //optimize:       "closure",
        cssOptimize:    "comments",
        selectorEngine: "acme",
 
        packages:[{
            name: "dojo",
            location: "dojo"
        },{
            name: "dijit",
            location: "dijit"
        },{
            name: "dojox",
            location: "dojox"
        },{
            name: "evf",
            location: "evf"
        }],
 
        layers: {
            "evf/core": {
                copyrightFile: "../../../evf-copyright.txt",
                include:    baseRequires,
                customBase: true,
                boot:       true
            },
            "evf/evf": {
                copyrightFile: "../../../evf-copyright.txt",
                include:    baseRequires
            }
        }
    };
})();