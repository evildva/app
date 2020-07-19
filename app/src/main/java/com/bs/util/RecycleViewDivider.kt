package com.bs.util

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecycleViewDivider(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {
    private var mPaint: Paint? = null
    private var mDivider: Drawable?
    private var mDividerHeight = 2 //分割线高度，默认为1px
    private val mOrientation //列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
            : Int

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    constructor(context: Context, orientation: Int, drawableId: Int) : this(context, orientation) {
        mDivider = ContextCompat.getDrawable(context, drawableId)
        mDividerHeight = mDivider!!.getIntrinsicHeight()
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    constructor(context: Context, orientation: Int, dividerHeight: Int, dividerColor: Int) : this(
        context,
        orientation
    ) {
        mDividerHeight = dividerHeight
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.setColor(dividerColor)
        mPaint!!.setStyle(Paint.Style.FILL)
    }

    //获取分割线尺寸
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
        outRect.set(0, 0, 0, mDividerHeight)
    }

    //绘制分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    //绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left: Int = parent.getPaddingLeft() //获取分割线的左边距，即RecyclerView的padding值
        val right: Int = parent.getMeasuredWidth() - parent.getPaddingRight() //分割线右边距
        val childSize: Int = parent.getChildCount()
        //遍历所有item view，为它们的下方绘制分割线
        for (i in 0 until childSize) {
            val child: View = parent.getChildAt(i)
            val layoutParams: RecyclerView.LayoutParams =
                child.getLayoutParams() as RecyclerView.LayoutParams
            val top: Int = child.getBottom() + layoutParams.bottomMargin
            val bottom = top + mDividerHeight
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
            }
        }
    }

    //绘制纵向 item 分割线
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top: Int = parent.getPaddingTop()
        val bottom: Int = parent.getMeasuredHeight() - parent.getPaddingBottom()
        val childSize: Int = parent.getChildCount()
        for (i in 0 until childSize) {
            val child: View = parent.getChildAt(i)
            val layoutParams: RecyclerView.LayoutParams =
                child.getLayoutParams() as RecyclerView.LayoutParams
            val left: Int = child.getRight() + layoutParams.rightMargin
            val right = left + mDividerHeight
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
            }
        }
    }

    companion object {
        private val ATTRS = intArrayOf(R.attr.listDivider) //使用系统自带的listDivider
    }

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     * @param orientation 列表方向
     */
    init {
        require(!(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL)) { "请输入正确的参数！" }
        mOrientation = orientation
        val a: TypedArray =
            context.obtainStyledAttributes(ATTRS) //使用TypeArray加载该系统资源
        mDivider = a.getDrawable(0)
        a.recycle() //缓存
    }
}