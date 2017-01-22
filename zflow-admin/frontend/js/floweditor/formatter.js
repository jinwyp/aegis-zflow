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

        cyArrayToRawArray : function (sourceGlobal, sourceNodes, sourceEdges, sourcePoints) {

            var globalConfig = {
                artifact: sourceGlobal.name,
                entry: "Yimei",
                groupId : sourceGlobal.groupId,
                initial    : 'Start',  // 代表初始节点
                timeout    : sourceGlobal.timeout,  // 超时时间配置(图的属性)
                persistent : sourceGlobal.persistent
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

                    userTasks: edge.data.userTasks,
                    autoTasks: edge.data.autoTasks,
                    partGTasks: edge.data.partGTasks,
                    partUTasks: edge.data.partUTasks,
                    allTasks: edge.data.allTask
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
                            fatherTask.guidKey = task.data.guidKey.id

                            task.data.tasks.forEach(function(subTask, subTaskIndex){
                                var newSubTask = {
                                    id : subTask.data.id,
                                    type : subTask.data.type,
                                    description : subTask.data.description,
                                    points : subTask.data.points
                                }
                                userTasksArray.push(newSubTask)
                                fatherTask.tasks.push(newSubTask)
                            })
                            partUTasksArray.push(fatherTask)
                        }

                        if (taskType === 'partGTasks') {
                            fatherTask.ggidKey = task.data.ggidKey.id

                            task.data.tasks.forEach(function(subTask, subTaskIndex){
                                var newSubTask = {
                                    id : subTask.data.id,
                                    type : subTask.data.type,
                                    description : subTask.data.description,
                                    points : subTask.data.points
                                }
                                userTasksArray.push(newSubTask)
                                fatherTask.tasks.push(newSubTask)
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
            var verticesObjectAll = {};
            var edgeObject = {};
            var pointObject = {};
            var pointObjectAll = {};

            var userTasksObject = {};
            var autoTasksObject = {};

            source.points.forEach(function(point, pointIndex){
                pointObject[point.id] = point.description
                pointObjectAll[point.id] = point
            })

            source.vertices.forEach(function(vertex, vertexIndex){
                verticesObject[vertex.id] = vertex.description
                verticesObjectAll[vertex.id] = vertex
            })


            source.edges.forEach(function(edge, edgeIndex){

                var newEdge = {
                    // "id" : edge.id,
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
                        "guidKey": task.data.guidKey.id,
                        "tasks": []
                    }

                    tempTask.tasks = task.data.tasks.map(function(subTask, subTaskIndex){
                        return subTask.data.id;
                    });

                    newEdge.partUTasks.push(tempTask)
                });


                edge.partGTasks.forEach(function(task, taskIndex){

                    var tempTask = {
                        "ggidKey": task.data.ggidKey.id,
                        "tasks": []
                    }

                    tempTask.tasks = task.data.tasks.map(function(subTask, subTaskIndex){
                        return subTask.data.id;
                    });

                    newEdge.partGTasks.push(tempTask)
                });

                edgeObject[edge.id] = newEdge
            })


            source.userTasks.forEach(function(userTask, userTaskIndex){
                var tempUserTask = {
                    // id : userTask.id,
                    // type : userTask.type,
                    description : userTask.description,
                    points : userTask.points.map(function (point, pointIndex) { return point.id })
                }

                userTasksObject[userTask.id] = tempUserTask
            })

            source.autoTasks.forEach(function(autoTask, autoTaskIndex){
                var tempAutoTask = {
                    // id : autoTask.id,
                    // type : autoTask.type,
                    description : autoTask.description
                }

                autoTasksObject[autoTask.id] = tempAutoTask
            })


            return {
                globalConfig : source.globalConfig,
                vertices : verticesObject,
                edges : edgeObject,
                points : pointObject,
                userTasks : userTasksObject,
                autoTasks : autoTasksObject
            }
        }
    }

    window.formatter = formatter;
})(window);