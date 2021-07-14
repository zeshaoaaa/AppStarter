package org.jay.appstarter.sort

import java.util.*

/**
 * 有向无环图的拓扑排序算法
 */
class Graph(

    // 顶点数
    private val mVerticalCount: Int

) {

    // 邻接表
    private val mAdj: Array<MutableList<Int>?> = arrayOfNulls(mVerticalCount)

    init {
        for (i in 0 until mVerticalCount) {
            mAdj[i] = ArrayList<Int>()
        }
    }

    /**
     * 添加边
     *
     * @param u from
     * @param v to
     */
    fun addEdge(u: Int, v: Int) {
        mAdj[u]?.add(v)
    }

    /**
     * 拓扑排序
     */
    fun topologicalSort(): Vector<Int> {

        val indegree = IntArray(mVerticalCount)

        //初始化所有点的入度数量
        for (i in 0 until mVerticalCount) {
            val temp = mAdj[i] as ArrayList<Int>
            for (node in temp) {
                indegree[node]++
            }
        }

        val queue: Queue<Int> = LinkedList()

        //找出所有入度为0的点
        for (i in 0 until mVerticalCount) {
            if (indegree[i] == 0) {
                queue.add(i)
            }
        }
        var cnt = 0
        val topOrder = Vector<Int>()
        while (!queue.isEmpty()) {
            val u = queue.poll()
            topOrder.add(u)

            val list = mAdj[u] ?: continue

            // 找到该点（入度为0）的所有邻接点
            for (node in list) {

                // //把这个点的入度减一，如果入度变成了0，那么添加到入度0的队列里
                if (--indegree[node] == 0) {

                    // 添加节点到队列中
                    queue.add(node)
                }
            }
            cnt++
        }

        // 检查是否有环，理论上拿出来的点的次数和点的数量应该一致，如果不一致，说明有环
        check(cnt == mVerticalCount) { "Exists a cycle in the graph" }
        return topOrder
    }


}