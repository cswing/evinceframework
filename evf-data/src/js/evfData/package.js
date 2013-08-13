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

    // checks if mid is in app/tests directory
    var testResourceRe = /^app\/tests\//,
    
    copyOnly = function(filename, mid){
        // specific files to copy only
        var list = {
            "evfData/package.js": true,
            "evfData/package.json": true
        };
        
        // Check if it is one of the special files, 
        // if it is in app/resource (but not CSS) or is an image
        return (mid in list) ||
            (/^app\/resources\//.test(mid)
                && !/\.css$/.test(filename)) ||
            /(png|jpg|jpeg|gif|tiff)$/.test(filename);
    };
 
    return {
        resourceTags: {
            // identify the tests
            test: function(filename, mid){
                return testResourceRe.test(mid) || mid=="evfData/tests";
            },
            
            copyOnly: function(filename, mid){
                return copyOnly(filename, mid);
                // Tag our copy only files
            },
            
            amd: function(filename, mid){
                return !testResourceRe.test(mid)
                    && !copyOnly(filename, mid)
                    && /\.js$/.test(filename);
                // If it isn't a test resource, copy only,
                // but is a .js file, tag it as AMD
            }
        }
    };
})();