package com.thissu.poetryalarm.Data

import com.chaopaikeji.poetryalert.Utility.DebugUtility
import java.util.*

/**
 * Created by apple on 2017/8/24.
 * 诗词的数据模型
 */
class PoetryModel {


    var name : String? = null
    var writer : String? = null
    var age : String? = null//!<年代
    var mainbody : String? = null//!<主体

    var question:String = ""
    var answer : String? = null//答案字符串
    var questionType : Int = 0//问题种类，0代表标题，1代表名字，2代表年代，3代表主体，大于3都代表主体。通过增加随机数的上限来提高主体问题的概率

    //问题设计出来之后，假设问题是第2句诗，那么bodyPart1就是第一句，bodyPart2就是第三和第四句，输入框刚好夹在中间。在这种情况下，暂时设计题目类型都是3
    var bodyPart1: String = ""//
    var bodyPart2: String = ""


    override fun toString(): String {
        return "PoetryModel(name=$name, writer=$writer, age=$age, mainbody=$mainbody)"
    }


    /**
     * 生成问题。生成问题后
     * */
    fun buildQuestion(){

        //生成问题类型
        val random = Random()
        questionType = random.nextInt(9)//生成从0到8的随机数，那么随机到诗的主体问题的概率就是2/3

        questionType = 3//先默认问题3

        DebugUtility.NSLog("questionType:${questionType}")

        if(0 == questionType){

            answer = name
        }
        else if (1 == questionType || 2 == questionType){//因为还没有加年代，所以年代先暂时也换成作者

            answer = writer
        }
        else if (2 == questionType){

            answer = age
        }
        else if (questionType >= 3){
            //主体的问题生成算法最复杂，因为要去掉其中的某一行，把去掉后的字符串赋值给question，答案赋值给answer
            val bodyArray = mainbody!!.split("\n")

            //随机一行做问题。
            var quesLine = random.nextInt(bodyArray.size)

            bodyPart1 = ""
            bodyPart2 = ""

            //遍历拼接问题
            for((index,value) in bodyArray.withIndex()){

                if(index < quesLine){
                    bodyPart1 = "$bodyPart1$value"
                }
                if(index > quesLine){
                    bodyPart2 = "$bodyPart2$value"
                }
                else{

                    answer = value
                }


                if(index < quesLine -1){
                    //假设问题在第4句，那么第1和第2句就要加回车，所以是小于4-1
                    bodyPart1 = "$bodyPart1\n"
                }
                else if(index >= quesLine +1 && index!=bodyArray.count()-1){
                    //假设问题行在第1句，那么第2和第3句就要有换行，且第4句没有换行。而问题是第2句的时候，只有第3句后面加换行。所以有了这个判断分支
                    bodyPart2 = "$bodyPart2\n"
                }

            }//end of for


        }
    }

}