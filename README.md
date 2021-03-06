# 葫芦娃课程设计

一、游戏运行
-------

### 1.1、程序入口

从main函数开始执行。

### 1.2、类的设计

人物有个基类，creature，其派生出两个类，Calabash与Monster。
人物类都聚集在field类中。

### 1.3、人物位置更新

#### 1.3.1 更新方式

每个人物执行时，会通过对field类的函数接口，找出距离其最近的敌人位置，并通过敌人的位置来更新自己的位置。
不能使游戏中两个人物同时走动，否则会造成两者碰撞，却没有被主程序发现，或者两者走到同一个格子里。所以我这里使用lock机制来实现，每次仅会有一个人物更新其位置信息。

#### 1.3.2 碰撞检测

当两个人物走到一起时，如果他们是朋友，就让这种走法不合法，如果他们是敌人，则这里发生了战斗，一定会有一方被打败，从地图中消失。
当地图中一方完全消失，则游戏结束。

二、游戏回放
-------

### 2.1 操作方式

当用户按下空格键时，则开始录制当前的游戏，到out.txt文件中。
注意：当录制完毕后，需要按下S键来结束录制进程。
当游戏未开始或者已经结束时，用户可以按下L键，选择ans.txt文件加载，就可以回放 ans.txt文件中的游戏过程。

### 2.2 具体实现

这里我是使用一个单独的线程Monitor实现的，可以把其想象为是上帝，每次控制战场发生一个动作：
1. move动作。葫芦娃或者小怪从一个坐标走到另一个坐标。
2. die动作。 标志一个人物的死亡。

三、Thread线程管理
-------

在field类初始化过程中，把人物初始化，并使用一个线程数组，在用户按下SPACE键后，就通过当前已经有的人物信息，每个人物生成一个线程，并发地多线程运行。
每次一个人物死亡时，并不会杀死这个线程，而是会把其从当前战场上清理掉，只有当重新开始游戏时，才会对上次游戏中所有的线程进行阻断，并且建立新的线程。
