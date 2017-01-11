/**
 * Created by JinWYP on 23/12/2016.
 */


(function(window, jQuery, cytoscape, angular){
    'use strict';

    jQuery.ajaxSettings.async = false;






    var testData1 = {
        "initial"    : "START",
        "graphJar"   : "com.yimei.cflow.graph.money.MoneyGraphJar",
        "timeout"    : 100,
        "persistent" : true,

        "vertices" : {
            "START" : {
                "description" : "发起申请",
                "program"     : "import com.yimei.cflow.api.models.flow._; (state: State) => Seq(Arrow(\"V1\", Some(\"E1\")))"
            }
        },
        "edges"    : {
            "E1" : {
                "name"       : "E1",
                "begin"      : "V0",
                "end"        : "V1",
                "userTasks"  : [],
                "partGTasks" : [],
                "partUTasks" : [],
                "autoTasks"  : []
            }
        }
    };





    angular.module('flowApp', []);

    angular.module('flowApp').controller('vertexController', vertexController);


    function vertexController ($scope){
        var vm = this;

        var cytoscapeChart;
        var formattedData;
        var sourceData;

        var vertexIdList = [];
        var edgeIdList = [];
        var taskIdList = [];

        vm.ouputData = {};
        vm.selectType = 'node';
        vm.isNewNode = true;
        vm.taskTypeList = ['autoTasks', 'userTasks', 'partUTasks', 'partGTasks'];

        vm.errorAddNewVertex = {
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

        vm.currentVertex = {
            id : '',
            description : ''
        };
        vm.currentEdge = {
            id : '',
            source : '',
            target : '',
            sourceData : {}
        };
        vm.currentTask = {
            id : '',
            description : '',
            type : ''
        };

        vm.newVertex = {
            id : '',
            description : ''
        };
        vm.newEdge = {
            id : ''
        };
        vm.newTask = {
            id : '',
            description : '',
            type : ''
        };

        vm.globalConfig = {
            initial : 'start',  // 代表初始节点
            timeout: 100, // 超时时间配置(图的属性)
            flowType: '',  //流程类型
            persistent: false // 代表初始节点
        };

        vm.points = [];
        vm.groups = [];
        vm.edges = [];
        vm.nodes = [];

        vm.userTasks = [];
        vm.autoTasks = [];
        vm.partUTasks = [];
        vm.partGTasks = [];

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
            vm.ouputData = formatter.rawArrayToObj(vm.nodes, vm.edges)
            console.log(vm.ouputData)
        }



        var chartEventCallback= function(cy){

            cy.nodes('.node').qtip({
                content: function(){
                    return this.data().sourceData.description;
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
                vm.currentVertex.id = this.data().id;
                vm.currentVertex.description = this.data().sourceData.description;
                vm.selectType = 'node';
                $scope.$apply();
            })


            cy.on('click', 'edge', function(evt){

                vm.currentEdge.id = this.data().id;
                vm.currentEdge.source = this.data().source;
                vm.currentEdge.target = this.data().target;
                vm.currentEdge.sourceData = this.data().sourceData;
                vm.selectType = 'edge';
                $scope.$apply();
                console.log('edge:', this.data(), vm.currentEdge)
            })

        };



        var app = {
            init : function(){
                jQuery.getJSON('./json/data99.json', function(resultData){
                    formattedData = resultData;
                    
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

                sourceData = cytoscapeChart.formatterObjectToArray(formattedData)

                vm.edges = sourceData.edges;
                vm.nodes = sourceData.nodes;

                vertexIdList = sourceData.nodes.map(function(vertex, vertexIndex){
                    return vertex.data.id
                })
                edgeIdList = sourceData.edges.map(function(edge, edgeIndex){
                    return edge.data.id
                })
                taskIdList = sourceData.formattedSource.allTask.map(function(task, taskIndex){
                    return task.data.id
                })

                console.log(cytoscapeChart.width())
            }
        };

        app.init();

    }



})(window, jQuery, cytoscape, angular);




