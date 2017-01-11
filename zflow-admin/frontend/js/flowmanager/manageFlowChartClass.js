/**
 * Created by JinWYP on 23/12/2016.
 */


(function(window, $, cytoscape){


    var styleList = [
        {
            selector: 'node',
            style: {
                'shape': 'ellipse',
                'width': function(ele){
                    return Math.max(60, ele.data().id.length*16);
                },
                'height': 60,
                'background-color': 'gray',
                'color': '#fff',
                'content': 'data(id)',
                'text-valign': 'center',
                'text-halign': 'center',
                'font-size': '24px'
            }
        },

        {
            selector: 'node.isFirstNode',
            style: {
                'border-width': 3,
                'border-color': 'red'
            }
        },

        {
            selector: 'node.isProcessing',
            style: {
                'background-color': 'orange'
            }
        },

        {
            selector: 'node.isFinished',
            style: {
                'background-color': 'green'
            }
        },

        {
            selector: 'node.task',
            style: {
                'shape': 'roundrectangle',
                'width': function(ele){
                    return Math.max(150, ele.data().id.length*16);
                },
                'height': 80
            }
        },

        {
            selector: 'node.task.autoTasks',
            style: {
                'shape': 'star',
                'width': function(ele){
                    return Math.max(110, ele.data().id.length*20);
                },
                'height': function(ele){
                    var h = (110 < ele.data().id.length*20) ? (ele.data().id.length*16) : 94;
                    return h
                }

            }
        },

        {
            selector: 'node.task:selected',
            style: {
                'border-width': 3,
                'border-color': '#e86e81'
            }
        },

        {
            selector: 'edge',
            style: {
                'label': function(ele){
                    var total = 0;
                    var number = {
                        'autoTasks' : {
                            count : 0,
                            taskNameList : []
                        },
                        'userTasks' : {
                            count : 0,
                            taskNameList : []
                        },
                        'partUTasks' : {
                            count : 0,
                            taskNameList : []
                        },
                        'partGTasks' : {
                            count : 0,
                            taskNameList : []
                        }
                    }
                    ele.data().sourceData.allTask.forEach(function(task, taskIndex){

                        for (var property in number){

                            if(task.data.sourceData.type === property){
                                number[property].count  += 1
                                number[property].taskNameList.push(task.id)
                            }
                        }
                        ++total
                    })

                    var text = ele.data().id + ' 共' + total + '个任务\n'

                    for (var property in number){

                        if (number[property].count > 0){
                            text += property + number[property].count + '个\n'
                        }
                    }

                    return text;
                },

                'text-wrap': 'wrap',
                'font-size': '14px',
                'text-valign': 'center',
                'text-halign': 'center',
                'width': 20,
                'curve-style': 'bezier',
                'line-color': '#9dbaea',
                'target-arrow-shape': 'triangle',
                'target-arrow-color': '#9dbaea',
                'mid-source-arrow-shape': 'square',
                'mid-source-arrow-color': 'gray',
                'mid-source-arrow-fill': 'hollow',
                'mid-target-arrow-shape': 'square',
                'mid-target-arrow-color': 'gray',
                'mid-target-arrow-fill': 'hollow'

                // 'control-point-distances': '-30% 30%',
                // 'control-point-weights': '0 1'
            }
        },

        {
            selector: 'edge.toRight',
            style: {
                'curve-style': 'unbundled-bezier',
                'control-point-distances': '30% -30%',
                'control-point-weights': '0 1'
            }
        },

        {
            selector: 'edge.toLeft',
            style: {
                'curve-style': 'unbundled-bezier',
                'control-point-distances': '-30% 30%',
                'control-point-weights': '0 1'
            }
        },

        {
            selector: 'edge.isProcessing',
            style: {
                'line-color': 'orange',
                'target-arrow-color': 'orange'
            }
        },

        {
            selector: 'edge.isFinished',
            style: {
                'line-color': 'green',
                'target-arrow-color': 'green'
            }
        }
    ];


    var formatterObjectToArray = function(source){

        var tempNodesId = [];
        var result = {
            nodes : [],
            edges : [],
            formattedSource : {
                edges : [],
                vertices : [],
                allTask : [],
                userTasks : [],
                autoTasks : [],
                partUTasks : [],
                partGTasks : [],

                groupEdges : [],
                groupVertices : []
            }
        };

        function addStyleForNode (node){
            if (node.data.id === source.initial) {
                node.classes = node.classes + " isFirstNode "
            }
        }


        for (var property in source.edges){

            var currentEdge = source.edges[property];
            var tempEdge = {};
            var tempNode = {};

            //console.log(currentEdge)

            // 整理边
            tempEdge = {
                classes : 'edge',
                data : {
                    id : property,
                    source : currentEdge.begin,
                    target : currentEdge.end,
                    sourceData : {}
                },
                sourceData : {
                    id : currentEdge.name,
                    source : currentEdge.begin,
                    target : currentEdge.end,
                    userTasks : currentEdge.userTasks,
                    autoTasks : currentEdge.autoTasks,
                    partGTasks : currentEdge.partGTasks,
                    partUTasks : currentEdge.partUTasks,
                    allTask : []
                }
            };




            // 整理节点
            if (tempNodesId.indexOf(currentEdge.begin) === -1) {

                var tempNodeBegin = {
                    description : '',
                    program : ''
                };
                if (typeof source.vertices[currentEdge.begin] !== 'undefined' ){

                    if (typeof source.vertices[currentEdge.begin] === 'string'){
                        tempNodeBegin.description = source.vertices[currentEdge.begin];
                    }else {
                        tempNodeBegin = source.vertices[currentEdge.begin]
                    }
                }

                tempNode = {
                    classes : 'node',
                    data : {
                        id : currentEdge.begin,
                        sourceData : {}
                    },
                    sourceData : {
                        id : currentEdge.begin,
                        description : tempNodeBegin.description,
                        program : tempNodeBegin.program
                    }
                };

                addStyleForNode(tempNode)

                tempNode.data.sourceData = tempNode.sourceData;

                tempNodesId.push(currentEdge.begin)
                result.nodes.push(tempNode)
                result.formattedSource.vertices.push(tempNode)
            }


            if (tempNodesId.indexOf(currentEdge.end) === -1) {

                var tempNodeTarget = {
                    description : '',
                    program : ''
                };

                if (typeof source.vertices[currentEdge.end] !== 'undefined' ){

                    if (typeof source.vertices[currentEdge.end] === 'string'){
                        tempNodeTarget.description = source.vertices[currentEdge.end];
                    }else {
                        tempNodeTarget = source.vertices[currentEdge.end]
                    }

                }

                tempNode = {
                    classes : 'node',
                    data : {
                        id : currentEdge.end,
                        sourceData : {}
                    },
                    sourceData : {
                        id : currentEdge.end,
                        description : tempNodeTarget.description,
                        program : tempNodeTarget.program
                    }
                };

                addStyleForNode(tempNode)

                tempNode.data.sourceData = tempNode.sourceData;

                tempNodesId.push(currentEdge.end)
                result.nodes.push(tempNode)
                result.formattedSource.vertices.push(tempNode)
            }








            // 整理任务

            function generateNewTask (sourceAllData, edge, task, taskType, fatherTask){
                var newTask = {
                    classes : 'node task ' + taskType,
                    data : {
                        id : task,
                        sourceData : {}
                    },
                    sourceData : {
                        id : task,
                        type : taskType,
                        description : '',
                        points : [],

                        belongToEdge : {
                            id : edge.name,
                            source : edge.begin,
                            target : edge.end,
                            userTasks : edge.userTasks,
                            autoTasks : edge.autoTasks,
                            partGTasks : edge.partGTasks,
                            partUTasks : edge.partUTasks
                        }
                    }
                };

                if (taskType === 'userTasks' ||  taskType === 'autoTasks'){
                    newTask.sourceData.description = sourceAllData[taskType][task].description

                    source[taskType][task].points.forEach(function(point, pointIndex){
                        newTask.sourceData.points.push({
                            id : point,
                            description : sourceAllData.points[point]
                        })
                    })

                }

                if (taskType === 'partUTasks' || taskType === 'partGTasks'){
                    newTask.sourceData.description = sourceAllData['userTasks'][task].description

                    source['userTasks'][task].points.forEach(function(point, pointIndex){
                        newTask.sourceData.points.push({
                            id : point,
                            description : sourceAllData.points[point]
                        })
                    })
                }

                if (taskType === 'partUTasks') {
                    newTask.sourceData.guidKey = fatherTask.guidKey
                }

                if (taskType === 'partGTasks') {
                    newTask.sourceData.ggidKey = fatherTask.ggidKey
                }

                newTask.data.sourceData = newTask.sourceData;

                return newTask
            }

            var taskTypeList = ['autoTasks', 'userTasks', 'partUTasks', 'partGTasks'];
            taskTypeList.forEach(function(taskType, taskTypeIndex){

                if (currentEdge[taskType] && currentEdge[taskType].length > 0){

                    currentEdge[taskType].forEach(function(task, taskIndex){
                        var tempTask = {};


                        if (taskType === 'userTasks' ||  taskType === 'autoTasks') {

                            tempTask = generateNewTask(source, currentEdge, task, taskType)

                            if (taskType === 'userTasks') result.formattedSource.userTasks.push(tempTask);
                            if (taskType === 'autoTasks') result.formattedSource.autoTasks.push(tempTask);

                            tempEdge.sourceData.allTask.push(tempTask)
                            result.formattedSource.allTask.push(tempTask);
                        }


                        if (taskType === 'partUTasks' || taskType === 'partGTasks') {

                            if (task.tasks && task.tasks.length > 0){
                                task.tasks.forEach(function(subTask, subTaskIndex){

                                    tempTask = generateNewTask(source, currentEdge, subTask, taskType, task)


                                    if (taskType === 'partUTasks') result.formattedSource.partUTasks.push(tempTask)
                                    if (taskType === 'partGTasks') result.formattedSource.partGTasks.push(tempTask)

                                    tempEdge.sourceData.allTask.push(tempTask)
                                    result.formattedSource.allTask.push(tempTask);
                                })
                            }

                        }
                    })
                }
            })


            // 整理边
            tempEdge.data.sourceData = tempEdge.sourceData
            result.edges.push(tempEdge)
            result.formattedSource.edges.push(tempEdge)

        }

        console.log(result)
        return result;

    };


    var flowChart2 = function (data, config){
        return this.init(data, config);
    };

    var getConfig = function (config){
        var cfg = Object.assign({
            container: document.getElementById(config.domId),

            layout: {
                name: 'dagre',
                fit: false,
                minLen : function( edge ){
                    return 4; // number of ranks to keep between the source and target of the edge
                },
                edgeWeight : function( edge ){
                    return 1;  // higher weight edges are generally made shorter and straighter than lower weight edges
                },
                nodeSep: 200,
                edgeSep: 300
            },
            style: styleList,
            elements: [],

            boxSelectionEnabled: false,
            autounselectify: false,
            userZoomingEnabled: true,
            userPanningEnabled: true,
            autoungrabify: false,

            minZoom: 1, //http://js.cytoscape.org/#core
            maxZoom: 1,
            zoom : 1,

            textureOnViewport : false
            // pixelRatio : 1.0


        }, config);

        return cfg
    }

    flowChart2.prototype.init =  function(sourceData, config){

        var cfg = getConfig(config);
        cfg.elements = formatterObjectToArray(sourceData) ;

        var cy = cytoscape(cfg);

        cfg.eventCB(cy);

        cy.formatterObjectToArray = formatterObjectToArray;
        cy.getConfig = getConfig;

        return cy;
    };


    flowChart2.prototype.formatterObjectToArray = formatterObjectToArray;


    window.flowChart2 = flowChart2;


})(window, jQuery, cytoscape)