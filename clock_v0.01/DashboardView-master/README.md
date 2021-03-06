# DashboardView
Android刻度盘控件

![image](https://github.com/shubowen/DashboardView/blob/master/dashboard.gif)

**支持属性**：
    
    <declare-styleable name="DashboardView">
            <!--长线条的长度-->
            <attr name="longLineLength" format="dimension"/>
            <!--长线条的宽度-->
            <attr name="longLineWidth" format="dimension"/>
            <!--长线条的颜色-->
            <attr name="longLineColor" format="color"/>
    
            <!--外圆线条的宽-->
            <attr name="circleStrokeWidth" format="dimension"/>
            <!--外圆线条的颜色-->
            <attr name="circleStrokeColor" format="color"/>
    
            <!--短线条的长度-->
            <attr name="shortLineLength" format="dimension"/>
            <!--短线条的宽度-->
            <attr name="shortLineWidth" format="dimension"/>
            <!--短线条的颜色-->
            <attr name="shortLineColor" format="color"/>
    
            <!--线条和外圆之间的距离-->
            <attr name="insetWidth" format="dimension"/>
    
            <!--显示最大数值-->
            <attr name="maxNum" format="float"/>
    
            <!--长线条的角标-->
            <attr name="longLineIndex" format="integer"/>
    
            <!--文字的颜色-->
            <attr name="android:textColor"/>
            <!--文字的大小-->
            <attr name="android:textSize"/>
            <!--文字的距线条的距离-->
            <attr name="textMargin" format="dimension"/>
    
            <!--指针的宽-->
            <attr name="pointerWidth" format="dimension"/>
            <!--指针的颜色-->
            <attr name="pointerColor" format="color"/>
            <!--指针和外圆的间距-->
            <attr name="pointerInset" format="dimension"/>
    
            <!--滑动灵敏度,最大值为1,，默认为0.1-->
            <attr name="sensitivity" format="float"/>
    
            <!--中心圆半径-->
            <attr name="centerCircleRadius" format="dimension"/>
            <attr name="centerCircleColors" format="string"/>
    
            <!--是否安整数显示-->
            <attr name="asInteger" format="boolean"/>
    
            <!--刻度的个数-->
            <attr name="spaceNum" format="integer"/>
    
            <!--起始值的偏移量-->
            <attr name="valueOffset" format="float"/>
    
        </declare-styleable>
