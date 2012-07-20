dependencies = {
	//Strip all console.* calls except console.warn and console.error. This is basically a work-around
	//for trac issue: http://bugs.dojotoolkit.org/ticket/6849 where Safari 3's console.debug seems
	//to be flaky to set up (apparently fixed in a webkit nightly).
	//But in general for a build, console.warn/error should be the only things to survive anyway.
	stripConsole: "normal",
	
	layers: [
		{
			name: "../evf/evf.js",
			copyrightFile: "../../../evf-copyright.txt",
			layerDependencies: [
			],
			dependencies: [
				"evf.evf"
			]
		}
	],

	prefixes: [
		[ "dijit", "../dijit" ],
		[ "dojox", "../dojox" ],
		[ "evf", "../../evf" ]
	]
}