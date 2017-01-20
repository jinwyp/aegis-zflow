/**
 * Created by JinWYP on 09/01/2017.
 */


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





var singleVertex  = {
    "id"          : "START",
    "description" : "发起申请",
    "program"     : "import com.yimei.cflow.api.models.flow._; (state: State) => Seq(Arrow(\"V1\", Some(\"E1\")))"
};


var edge      = {
    "id"         : "E1",
    "name"       : "E1",
    "begin"      : "V0",
    "end"        : "V1",
    "userTasks"  : [],
    "partGTasks" : [],
    "partUTasks" : [],
    "autoTasks"  : []
}


var point = {
    "id"          : "P1",
    "description" : "申请成功率"
};


(function (window) {

    'use strict';

    var formatter = {

        cyArrayToRawArray : function (sourceNodes, sourceEdges, sourcePoints) {

            var globalConfig = {
                initial    : 'START',  // 代表初始节点
                graphJar   : "com.yimei.cflow.graph.money.MoneyGraphJar",
                timeout    : 100,  // 超时时间配置(图的属性)
                flowType   : '',   //流程类型
                persistent : true
            };

            var verticesArray = [];
            var edgeArray = [];

            var userTasksArray = [];
            var autoTasksArray = [];
            var partUTasksArray = [];
            var partGTasksArray = [];


            sourceNodes.forEach(function (node, nodeIndex) {
                var tempNode = {
                    id : node.data.id,
                    description : node.data.description,
                    program : node.data.program
                }
                verticesArray.push(tempNode)
            })


            sourceEdges.forEach(function (edge, edgeIndex) {
                var tempEdge = {
                    id : edge.data.id,
                    name : edge.data.id,
                    begin : edge.data.source,
                    end : edge.data.target,
                    "userTasks": edge.data.userTasks,
                    "autoTasks": edge.data.autoTasks,
                    "partGTasks": edge.data.partGTasks,
                    "partUTasks": edge.data.partUTasks,
                    "allTasks": edge.data.allTask
                }

                edgeArray.push(tempEdge)

                var taskTypeList = ['autoTasks', 'userTasks', 'partUTasks', 'partGTasks'];
                taskTypeList.forEach(function(taskType, taskTypeIndex){
                    tempEdge[taskType].forEach(function(task, taskIndex){

                        var fatherTask = {
                            id : task.data.id,
                            guidKey : '',
                            ggidKey : '',
                            tasks : []
                        }

                        var newTask = {
                            id : task.data.id,
                            type : task.data.type,
                            description : task.data.description,
                            points : []
                        }


                        if (taskType === 'userTasks') {
                            newTask.points = task.data.points
                            userTasksArray.push(newTask)
                        }

                        if (taskType === 'autoTasks') {
                            newTask.points = task.data.points
                            autoTasksArray.push(newTask)
                        }

                        if (taskType === 'partUTasks') {
                            fatherTask.guidKey = task.data.guidKey

                            task.data.tasks.forEach(function(subTask, subTaskIndex){
                                fatherTask.tasks.push({
                                    id : subTask.data.id,
                                    type : subTask.data.type,
                                    description : subTask.data.description,
                                    points : subTask.data.points
                                })
                            })
                            partUTasksArray.push(fatherTask)
                        }

                        if (taskType === 'partGTasks') {
                            fatherTask.ggidKey = task.data.ggidKey

                            task.data.tasks.forEach(function(subTask, subTaskIndex){
                                fatherTask.tasks.push({
                                    id : subTask.data.id,
                                    type : subTask.data.type,
                                    description : subTask.data.description,
                                    points : subTask.data.points
                                })
                            })

                            partGTasksArray.push(fatherTask)
                        }

                    })
                })


            })


            return {
                globalConfig : globalConfig,
                vertices : verticesArray,
                edges : edgeArray,
                points : sourcePoints,
                userTasks : userTasksArray,
                autoTasks : autoTasksArray,
                partGTasks : partGTasksArray,
                partUTasks : partUTasksArray
            }

        },



        rawArrayToObj : function (source) {

            var verticesObject = {};
            var edgeObject = {};
            var pointObject = {};

            var userTasksObject = {};
            var autoTasksObject = {};
            var partUTasksObject = {};
            var partGTasksObject = {};

            source.points.forEach(function(point, pointIndex){
                pointObject[point.id] = point
            })

            source.vertices.forEach(function(vertex, vertexIndex){
                verticesObject[vertex.id] = vertex
            })

            source.edges.forEach(function(edge, edgeIndex){

                var newEdge = {
                    "id" : edge.id,
                    "name": edge.name,
                    "begin": edge.begin,
                    "end": edge.end,
                    "userTasks": [],
                    "autoTasks": [],
                    "partUTasks": [],
                    "partGTasks": []
                }

                newEdge.userTasks = edge.userTasks.map(function(task, taskIndex){
                    return task.data.id;
                });

                newEdge.autoTasks = edge.autoTasks.map(function(task, taskIndex){
                    return task.data.id;
                });

                edge.partUTasks.forEach(function(task, taskIndex){

                    var tempTask = {
                        "guidKey": task.data.guidKey,
                        "tasks": []
                    }

                    tempTask.tasks = task.data.tasks.map(function(subTask, subTaskIndex){
                        return subTask.data.id;
                    });

                    newEdge.partUTasks.push(tempTask)
                });


                edge.partGTasks.forEach(function(task, taskIndex){

                    var tempTask = {
                        "ggidKey": task.data.ggidKey,
                        "tasks": []
                    }

                    tempTask.tasks = task.data.tasks.map(function(subTask, subTaskIndex){
                        return subTask.data.id;
                    });

                    newEdge.partGTasks.push(tempTask)
                });

                edgeObject[edge.id] = newEdge
            })


            source.userTasks.forEach(function(task, taskIndex){
                userTasksObject[task.id] = task
            })

            source.autoTasks.forEach(function(task, taskIndex){
                autoTasksObject[task.id] = task
            })


            source.partUTasks.forEach(function(task, taskIndex){

                var tempSubTaskIDList = task.tasks.map(function(subTask, subTaskIndex){
                    return subTask.id;
                });
                task.tasks = tempSubTaskIDList

                partUTasksObject[task.id] = task
            })

            source.partGTasks.forEach(function(task, taskIndex){

                var tempSubTaskIDList = task.tasks.map(function(subTask, subTaskIndex){
                    return subTask.id;
                });
                task.tasks = tempSubTaskIDList

                partGTasksObject[task.id] = task
            })


            return {
                globalConfig : source.globalConfig,
                vertices : verticesObject,
                edges : edgeObject,
                points : pointObject,
                userTasks : userTasksObject,
                autoTasks : autoTasksObject,
                partGTasks : partGTasksObject,
                partUTasks : partUTasksObject
            }
        }
    }

    window.formatter = formatter;
})(window);