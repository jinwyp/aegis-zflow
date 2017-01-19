/**
 * Created by JinWYP on 23/12/2016.
 */


(function(window, jQuery, cytoscape, angular){
    'use strict';

    jQuery.ajaxSettings.async = false;

    var JsonSchemaMetaSchema = {
        "definitions":{
            "schemaArray":{
                "type":"array",
                "minItems":1,
                "items":{
                    "$ref":"#/definitions/schema",
                    "title":"schema"
                }
            },
            "null":{
                "type":{
                    "enum":[
                        "null"
                    ]
                },
                "enum":{
                    "items":{
                        "type":"null"
                    }
                }
            },
            "boolean":{
                "properties":{
                    "default":{
                        "type":"boolean"
                    },
                    "type":{
                        "enum":[
                            "boolean"
                        ]
                    },
                    "enum":{
                        "items":{
                            "type":"boolean"
                        }
                    },
                    "format":{
                        "type":"string",
                        "enum":[
                            "select",
                            "checkbox"
                        ]
                    }
                }
            },
            "string":{
                "properties":{
                    "options":{
                        "properties":{
                            "input_width":{
                                "type":"string"
                            },
                            "input_height":{
                                "type":"string"
                            },
                            "expand_height":{
                                "type":"boolean"
                            },
                            "wysiwyg":{
                                "type":"boolean"
                            }
                        }
                    },
                    "format":{
                        "type":"string",
                        "propertyOrder":20,
                        "enum":[
                            "color",
                            "date",
                            "datetime",
                            "datetime-local",
                            "email",
                            "month",
                            "number",
                            "range",
                            "tel",
                            "text",
                            "textarea",
                            "time",
                            "url",
                            "week",
                            "actionscript",
                            "batchfile",
                            "c",
                            "c++",
                            "cpp",
                            "coffee",
                            "cshart",
                            "css",
                            "dart",
                            "django",
                            "ejs",
                            "erlang",
                            "golang",
                            "groovy",
                            "handlebars",
                            "haskell",
                            "haxe",
                            "html",
                            "ini",
                            "jade",
                            "java",
                            "javascript",
                            "json",
                            "less",
                            "lisp",
                            "lua",
                            "makefile",
                            "markdown",
                            "matlab",
                            "mysql",
                            "objectivec",
                            "pascal",
                            "perl",
                            "pgsql",
                            "php",
                            "python",
                            "r",
                            "ruby",
                            "sass",
                            "scala",
                            "scss",
                            "smarty",
                            "sql",
                            "stylus",
                            "svg",
                            "twig",
                            "vbscript",
                            "xml",
                            "yaml"
                        ]
                    },
                    "default":{
                        "type":"string"
                    },
                    "minLength":{
                        "type":"integer",
                        "minimum":0,
                        "propertyOrder":25
                    },
                    "maxLength":{
                        "type":"integer",
                        "minimum":0,
                        "propertyOrder":30
                    },
                    "pattern":{
                        "type":"string",
                        "format":"regex",
                        "propertyOrder":35
                    },
                    "type":{
                        "enum":[
                            "string"
                        ]
                    },
                    "enum":{
                        "items":{
                            "type":"string"
                        }
                    },
                    "media":{
                        "type":"object",
                        "additionalProperties":false,
                        "properties":{
                            "type":{
                                "type":"string"
                            }
                        }
                    },
                    "template":{
                        "type":"string"
                    }
                }
            },
            "number":{
                "properties":{
                    "options":{
                        "properties":{
                            "input_width":{
                                "type":"string"
                            },
                            "input_height":{
                                "type":"string"
                            },
                            "expand_height":{
                                "type":"boolean"
                            }
                        }
                    },
                    "type":{
                        "enum":[
                            "number"
                        ]
                    },
                    "enum":{
                        "items":{
                            "type":"number"
                        }
                    },
                    "default":{
                        "type":"number"
                    },
                    "minimum":{
                        "type":"number",
                        "propertyOrder":25
                    },
                    "maximum":{
                        "type":"number",
                        "propertyOrder":30
                    },
                    "multipleOf":{
                        "type":"number",
                        "propertyOrder":35
                    }
                }
            },
            "integer":{
                "properties":{
                    "options":{
                        "properties":{
                            "input_width":{
                                "type":"string"
                            },
                            "input_height":{
                                "type":"string"
                            },
                            "expand_height":{
                                "type":"boolean"
                            }
                        }
                    },
                    "default":{
                        "type":"integer"
                    },
                    "minimum":{
                        "type":"number",
                        "propertyOrder":25
                    },
                    "maximum":{
                        "type":"number",
                        "propertyOrder":30
                    },
                    "multipleOf":{
                        "type":"number",
                        "propertyOrder":35
                    },
                    "type":{
                        "enum":[
                            "integer"
                        ]
                    },
                    "enum":{
                        "items":{
                            "type":"integer"
                        }
                    }
                }
            },
            "schemaBase":{
                "type":"object",
                "additionalProperties":true,
                "defaultProperties":[
                    "type"
                ],
                "options":{
                    "type":"object",
                    "options":{
                        "keep_oneof_values":false
                    },
                    "default":{

                    },
                    "properties":{
                        "enum_titles":{
                            "type":"array",
                            "uniqueItems":true,
                            "format":"table",
                            "items":{
                                "type":"string",
                                "title":"title"
                            }
                        },
                        "grid_columns":{
                            "type":"integer",
                            "minimum":1,
                            "maximum":12
                        },
                        "hidden":{
                            "type":"boolean"
                        },
                        "keep_oneof_values":{
                            "type":"boolean"
                        }
                    }
                },
                "properties":{
                    "type":{
                        "propertyOrder":1,
                        "type":"string",
                        "options":{
                            "hidden":true
                        }
                    },
                    "id":{
                        "type":"string",
                        "format":"uri"
                    },
                    "$schema":{
                        "type":"string",
                        "format":"uri"
                    },
                    "title":{
                        "type":"string",
                        "propertyOrder":2
                    },
                    "description":{
                        "type":"string",
                        "propertyOrder":4
                    },
                    "default":{
                        "propertyOrder":10
                    },
                    "enum":{
                        "type":"array",
                        "minItems":1,
                        "uniqueItems":true,
                        "propertyOrder":50
                    },
                    "enumSource":{
                        "oneOf":[
                            {
                                "title":"Simple Source",
                                "type":"string"
                            },
                            {
                                "title":"Complex Source",
                                "type":"array",
                                "format":"tabs",
                                "minItems":1,
                                "items":{
                                    "oneOf":[
                                        {
                                            "title":"Constant Values",
                                            "type":"array",
                                            "format":"table",
                                            "uniqueItems":true,
                                            "items":{
                                                "type":"string"
                                            }
                                        },
                                        {
                                            "title":"Source",
                                            "type":"object",
                                            "additionalProperties":false,
                                            "required":[
                                                "source"
                                            ],
                                            "defaultProperties":[
                                                "source"
                                            ],
                                            "properties":{
                                                "source":{
                                                    "oneOf":[
                                                        {
                                                            "title":"Watched Field",
                                                            "type":"string"
                                                        },
                                                        {
                                                            "title":"Constant Values",
                                                            "type":"array",
                                                            "format":"table",
                                                            "minItems":1,
                                                            "items":{
                                                                "title":"value",
                                                                "type":"object",
                                                                "additionalProperties":false,
                                                                "properties":{
                                                                    "value":{
                                                                        "type":"string"
                                                                    },
                                                                    "title":{
                                                                        "type":"string"
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    ]
                                                },
                                                "slice":{
                                                    "type":"array",
                                                    "format":"table",
                                                    "minItems":2,
                                                    "maxItems":2,
                                                    "items":{
                                                        "type":"integer"
                                                    }
                                                },
                                                "filter":{
                                                    "type":"string"
                                                },
                                                "title":{
                                                    "type":"string"
                                                },
                                                "value":{
                                                    "type":"string"
                                                }
                                            }
                                        }
                                    ]
                                }
                            }
                        ]
                    },
                    "$ref":{
                        "type":"string"
                    },
                    "oneOf":{
                        "$ref":"#/definitions/schemaArray"
                    },
                    "anyOf":{
                        "$ref":"#/definitions/schemaArray"
                    },
                    "allOf":{
                        "$ref":"#/definitions/schemaArray"
                    },
                    "not":{
                        "$ref":"#/definitions/schema"
                    },
                    "propertyOrder":{
                        "type":"number",
                        "default":1000
                    },
                    "links":{
                        "type":"array",
                        "items":{
                            "type":"object",
                            "title":"link",
                            "additionalProperties":false,
                            "properties":{
                                "rel":{
                                    "type":"string"
                                },
                                "href":{
                                    "type":"string",
                                    "format":"url"
                                },
                                "class":{
                                    "type":"string"
                                },
                                "download":{
                                    "oneOf":[
                                        {
                                            "type":"boolean"
                                        },
                                        {
                                            "type":"string"
                                        }
                                    ]
                                },
                                "mediaType":{
                                    "type":"string"
                                }
                            }
                        }
                    },
                    "watch":{
                        "type":"object",
                        "patternProperties":{
                            ".*":{
                                "type":"string"
                            }
                        }
                    },
                    "headerTemplate":{
                        "type":"string"
                    }
                }
            },
            "object":{
                "defaultProperties":[
                    "properties"
                ],
                "properties":{
                    "options":{
                        "properties":{
                            "collapsed":{
                                "type":"boolean"
                            },
                            "disable_collapse":{
                                "type":"boolean"
                            },
                            "disable_edit_json":{
                                "type":"boolean"
                            },
                            "disable_properties":{
                                "type":"boolean"
                            },
                            "remove_empty_properties":{
                                "type":"boolean"
                            },
                            "layout":{
                                "type":"string",
                                "enum":[
                                    "grid"
                                ]
                            }
                        }
                    },
                    "default":{
                        "type":"object"
                    },
                    "type":{
                        "enum":[
                            "object"
                        ]
                    },
                    "properties":{
                        "type":"object",
                        "patternProperties":{
                            ".*":{
                                "$ref":"#/definitions/schema"
                            }
                        }
                    },
                    "patternProperties":{
                        "type":"object",
                        "patternProperties":{
                            ".*":{
                                "$ref":"#/definitions/schema"
                            }
                        }
                    },
                    "additionalProperties":{
                        "type":"boolean"
                    },
                    "required":{
                        "type":"array",
                        "uniqueItems":true,
                        "format":"table",
                        "items":{
                            "type":"string",
                            "title":"property"
                        }
                    },
                    "format":{
                        "type":"string",
                        "enum":[
                            "grid"
                        ]
                    }
                }
            },
            "array":{
                "defaultProperties":[
                    "items"
                ],
                "properties":{
                    "options":{
                        "properties":{
                            "collapsed":{
                                "type":"boolean"
                            },
                            "disable_array_add":{
                                "type":"boolean"
                            },
                            "disable_array_delete":{
                                "type":"boolean"
                            },
                            "disable_array_delete_all_rows":{
                                "type":"boolean"
                            },
                            "disable_array_delete_last_row":{
                                "type":"boolean"
                            },
                            "disable_array_reorder":{
                                "type":"boolean"
                            },
                            "disable_collapse":{
                                "type":"boolean"
                            }
                        }
                    },
                    "default":{
                        "type":"array"
                    },
                    "type":{
                        "enum":[
                            "array"
                        ]
                    },
                    "items":{
                        "$ref":"#/definitions/schema"
                    },
                    "uniqueItems":{
                        "type":"boolean"
                    },
                    "minItems":{
                        "type":"integer",
                        "minimum":0
                    },
                    "maxItems":{
                        "type":"integer",
                        "minimum":0
                    },
                    "additionalItems":{
                        "$ref":"#/definitions/schema"
                    },
                    "format":{
                        "type":"string",
                        "enum":[
                            "array",
                            "table",
                            "tabs",
                            "checkbox",
                            "select"
                        ]
                    }
                }
            },
            "schema":{
                "$ref":"#/definitions/schemaBase",
                "options":{
                    "keep_oneof_values":false
                },
                "default":{
                    "type":"object"
                },
                "oneOf":[
                    {
                        "title":"String",
                        "$ref":"#/definitions/string"
                    },
                    {
                        "title":"Number",
                        "$ref":"#/definitions/number"
                    },
                    {
                        "title":"Integer",
                        "$ref":"#/definitions/integer"
                    },
                    {
                        "title":"Boolean",
                        "$ref":"#/definitions/boolean"
                    },
                    {
                        "title":"Object",
                        "$ref":"#/definitions/object"
                    },
                    {
                        "title":"Array",
                        "$ref":"#/definitions/array"
                    },
                    {
                        "title":"Null",
                        "$ref":"#/definitions/null"
                    }
                ]
            }
        },
        "title":"JSON Schema",
        "$ref":"#/definitions/schema",
        "properties":{
            "definitions":{
                "type":"object",
                "patternProperties":{
                    ".*":{
                        "$ref":"#/definitions/schema"
                    }
                }
            }
        }
    };


    var testData2 = {

        nodes : [
            {
                classes : "node isFirstNode ",
                data : {
                    id : 'Start',
                    description : '',
                    program : ''
                }
            },

            {
                classes : "node isLastNode ",
                data : {
                    id : 'End',
                    description : '',
                    program : ''
                }
            }
        ],

        edges    : [
            {
                classes : "edge",
                data : {
                    id : 'E1',
                    description : '',
                    source : 'Start',
                    target : 'End',
                    "userTasks"  : [],
                    "partGTasks" : [],
                    "partUTasks" : [],
                    "autoTasks"  : [],
                    "allTask"  : []
                }
            }
        ],

        formattedSource : {
            "userTasks"  : [],
            "partGTasks" : [],
            "partUTasks" : [],
            "autoTasks"  : [],
            "allTask"  : [],

            points : []
        }
    };



    angular.module('flowApp', ['cgNotify']);

    angular.module('flowApp').controller('vertexController', vertexController);


    function vertexController ($scope, notify){

        notify.config({
            position: 'right',
            duration: 20000,
            templateUrl : '/lib/angular-notify/angular-notify.html'
        });


        var vm = this;

        var cytoscapeChart;
        var formattedData;
        var JSONSchemaEditor;

        var nodeIdList = [];
        var edgeIdList = [];
        var taskIdList = [];
        var pointIdList = [];

        vm.ouputData = '';

        vm.flow = {
            globalConfig : {
                name : '',
                initial : 'Start',  // 代表初始节点
                timeout: 100, // 超时时间配置(图的属性)
                flowType: '',  //流程类型
                persistent: false // 代表初始节点
            },
            points : [],
            edges : [],
            nodes : []
        };



        vm.selectType = 'node';
        vm.taskEditType = 'list';
        vm.pointEditType = '';

        vm.isNewNode = true;
        vm.taskTypeList = ['autoTasks', 'userTasks', 'partUTasks', 'partGTasks'];

        vm.errorAddNewNode = {
            notSelected : false,
            nodeExist : false,
            edgeExist : false,
            nodeSelf : false,
            ajax : false
        };
        vm.errorAddNewTask = {
            taskExist : false,
            ajax : false
        };
        vm.errorAddNewPoint = {
            pointExist : false,
            ajax : false
        };

        vm.currentNode = {
            data : {
                id : '',
                description : '',
                program : ''
            }
        };
        vm.currentEdge = {
            data : {
                id : '',
                description : '',
                source : '',
                target : '',
                userTasks  : [],
                partGTasks : [],
                partUTasks : [],
                autoTasks  : [],
                allTask  : []
            }
        };
        vm.currentTask = {};
        vm.currentPoint = {};

        vm.newNode = {
            classes : "node ",
            data : {
                id : '',
                description : '',
                program : ''
            }
        };

        vm.newEdge = {
            classes : "edge ",
            data : {
                id : '',
                description : '',
                source : '',
                target : '',
                userTasks  : [],
                partGTasks : [],
                partUTasks : [],
                autoTasks  : [],
                allTask  : []
            }
        };
        vm.newTask = {
            classes : 'node task ',
            data : {
                id : '',
                type : '',
                description : '',
                points : [],
                tasks : [],

                belongToEdge : {
                    id : '',
                    source : '',
                    target : ''
                }
            }
        };

        vm.newPoint = {
            id : '',
            description : '',
            JSONSchema : {
                "title": "ExampleSchema",
                "type": "object",
                "properties": {
                    "demoFieldName": {
                        "type": "string"
                    }
                }
            }
        };
        vm.newPointToTask = ''


        vm.saveGlobal = function (form) {
            console.log(vm.flow.globalConfig)
            if (form.$valid){

            }
        }


        vm.addNewEdge = function (form){
            console.log(vm.newNode)
            if (form.$valid){

                // 判断是否选中节点
                if (vm.currentNode.data.id){
                    vm.errorAddNewNode.notSelected = false;
                }else{
                    vm.errorAddNewNode.notSelected = true;
                    return;
                }

                // 判断新添加的线是否ID已存在
                if (edgeIdList.indexOf(vm.newEdge.data.id) > -1 ){
                    vm.errorAddNewNode.edgeExist = true;
                    return;
                }else{
                    vm.errorAddNewNode.edgeExist = false;
                }


                // 判断是否插入新节点
                if (vm.isNewNode){

                    // 判断新节点ID是否存在
                    if (nodeIdList.indexOf(vm.newNode.data.id) > -1 ){
                        vm.errorAddNewNode.nodeExist = true;
                        return;
                    }else{
                        vm.errorAddNewNode.nodeExist = false;
                        nodeIdList.push(vm.newNode.data.id)
                    }


                }else{
                    // 判断新节点ID是否和出发点是同一个点
                    if (vm.currentNode.id === vm.newNode.data.id ){
                        vm.errorAddNewNode.nodeSelf = true;
                        return;
                    }else{
                        vm.errorAddNewNode.nodeSelf = false;
                    }
                }


                var newTempNode = {
                    group: "nodes",
                    classes : 'node ',
                    data : {
                        id : vm.newNode.data.id,
                        description : vm.newNode.data.description,
                        program : ''
                    }
                };

                var newTempEdge = {
                    group: "edges",
                    classes : 'edge ',
                    data : {
                        id : vm.newEdge.data.id,
                        source : vm.currentNode.data.id,
                        target : vm.newNode.data.id,
                        allTask : [],
                        userTasks : [],
                        autoTasks : [],
                        partUTasks : [],
                        partGTasks : []
                    }
                };


                if (edgeIdList.indexOf(newTempEdge.data.id) === -1 ){
                    edgeIdList.push(newTempEdge.data.id)
                }


                if (vm.isNewNode){
                    vm.flow.nodes.push(newTempNode)
                    cytoscapeChart.add(newTempNode);
                }

                vm.flow.edges.push(newTempEdge)
                cytoscapeChart.add(newTempEdge);

                vm.newEdge.data.id = ''
                vm.newNode.data.id = ''
                vm.newNode.data.description = '';
                form.$setPristine();
                form.$setUntouched();

                cytoscapeChart.layout(cytoscapeChart.getConfig({}).layout);

                notify({
                    classes: 'alert-success',
                    message: '保存成功!'
                });
            }

        }


        vm.addNewTask = function(form){
            if (form.$valid){

                if (taskIdList.indexOf(vm.newTask.data.id) > -1 ){
                    vm.errorAddNewTask.taskExist = true;
                    return;
                }else{
                    vm.errorAddNewTask.taskExist = false;
                    taskIdList.push(vm.newTask.data.id)
                }


                var newTempTask = {
                    classes : 'node task ' + vm.newTask.data.type,
                    data : {
                        id : vm.newTask.data.id,
                        type : vm.newTask.data.type,
                        description : vm.newTask.data.description,
                        points : [],
                        belongToEdge : {
                            id : vm.currentEdge.data.id,
                            source : vm.currentEdge.data.source,
                            target : vm.currentEdge.data.target
                        }
                    }
                };

                vm.taskTypeList.forEach(function(type, typeIndex){
                    if (vm.newTask.data.type === type){
                        vm.currentEdge.data[type].push(newTempTask);
                    }
                })

                vm.currentEdge.data.allTask.push(newTempTask);

                cytoscapeChart.getElementById( vm.currentEdge.data.id ).data(vm.currentEdge.data);

                vm.flow.edges.forEach(function (edge, edgeIndex) {
                    if (edge.data.id === vm.currentEdge.data.id){
                        edge.data = vm.currentEdge.data
                    }
                })

                console.log(vm.flow.edges)
                notify({
                    classes: 'alert-success',
                    message: '保存成功!'
                });

            }
        }


        vm.addNewPoint = function(form){

            if (form.$valid){
                if (pointIdList.indexOf(vm.newPoint.id) > -1 ){
                    vm.errorAddNewPoint.pointExist = true;
                    return;
                }else{
                    vm.errorAddNewPoint.pointExist = false;
                    pointIdList.push(vm.newPoint.id)
                }
            }

            var tempPoint = {
                id : vm.newPoint.id,
                description : vm.newPoint.description,
                JSONSchema : {
                    "type": "object",
                    "properties": vm.newPoint.JSONSchema.properties
                }

            };
            vm.flow.points.push(tempPoint);

            notify({
                classes: 'alert-success',
                message: '保存成功!'
            });

        }

        vm.addPointIntoTask = function(task){
            if (pointIdList.indexOf(vm.newPointToTask) === -1 ){
                alert('没有改点' + vm.newPointToTask)
                return;
            }

            var tempPointIdList = task.data.points.map(function(point, pointIndex){
                return point.id
            })

            if (tempPointIdList.indexOf(vm.newPointToTask) > -1 ){
                alert('已经添加过该点:' + vm.newPointToTask)
                return;
            }

            vm.flow.points.forEach(function(point, pointIndex){
                if (point.id === vm.newPointToTask){
                    task.data.points.push(point)
                }
            })

            console.log(task)
        }


        vm.changeSelectType = function(type){
            vm.selectType = type;
        }
        vm.changeTaskEditType = function(type){
            vm.taskEditType = type;
        }
        vm.changePointEditType = function(type){
            vm.pointEditType = type;
        }

        function jsonEditor (id, schema){
            return new JSONEditor(document.getElementById(id),{
                schema : JsonSchemaMetaSchema,
                startval : schema,
                theme : 'bootstrap3'
            });
        }
        vm.selectPoint = function(type, point){
            vm.pointEditType = type;
            vm.currentPoint = point;
            if (JSONSchemaEditor) JSONSchemaEditor.destroy();
            JSONSchemaEditor = jsonEditor('json_editor_box', vm.currentPoint.JSONSchema)
        }
        vm.savePointSchema = function(type, point){
            console.log(JSONSchemaEditor.getValue());
            vm.currentPoint.JSONSchema.properties = JSONSchemaEditor.getValue().properties
        }




        vm.convertDataArray = function () {
            vm.ouputData = formatter.cyArrayToRawArray(vm.flow.nodes, vm.flow.edges, vm.flow.points)
            console.log(vm.ouputData)
        }

        vm.convertDataObj = function () {
            vm.ouputData = formatter.rawArrayToObj(formatter.cyArrayToRawArray(vm.flow.nodes, vm.flow.edges, vm.flow.points))
            console.log(vm.ouputData)
        }





        function chartEventCallback (cy){

            cy.nodes('.node').qtip({
                content: function(){
                    return this.data().description || this.data().id;
                },
                show: {
                    event: 'click'
                },
                hide: {
                    event: 'unfocus'
                },
                position: {
                    my: 'bottom center',
                    at: 'top center'
                },
                style: {
                    classes: 'qtip-bootstrap',
                    tip: {
                        width: 16,
                        height: 8
                    }
                }
            })

            cy.edges('.edge').qtip({
                content: function(){
                    return this.data().id;
                },
                show: {
                    event: 'click'
                },
                hide: {
                    event: 'unfocus'
                },
                position: {
                    my: 'bottom center',
                    at: 'top center'
                },
                style: {
                    classes: 'qtip-bootstrap',
                    tip: {
                        width: 16,
                        height: 8
                    }
                }
            })



            cy.on('click', 'node', function(evt){
                console.log('node:', this.data())
                vm.currentNode.data = this.data();
                vm.selectType = 'node';
                $scope.$apply();
            })


            cy.on('click', 'edge', function(evt){
                console.log('edge:', this.data())
                vm.currentEdge.data = this.data();
                vm.selectType = 'edge';
                $scope.$apply();
            })

        };



        var app = {
            init : function(){
                jQuery.getJSON('/json/data5.json', function(resultData){

                    formattedData = cytoscapeFormatterObjectToArray(resultData)
                    formattedData = testData2
                })
                this.drawChart();
            },
            drawChart : function(){
                var configChart = {
                    domId : 'chart',
                    eventCB : chartEventCallback
                };

                cytoscapeChart = new flowChart2(formattedData, configChart);
                // cytoscapeChart.center()
                // cytoscapeChart.pan({
                //     x: 10,
                //     y: 10
                // });

                console.log(cytoscapeChart.width())


                vm.flow.edges = formattedData.edges;
                vm.flow.nodes = formattedData.nodes;
                vm.flow.points = formattedData.formattedSource.points;

                console.log(vm.flow)

                nodeIdList = formattedData.nodes.map(function(vertex, vertexIndex){
                    return vertex.data.id
                })
                edgeIdList = formattedData.edges.map(function(edge, edgeIndex){
                    return edge.data.id
                })
                taskIdList = formattedData.formattedSource.allTask.map(function(task, taskIndex){
                    return task.data.id
                })
                pointIdList = formattedData.formattedSource.points.map(function(point, pointIndex){
                    return point.data.id
                })
            }
        };

        app.init();

    }



})(window, jQuery, cytoscape, angular);




