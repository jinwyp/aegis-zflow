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
            selector: 'node:selected',
            style: {
                "border-width": "3px",
                "border-color": "#AAD8FF",
                "background-color": "#77828C",
                "border-opacity": "1"
            }
        },

        {
            selector: 'node.isFirstNode',
            style: {
                'background-color': '#555'
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
                    ele.data().allTask.forEach(function(task, taskIndex){

                        for (var property in number){

                            if(task.data.type === property){
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
            selector: 'edge:selected',
            style: {
                'line-color': '#d0b7d5',
                'target-arrow-color': '#d0b7d5'
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

                points : []
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
                    userTasks : [],
                    autoTasks : [],
                    partGTasks : [],
                    partUTasks : [],
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
                        description : tempNodeBegin.description,
                        program : tempNodeBegin.program
                    }
                };

                addStyleForNode(tempNode)

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
                        description : tempNodeTarget.description,
                        program : tempNodeTarget.program
                    }
                };

                addStyleForNode(tempNode)

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
                        type : taskType,
                        description : '',
                        points : [],

                        belongToEdge : {
                            id : edge.name,
                            source : edge.begin,
                            target : edge.end
                        }
                    }
                };

                if (taskType === 'userTasks' ||  taskType === 'autoTasks'){
                    newTask.data.description = sourceAllData[taskType][task].description

                    source[taskType][task].points.forEach(function(point, pointIndex){
                        var tempPoint = {
                            classes : 'point ',
                            data : {
                                id : point,
                                description : sourceAllData.points[point],
                                JSONSchema : {
                                    "title": "Example Schema",
                                    "type": "object",
                                    "properties": {
                                        "firstName": {
                                            "type": "string"
                                        },
                                        "lastName": {
                                            "type": "string"
                                        },
                                        "age": {
                                            "description": "Age in years",
                                            "type": "integer",
                                            "minimum": 0
                                        }
                                    },
                                    "required": ["firstName", "lastName"]
                                }
                            }
                        }
                        newTask.data.points.push(tempPoint)
                        result.formattedSource.points.push(tempPoint)
                    })
                }

                if (taskType === 'partUTasks' || taskType === 'partGTasks'){
                    newTask.data.description = sourceAllData['userTasks'][task].description
                    newTask.data.type = 'userTasks'

                    source['userTasks'][task].points.forEach(function(point, pointIndex){
                        var tempPoint = {
                            classes : 'point ',
                            data : {
                                id : point,
                                description : sourceAllData.points[point],
                                JSONSchema : {
                                    "title": "Example Schema",
                                    "type": "object",
                                    "properties": {
                                        "firstName": {
                                            "type": "string"
                                        },
                                        "lastName": {
                                            "type": "string"
                                        },
                                        "age": {
                                            "description": "Age in years",
                                            "type": "integer",
                                            "minimum": 0
                                        }
                                    },
                                    "required": ["firstName", "lastName"]
                                }
                            }
                        }
                        newTask.data.points.push(tempPoint)
                        result.formattedSource.points.push(tempPoint)

                    })
                }

                if (taskType === 'partUTasks') {
                    newTask.data.guidKey = fatherTask.guidKey
                }

                if (taskType === 'partGTasks') {
                    newTask.data.ggidKey = fatherTask.ggidKey
                }

                return newTask
            }


            // 整理任务 开始
            var taskTypeList = ['autoTasks', 'userTasks', 'partUTasks', 'partGTasks'];
            taskTypeList.forEach(function(taskType, taskTypeIndex){

                if (currentEdge[taskType] && currentEdge[taskType].length > 0){

                    currentEdge[taskType].forEach(function(task, taskIndex){
                        var tempTask = {};

                        if (taskType === 'userTasks' ||  taskType === 'autoTasks') {

                            tempTask = generateNewTask(source, currentEdge, task, taskType)

                            if (taskType === 'userTasks') {
                                tempEdge.data.userTasks.push(tempTask)
                                result.formattedSource.userTasks.push(tempTask);
                            }
                            if (taskType === 'autoTasks'){
                                tempEdge.data.autoTasks.push(tempTask)
                                result.formattedSource.autoTasks.push(tempTask);
                            }

                            tempEdge.data.allTask.push(tempTask)
                            result.formattedSource.allTask.push(tempTask);
                        }


                        if (taskType === 'partUTasks' || taskType === 'partGTasks') {

                            if (task.tasks && task.tasks.length > 0){

                                var tempFatherTask = {
                                    classes : 'node task ' + taskType,
                                    data : {
                                        id : '',
                                        guidKey : '',
                                        ggidKey : '',
                                        type : taskType,
                                        description : '',
                                        tasks : [],

                                        belongToEdge : {
                                            id : property,
                                            source : currentEdge.begin,
                                            target : currentEdge.end
                                        }
                                    }
                                }


                                task.tasks.forEach(function(subTask, subTaskIndex){
                                    tempTask = generateNewTask(source, currentEdge, subTask, taskType, task)
                                    tempFatherTask.data.tasks.push(tempTask)
                                })

                                if (taskType === 'partUTasks') {
                                    tempFatherTask.data.id = task.guidKey
                                    tempFatherTask.data.guidKey = task.guidKey
                                }

                                if (taskType === 'partGTasks') {
                                    tempFatherTask.data.id = task.ggidKey
                                    tempFatherTask.data.ggidKey = task.ggidKey
                                }


                                if (taskType === 'partUTasks') {
                                    tempEdge.data.partUTasks.push(tempFatherTask)
                                    result.formattedSource.partUTasks.push(tempFatherTask)
                                }

                                if (taskType === 'partGTasks') {
                                    tempEdge.data.partGTasks.push(tempFatherTask)
                                    result.formattedSource.partGTasks.push(tempFatherTask)
                                }

                                tempEdge.data.allTask.push(tempFatherTask)
                                result.formattedSource.allTask.push(tempFatherTask);
                            }

                        }
                    })
                }
            })


            // 整理边
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
            userZoomingEnabled: false,
            userPanningEnabled: true,
            autoungrabify: false,

            minZoom: 1, //http://js.cytoscape.org/#core
            maxZoom: 2,
            zoom : 1,

            textureOnViewport : false
            // pixelRatio : 1.0


        }, config);

        return cfg
    }

    flowChart2.prototype.init =  function(sourceData, config){

        var cfg = getConfig(config);
        cfg.elements = sourceData ;

        var cy = cytoscape(cfg);

        cfg.eventCB(cy);

        cy.getConfig = getConfig;

        return cy;
    };


    window.flowChart2 = flowChart2;
    window.cytoscapeFormatterObjectToArray = formatterObjectToArray;


})(window, jQuery, cytoscape)