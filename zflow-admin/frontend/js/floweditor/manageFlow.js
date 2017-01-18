/**
 * Created by JinWYP on 23/12/2016.
 */


(function(window, jQuery, cytoscape, angular){
    'use strict';

    jQuery.ajaxSettings.async = false;

    var testData2 = {
        globalConfig : {
            name : '流程名称',
            initial : 'Start',  // 代表初始节点
            timeout: 100, // 超时时间配置(图的属性)
            flowType: '',  //流程类型
            persistent: false // 代表初始节点
        },

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
        ]
    };





    angular.module('flowApp', []);

    angular.module('flowApp').controller('vertexController', vertexController);


    function vertexController ($scope){
        var vm = this;

        var cytoscapeChart;
        var formattedData;

        var vertexIdList = [];
        var edgeIdList = [];
        var taskIdList = [];

        vm.ouputData = {};

        vm.flow = {
            globalConfig : {
                name : '流程名称',
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
        vm.isNewNode = true;
        vm.taskTypeList = ['autoTasks', 'userTasks', 'partUTasks', 'partGTasks'];

        vm.errorAddNewNode = {
            notSelected : false,
            vertexExist : false,
            edgeExist : false,
            vertexSelf : false,
            ajax : false
        };
        vm.errorAddNewTask = {
            taskExist : false,
            ajax : false
        };

        vm.currentNode = {};
        vm.currentEdge = {};
        vm.currentTask = {};

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
                "userTasks"  : [],
                "partGTasks" : [],
                "partUTasks" : [],
                "autoTasks"  : [],
                "allTask"  : []
            }
        };
        vm.newTask = {
            classes : 'node task ',
            data : {
                id : '',
                type : '',
                description : '',
                points : [],

                belongToEdge : {
                    id : '',
                    source : '',
                    target : ''
                }
            }
        };







        vm.addNewLine = function (form){
            console.log(vm.newVertex)
            if (form.$valid){

                if (vm.currentVertex.id){
                    vm.errorAddNewVertex.notSelected = false;
                }else{
                    vm.errorAddNewVertex.notSelected = true;
                    return;
                }

                if (edgeIdList.indexOf(vm.newEdge.id) > -1 ){
                    vm.errorAddNewVertex.edgeExist = true;
                    return;
                }else{
                    vm.errorAddNewVertex.edgeExist = false;
                }

                if (vm.isNewNode){

                    if (vertexIdList.indexOf(vm.newVertex.id) > -1 ){
                        vm.errorAddNewVertex.vertexExist = true;
                        return;
                    }else{
                        vm.errorAddNewVertex.vertexExist = false;
                    }
                }else{
                    if (vm.currentVertex.id === vm.newVertex.id ){
                        vm.errorAddNewVertex.vertexSelf = true;
                        return;
                    }else{
                        vm.errorAddNewVertex.vertexSelf = false;
                    }
                }

                var newTempNode = {
                    group: "nodes",
                    classes : 'node',
                    data : {
                        id : vm.newVertex.id,
                        description : vm.newVertex.description,
                        sourceData : {}
                    },
                    sourceData : {
                        id : vm.newVertex.id,
                        description : vm.newVertex.description,
                        program : ''
                    }
                };
                newTempNode.data.sourceData = newTempNode.sourceData;

                var newTempEdge = {
                    group: "edges",
                    classes : 'edge',
                    data : {
                        id : vm.newEdge.id,
                        source : vm.currentVertex.id,
                        target : vm.newVertex.id,
                        sourceData : {}
                    },
                    sourceData : {
                        id : vm.newEdge.id,
                        source : vm.currentVertex.id,
                        target : vm.newVertex.id,
                        allTask : [],
                        userTasks : [],
                        autoTasks : [],
                        partUTasks : [],
                        partGTasks : []
                    }
                };
                newTempEdge.data.sourceData = newTempEdge.sourceData;


                if (vertexIdList.indexOf(vm.newEdge.id) === -1 ){
                    vertexIdList.push(newTempNode.data.id)
                }
                if (edgeIdList.indexOf(vm.newEdge.id) === -1 ){
                    edgeIdList.push(newTempEdge.data.id)
                }


                if (vm.isNewNode){
                    vm.nodes.push(newTempNode)
                    cytoscapeChart.add(newTempNode);
                }

                vm.edges.push(newTempEdge)
                cytoscapeChart.add(newTempEdge);

                vm.newEdge.id = ''
                vm.newVertex.id = ''
                vm.newVertex.description = ''

                cytoscapeChart.layout(cytoscapeChart.getConfig({}).layout);
            }

        }


        vm.addNewTask = function(form){
            if (form.$valid){

                if (taskIdList.indexOf(vm.newTask.id) > -1 ){
                    vm.errorAddNewTask.taskExist = true;
                    return;
                }else{
                    vm.errorAddNewTask.taskExist = false;
                }


                var newTempTask = {
                    classes : 'node task ' + vm.newTask.type,
                    data : {
                        id : vm.newTask.id,
                        sourceData : {}
                    },
                    sourceData : {
                        id : vm.newTask.id,
                        type : vm.newTask.type,
                        description : vm.newTask.description,
                        points : [],
                        belongToEdge : {}
                    }
                };

                vm.taskTypeList.forEach(function(type, typeIndex){
                    if (vm.newTask.type === type){
                        vm.currentEdge.sourceData[type].push(newTempTask);
                    }
                })

                vm.currentEdge.sourceData.allTask.push(newTempTask);
                newTempTask.sourceData.belongToEdge = vm.currentEdge.sourceData;
                newTempTask.data.sourceData = newTempTask.sourceData;

                if (taskIdList.indexOf(vm.newEdge.id) === -1 ){
                    taskIdList.push(newTempTask.data.id)
                }

                cytoscapeChart.getElementById( vm.currentEdge.id ).data(sourceData, vm.currentEdge.sourceData);


                vm.edges.forEach(function (edge, edgeIndex) {
                    if (edge.data.id === vm.currentEdge.id){
                        edge.sourceData = vm.currentEdge.sourceData
                        edge.data.sourceData = vm.currentEdge.sourceData
                    }
                })

            }
        }


        vm.convertDataArray = function () {
            vm.ouputData = formatter.cyArrayToRawArray(vm.nodes, vm.edges)
            console.log(vm.ouputData)
        }

        vm.convertDataObj = function () {
            vm.ouputData = formatter.rawArrayToObj(formatter.cyArrayToRawArray(vm.nodes, vm.edges))
            console.log(vm.ouputData)
        }



        var chartEventCallback= function(cy){

            cy.nodes('.node').qtip({
                content: function(){
                    return this.data().description;
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
                })
                this.drawChart();
            },
            drawChart : function(){
                var configChart = {
                    domId : 'chart',
                    userZoomingEnabled: false,
                    eventCB : chartEventCallback
                };

                cytoscapeChart = new flowChart2(formattedData, configChart);
                cytoscapeChart.center()
                cytoscapeChart.pan({
                    x: 10,
                    y: 10
                });

                console.log(cytoscapeChart.width())


                vm.flow.edges = formattedData.edges;
                vm.flow.nodes = formattedData.nodes;

                vertexIdList = formattedData.nodes.map(function(vertex, vertexIndex){
                    return vertex.data.id
                })
                edgeIdList = formattedData.edges.map(function(edge, edgeIndex){
                    return edge.data.id
                })
                taskIdList = formattedData.formattedSource.allTask.map(function(task, taskIndex){
                    return task.data.id
                })
            }
        };

        app.init();

    }



})(window, jQuery, cytoscape, angular);




