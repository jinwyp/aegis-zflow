# cyto-fc

flow chart with cytoscape



### 变量模型 内存中保存以下临时变量

#### 全局变量

```
{
    initial: 'start'  代表初始节点
}
```

这三个属性是在创建图的对话框里填写就不变的
```
{
    timeout: 100, // 超时时间配置(图的属性)  
    flowType: '',  //流程类型
    persistent: true/false // 代表初始节点
}
```



 
#### 模型数据

1. points:  代表当前添加的所有数据点   -- 这个暂时没有设计

2. userTasks: 代表当前所有添加的用户任务, 结构为:
```
{
    "用户任务1":   {
         description:  "任务描述",
         points:  [ "A", "B", "C" ]
    },
    "用户任务2":   {
         description:  "任务描述",
         points:  [ "X", "Y", "Z" ]
    }
}

```


3. autoTasks: 代表当前添加的自动任务, 结构为:

```

{
    "自动任务1":   {
         description:  "任务描述",
         points:  [ "K1", "K2", "K3" ]
         program: ""
    },
    "自动任务2":   {
         description:  "任务描述",
         points:  [ "M1", "M2", "M3" ]
         program: ""
    }
}

```

4. partUTasks: 代表当前添加的参与方任务

```
{
    "参与方任务1": {
        guidKey:  "key",
        tasks: [ "用户任务1", "用户任务2" ]
    },
    "参与方任务2": {
        guidKey:  "key",
        tasks: [ "用户任务1", "用户任务2" ]
    }
}

```


5. partGTasks: 代表当前添加的参与方组任务

```

```
{
    "参与方任务1": {
        ggidKey:  "key",
        tasks: [ "用户任务1", "用户任务2" ]
    },
    "参与方任务2": {
        ggidKey:  "key",
        tasks: [ "用户任务1", "用户任务2" ]
    }
}


6. groups: 代表当前添加的所有任务组元素, 结构为:

```
{
     "group1": {
        autoTasks:   ["自动任务1", "自动任务2"],
        userTasks:   ["用户任务1", "用户任务2"],
        partUTasks:  ["参与方任务1"],
        partGTasks:  ["参与方组任务1"],
     }
     
     "group2" : {
     }
}

```


7. v2g: 代表当前添加的定点到任务组的连线   vertex  to group
```
{
   "V0": ["Group1"]
}
```



8. g2v: 代表当前添加的任务组到定点的连线
```
{
   "Group1": "V1"
}

```


9. vertices: 代表当前的所有图中的顶点
```
{
    "start" : {
       description: "启动",
       program: "",
    }

    "V0":  {
       description: "描述",
       program: "用户填写",
    },
    
    "V1":  {
       description: "描述",
       program: "用户填写",
    },
}
```



### 整个图的设计过程如下: 

1. 创建图, 需要在对话框里输入 timeout flowType persistent三个属性。 后面应该有三个属性的修改的属性编辑栏

2. 添加initial节点,  编辑initial节点的属性(name, description, program), 比如为("start", "启动", ""), 
然后添加{ "start" : { description: "启动", program: "" } }到vertices中
同时设置全局属性intial为此节点的name

添加定于添加initial类似, 只是不需要设置全局initial属性

3. 添加用户任务1, 编辑属性(name, description, points), 比如为: ("用户任务1", "用户提交表单", ["A", "B", "C"])
然后向userTasks临时变量中添加 { "用户任务1": { description: "提交用户表单", points: ["A", "B", "C"]} }

4. 添加自动任务与添加用户任务类似, 只是属性编辑多了个program的字符串

5. 添加参与方任务

6. 添加参与方组任务

7. 添加任务组:  选中 3, 4, 5, 6中的图形中元素, 选择菜单添加组, 图形中显示这些任务落在这个组的框里, 
编辑属性(name), 比如为"E1"
此时在groups数据结构里添加  { "E1": { "autoTasks": [], userTasks : [], partUTasks: [], partGTasks: [] } }
这里要依据每个任务的类型!!!

8. 选择某个顶点V0,  选择任务组Group1, 在菜单中点击添加连线。 图形中显示顶点与任务组连接好(这里有方向问题), 
此时会在v2g里添加  { "V0":  ["Group1"]}

9. 选择某个顶点V0,  选择任务组Group2, 在菜单中点击添加连线。 图形中显示顶点与任务组连接好(这里有方向问题), 
此时会在v2g里添加  { "V0":  ["Group2"]}, 此时v2g变为:  {  "V0":  ["Group1", "Group2"] }

10. 选择某个任务组Group1, 选择某个定点V1. 选在添加连线。 此时图形中显示好连线, 
此时在g2v中添加{ "Group1": "V1" }


#### 当所有的内存临时结构完成后, 调用我写的saveGraph函数就可以了。 

saveGraph(auto.......)  --->  json





