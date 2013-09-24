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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.hao.requestlistviewlib.R;

public class RequestListView extends ListView implements OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;

	// footer 布局
	private RelativeLayout mFooterView;
	// footer 文字
	private TextView mMore;

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
	private ImageView mProgress;

	// 进度图片id
	private int mProgressDrawable;
	// footer背景id
	private int mFooterBackground;

	// 返回的字符串
	private String mResult;

	// 请求成功的标记
	private static final int REQUEST_SUCCESS = 1;
	// 请求失败的标记
	private static final int REQUEST_FAIL = 2;

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
	private void init() {
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 得到footer布局
		mFooterView = (RelativeLayout) mInflater.inflate(R.layout.footer, this,
				false);
		// 为listview添加footer
		this.addFooterView(mFooterView);
		// 得到footer的文字控件
		mMore = (TextView) mFooterView.findViewById(R.id.more);
		// 得到footer的进度条控件
		mProgress = (ImageView) mFooterView.findViewById(R.id.progress);
		// 为footer设置onclick监听
		mFooterView.setOnClickListener(this);
		// new一个hashmap存放其他后续的参数
		mParaMap = new HashMap<String, String>();
	}

	/**
	 * 得到360旋转的动画
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private RotateAnimation getAnim() {
		Drawable drawable = getResources().getDrawable(mProgressDrawable);
		mProgress.setBackgroundDrawable(drawable);
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
		//点击后更改textview的文字
		mMore.setText(mLoadingString);
		//加载中，锁定按钮
		mFooterView.setClickable(false);
		if (mProgressDrawable != 0) {
			//如果传入了图片id，那么执行旋转
			mProgress.setVisibility(View.VISIBLE);
			mProgress.startAnimation(getAnim());
		}
		//发起请求
		ajax(++mPageCount);
	}

	public void showResult() {
		if (mUrl == null || mUrl == "") {
			//如果url参数不正确
			throw new RuntimeException("Url is null or empty. 请设置Url参数。");
		}
		if (mProgressDrawable != 0) {
			//如果进度条id正确，则加载旋转动画
			mProgress.setVisibility(View.VISIBLE);
			mProgress.startAnimation(getAnim());
		}
		if (mFooterBackground != 0) {
			//如果footer的背景id正确，则加载footer背景
			mFooterView.setBackgroundResource(mFooterBackground);
		}
		//设置加载中的文字提示
		mMore.setText(mLoadingString);
		mAq = new AQuery(mContext);
		//请求
		ajax(++mPageCount);
	}

	/**
	 * 获取数据
	 * @param pageCount 页码
	 */
	private void ajax(int pageCount) {
		String url = mUrl + "?" + mUrlPageParaName + "=" + pageCount;
		if (mParaMap.size() != 0) {
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
					//成功
					mResult = str;
					onComplete(REQUEST_SUCCESS);
				} else {					
					//失败
					onComplete(REQUEST_FAIL);
				}
				super.callback(url, str, status);
			}
		});
	}

	/**
	 * 设置url
	 * @param url
	 */
	public void setUrl(String url) {
		this.mUrl = url;
	}

	/**
	 * 设置页码参数名称
	 * @param urlPageParaName
	 */
	public void setUrlPageParaName(String urlPageParaName) {
		this.mUrlPageParaName = urlPageParaName;
	}

	/**
	 * 设置需要请求的其他参数
	 * @param k
	 * @param v
	 */
	public void setUrlPara(String k, String v) {
		this.mParaMap.put(k, v);
	}

	/**
	 * 设置footer提示
	 * @param beforLoading 加载前
	 * @param loading 加载中
	 */
	public void setFooterHint(String beforLoading, String loading) {
		this.mMoreString = beforLoading;
		this.mLoadingString = loading;
	}

	/**
	 * 设置footer背景
	 * @param background
	 */
	public void setFooterBackground(int background) {
		mFooterBackground = background;
	}

	/**
	 * 设置进度条图片
	 * @param drawable
	 */
	public void setProgressDrawable(int drawable) {
		mProgressDrawable = drawable;
	}

	/**
	 * 得到请求后的结果
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
	 * @param res
	 */
	private void onComplete(int res) {
		mMore.setText(mMoreString);
		mFooterView.setClickable(true);
		mProgress.setAnimation(null);
		mProgress.setVisibility(View.GONE);
		if (mOnCompleteListener != null) {
			switch (res) {
			case REQUEST_SUCCESS:
				mOnCompleteListener.onSuccess(mResult);
				break;
			case REQUEST_FAIL:
				mOnCompleteListener.onFail();
				break;
			}
		}
	}

	/**
	 * 设置监听
	 * @param l
	 */
	public void setOnCompleteListener(OnCompleteListener l) {
		mOnCompleteListener = l;
	}

	/**
	 * 监听接口
	 */
	public interface OnCompleteListener {

		public void onSuccess(String str);

		public void onFail();

	}

}
