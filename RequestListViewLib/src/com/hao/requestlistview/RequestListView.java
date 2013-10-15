/**
 * Copyright (c) 2013, Yoson Hao 郝悦兴 (haoyuexing@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hao.requestlistview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.hao.requestlistviewlib.R;

public class RequestListView extends ListView implements OnClickListener,
		OnScrollListener {
	// 正常状态
	private final static int NONE_PULL_REFRESH = 0;
	// 进入下拉刷新状态
	private final static int ENTER_PULL_REFRESH = 1;
	// 进入松手刷新状态
	private final static int OVER_PULL_REFRESH = 2;
	// 松手后反弹和加载状态
	private final static int EXIT_PULL_REFRESH = 3;
	// 记录刷新状态
	private int mPullRefreshState = 0;

	// 刷新请求成功标记
	private static final int REQUEST_REFRESH_SUCCESS = 0;
	// 请求成功的标记
	private static final int REQUEST_MORE_SUCCESS = 1;
	// 请求失败的标记
	private static final int REQUEST_FAIL = 2;

	// 请求刷新的类型
	private static final int TYPE_REFRESH = 1;
	// 请求更多的类型
	private static final int TYPE_MORE = 2;
	// 请求类型状态
	private int mReqType;

	private Context mContext;
	private LayoutInflater mInflater;

	// 移动的距离
	private float mMoveY;
	// 按下时的Y轴
	private float mDownY;

	private int mCurrentScrollState;

	// footer 布局
	private RelativeLayout mFooterView;
	// footer 文字
	private TextView mFooterMore;

	// header 布局
	private RelativeLayout mHeaderView;
	// header 刷新时间
	private TextView mHeaderTime;
	// header 刷新提示
	private TextView mHeaderHint;
	// header 高度
	private int mHeaderHeight;

	private int mFirstVisibleItem;

	// header刷新时间
	SimpleDateFormat mSimpleDateFormat;

	// 请求地址
	private String mUrl;
	// 请求页数的参数名
	private String mUrlPageParaName = "page";

	// 请求的页码
	private int mPageCount;

	// aq对象
	private AQuery mAq;

	// 请求的其余参数
	private HashMap<String, String> mParaMap;

	// 请求完成的监听
	private OnCompleteListener mOnCompleteListener;

	// 默认footer文字
	private String mMoreString = "More...";
	// 默认footer加载中的文字
	private String mLoadingString = "onLoading...";

	// 显示进度条的控件
	private ImageView mFooterProgress;

	// 进度图片id
	private int mFooterProgressDrawable;
	// footer背景id
	private int mFooterBackground;

	// 返回的字符串
	private String mResult;

	public RequestListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public RequestListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public RequestListView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	/**
	 * 初始化
	 */
	@SuppressLint("SimpleDateFormat")
	private void init() {
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 得到footer布局
		mFooterView = (RelativeLayout) mInflater.inflate(R.layout.footer, this,
				false);
		// 得到header布局
		mHeaderView = (RelativeLayout) mInflater.inflate(R.layout.header, this,
				false);
		// 为listview添加footer
		this.addFooterView(mFooterView);
		// 为listview添加header
		this.addHeaderView(mHeaderView);
		// 得到footer的文字控件
		mFooterMore = (TextView) mFooterView.findViewById(R.id.more);
		// 得到footer的进度条控件
		mFooterProgress = (ImageView) mFooterView.findViewById(R.id.progress);
		// 得到header刷新时间控件;
		mHeaderTime = (TextView) mHeaderView.findViewById(R.id.header_time);
		// 得到header文字提示
		mHeaderHint = (TextView) mHeaderView.findViewById(R.id.header_hint);
		// 为footer设置onclick监听
		mFooterView.setOnClickListener(this);
		// new一个hashmap存放其他后续的参数
		mParaMap = new HashMap<String, String>();
		// 设置header时间
		mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		mHeaderTime.setText(mSimpleDateFormat.format(new Date()));

		// 设置OnScroll监听
		setOnScrollListener(this);
		// 得到header高度
		measureView(mHeaderView);
		mHeaderHeight = mHeaderView.getMeasuredHeight();

		setHeaderPadding(-mHeaderHeight);

	}

	private void setHeaderPadding(int padding) {
		this.setPadding(this.getPaddingLeft(), padding, this.getPaddingRight(),
				this.getPaddingBottom());
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setSelection(1);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		setSelection(1);
	}

	/**
	 * 测量控件
	 * 
	 * @param child
	 */
	@SuppressWarnings("deprecation")
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 得到360旋转的动画
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private RotateAnimation getAnim() {
		Drawable drawable = getResources().getDrawable(mFooterProgressDrawable);
		mFooterProgress.setBackgroundDrawable(drawable);
		RotateAnimation anim = new RotateAnimation(0f, 360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		LinearInterpolator lin = new LinearInterpolator();
		anim.setInterpolator(lin);
		anim.setDuration(1000);
		anim.setRepeatCount(Animation.INFINITE);
		return anim;
	}

	@Override
	public void onClick(View arg0) {
		// 点击后更改textview的文字
		mFooterMore.setText(mLoadingString);
		// 加载中，锁定按钮
		mFooterView.setClickable(false);
		if (mFooterProgressDrawable != 0) {
			// 如果传入了图片id，那么执行旋转
			mFooterProgress.setVisibility(View.VISIBLE);
			mFooterProgress.startAnimation(getAnim());
		}
		// 发起请求
		ajax(++mPageCount, TYPE_MORE);
	}

	public void showResult() {
		if (mUrl == null || mUrl == "") {
			// 如果url参数不正确
			throw new RuntimeException("Url is null or empty. 请设置Url参数。");
		}
		if (mFooterProgressDrawable != 0) {
			// 如果进度条id正确，则加载旋转动画
			mFooterProgress.setVisibility(View.VISIBLE);
			mFooterProgress.startAnimation(getAnim());
		}
		if (mFooterBackground != 0) {
			// 如果footer的背景id正确，则加载footer背景
			mFooterView.setBackgroundResource(mFooterBackground);
		}
		// 设置加载中的文字提示
		mFooterMore.setText(mLoadingString);
		mAq = new AQuery(mContext);
		// 请求
		ajax(++mPageCount, TYPE_MORE);
	}

	/**
	 * 获取数据
	 * 
	 * @param pageCount
	 *            页码
	 */
	private void ajax(int pageCount, int type) {
		// 拼接url
		mReqType = type;
		String url = mUrl + "?" + mUrlPageParaName + "=" + pageCount;
		if (mParaMap.size() != 0) {
			// 如果有参数那么拼接到连接上
			Set<Map.Entry<String, String>> mapEntrySet = mParaMap.entrySet();
			Iterator<Map.Entry<String, String>> mapEntryIterator = mapEntrySet
					.iterator();
			while (mapEntryIterator.hasNext()) {
				Map.Entry<String, String> entry = mapEntryIterator.next();
				url = url + "&" + entry.getKey() + "=" + entry.getValue();
			}
		}

		mAq.ajax(url, String.class, new AjaxCallback<String>() {
			@Override
			public void callback(String url, String str, AjaxStatus status) {
				if (str != null) {
					// 成功
					mResult = str;
					switch (mReqType) {
					case TYPE_REFRESH:
						// 刷新请求成功
						onComplete(REQUEST_REFRESH_SUCCESS);
						break;
					case TYPE_MORE:
						// 更多请求成功
						onComplete(REQUEST_MORE_SUCCESS);
						break;
					}
				} else {
					// 失败
					onComplete(REQUEST_FAIL);
				}
				super.callback(url, str, status);
			}
		});
	}

	/**
	 * 设置url
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.mUrl = url;
	}

	/**
	 * 设置页码参数名称
	 * 
	 * @param urlPageParaName
	 */
	public void setUrlPageParaName(String urlPageParaName) {
		this.mUrlPageParaName = urlPageParaName;
	}

	/**
	 * 设置需要请求的其他参数
	 * 
	 * @param k
	 * @param v
	 */
	public void setUrlPara(String k, String v) {
		this.mParaMap.put(k, v);
	}

	/**
	 * 设置footer提示
	 * 
	 * @param beforLoading
	 *            加载前
	 * @param loading
	 *            加载中
	 */
	public void setFooterHint(String beforLoading, String loading) {
		this.mMoreString = beforLoading;
		this.mLoadingString = loading;
	}

	/**
	 * 设置footer背景
	 * 
	 * @param background
	 */
	public void setFooterBackground(int background) {
		mFooterBackground = background;
	}

	/**
	 * 设置进度条图片
	 * 
	 * @param drawable
	 */
	public void setProgressDrawable(int drawable) {
		mFooterProgressDrawable = drawable;
	}

	/**
	 * 得到请求后的结果
	 * 
	 * @return
	 */
	public String getResult() {
		return mResult;
	}

	/**
	 * 取消请求
	 */
	public void requestCancel() {
		mAq.ajaxCancel();
	}

	/**
	 * 完成后执行的方法
	 * 
	 * @param res
	 */
	private void onComplete(int res) {

		// 刷新状态更新为-正常状态
		mPullRefreshState = NONE_PULL_REFRESH;
		if (mReqType == TYPE_MORE) {
			// 设置footer文字
			mFooterMore.setText(mMoreString);
			// 成功后更多按钮可用
			mFooterView.setClickable(true);
			// 取消进度条动画
			mFooterProgress.setAnimation(null);
			// 进度条设置为不可见
			mFooterProgress.setVisibility(View.GONE);
		}
		if (mReqType == TYPE_REFRESH) {
			setHeaderPadding(-mHeaderHeight);
			// 刷新后的提示为下拉刷新
			mHeaderHint.setText("下拉刷新");
			// 刷新后的时间为当前时间
			mHeaderTime.setText(mSimpleDateFormat.format(new Date()));
		}
		if (mOnCompleteListener != null) {
			switch (res) {
			// 请求完成后执行对应的监听
			case REQUEST_REFRESH_SUCCESS:
				mOnCompleteListener.onRefreshSuccess(mResult);
				break;
			case REQUEST_MORE_SUCCESS:
				mOnCompleteListener.onMoreSuccess(mResult);
				break;
			case REQUEST_FAIL:
				mOnCompleteListener.onFail();
				break;
			}
		}
	}

	/**
	 * 设置监听
	 * 
	 * @param l
	 */
	public void setOnCompleteListener(OnCompleteListener l) {
		mOnCompleteListener = l;
	}

	/**
	 * 监听接口
	 */
	public interface OnCompleteListener {

		public void onRefreshSuccess(String str);

		public void onMoreSuccess(String str);

		public void onFail();

	}

	/**
	 * onScroll监听
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
				&& firstVisibleItem == 0
				&& (mHeaderView.getBottom() >= 0 && mHeaderView.getBottom() < mHeaderHeight)) {
			// 进入且仅进入下拉刷新状态
			if (mPullRefreshState == NONE_PULL_REFRESH) {
				mPullRefreshState = ENTER_PULL_REFRESH;
			}
		} else if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
				&& firstVisibleItem == 0
				&& (mHeaderView.getBottom() >= mHeaderHeight)) {
			// 下拉达到界限，进入松手刷新状态
			if (mPullRefreshState == ENTER_PULL_REFRESH
					|| mPullRefreshState == NONE_PULL_REFRESH) {
				mPullRefreshState = OVER_PULL_REFRESH;
				// 为下拉1/3折扣效果记录开始位置
				mDownY = mMoveY;
				// 显示松手刷新
				mHeaderHint.setText("松手刷新");
			}
		} else if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
				&& firstVisibleItem != 0) {
			// 不刷新了
			if (mPullRefreshState == ENTER_PULL_REFRESH) {
				mPullRefreshState = NONE_PULL_REFRESH;
			}
		} else if (mCurrentScrollState == SCROLL_STATE_FLING
				&& firstVisibleItem == 0) {
			// 飞滑状态，不能显示出header，也不能影响正常的飞滑
			// 只在正常情况下才纠正位置
			if (mPullRefreshState == NONE_PULL_REFRESH) {
				setSelection(1);
			}
		}
	}

	/**
	 * onScroll状态监听
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mCurrentScrollState = scrollState;
	}

	/**
	 * 手势
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			if (mPullRefreshState == OVER_PULL_REFRESH) {
				// 下拉状态的情况下 松手listview归位
				this.setPadding(this.getPaddingLeft(), 0,
						this.getPaddingRight(), this.getPaddingBottom());
				// 刷新状态更新为-正在刷新
				mPullRefreshState = EXIT_PULL_REFRESH;
				mHeaderHint.setText("正在刷新");
				ajax(1, TYPE_REFRESH);
				mPageCount = 1;
			}
			if (mPullRefreshState == ENTER_PULL_REFRESH) {
				// 第一项置顶？？？
				setSelection(1);
				mPullRefreshState = NONE_PULL_REFRESH;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mDownY = ev.getY();
			if (mPullRefreshState == NONE_PULL_REFRESH
					&& mFirstVisibleItem == 0) {
				setHeaderPadding(0);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mMoveY = ev.getY();
			if (mPullRefreshState == OVER_PULL_REFRESH) {
				// 如果状态为松手刷新状态
				this.setPadding(this.getPaddingLeft(),
						(int) ((mMoveY - mDownY) / 3), this.getPaddingRight(),
						this.getPaddingBottom());
			}
			break;
		}

		return super.onTouchEvent(ev);
	}
}
