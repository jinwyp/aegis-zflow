/**
 * Created by JinWYP on 23/12/2016.
 */


(function(window, jQuery, cytoscape, angular){
    'use strict';

    jQuery.ajaxSettings.async = false;

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
        ]
    };





    angular.module('flowApp', []);

    angular.module('flowApp').controller('vertexController', vertexController);


    function vertexController ($scope){
        var vm = this;

        var cytoscapeChart;
        var formattedData;

        var nodeIdList = [];
        var edgeIdList = [];
        var taskIdList = [];

        vm.ouputData = {};

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
                    if (vm.newTask.type === type){
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

            }
        }


        vm.changeTaskEditType = function(type){
            vm.taskEditType = type;
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

                nodeIdList = formattedData.nodes.map(function(vertex, vertexIndex){
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




