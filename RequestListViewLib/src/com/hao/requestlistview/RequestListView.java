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

public class RequestListView extends ListView {

	private Context mContext;
	private LayoutInflater mInflater;

	private RelativeLayout mFooterView;// footer 布局
	private TextView mFooterTextView; // footer 文字 textview
	private ImageView mFooterProgress;

	private int mFooterBackgroundResource; // footer 背景 resource
	private int mFooterProgressDrawableResource;// footer 进度条 resource

	private String mFooterTextLoading = "Loading..."; // 加载的提示文字
	private String mFooterTextMore = "More..."; // 更多的提示文字

	public static final int TYPE_GET = 0; // get请求类型
	public static final int TYPE_POST = 1; // post请求类型

	private int mRequestType; // 请求类型

	private String mPageName = "page"; // 页码参数
	private int mPageCount; // 页数

	private OnCompleteListener mOnCompleteListener; // 完成的监听

	private String mUrl; // 请求地址
	private HashMap<String, String> mParams; // 请求参数

	private static AQuery aq; // aq对象

	private static AQuery getAQueryInstance(Context context) {
		if (aq == null) {
			aq = new AQuery(context);
			return aq;
		} else {
			return aq;
		}
	}

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
	 * 
	 * @Title: init
	 * @Description: 初始化操作
	 * @param
	 * @return void
	 * @throws
	 */
	private void init() {
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 得到footer布局
		mFooterView = (RelativeLayout) mInflater.inflate(R.layout.footer, this, false);
		// 为listview添加footer
		this.addFooterView(mFooterView);
		// 得到footer的文字控件
		mFooterTextView = (TextView) mFooterView.findViewById(R.id.footer_text);
		// 得到footer的进度条控件
		mFooterProgress = (ImageView) mFooterView.findViewById(R.id.footer_progress);
		// 为footer设置onclick监听
		mFooterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 设置加载中的文字提示
				mFooterTextView.setText(mFooterTextLoading);
				// 加载中，锁定按钮
				mFooterView.setClickable(false);
				if (mFooterProgressDrawableResource != 0) {
					// 如果传入了图片id，那么执行旋转
					mFooterProgress.setVisibility(View.VISIBLE);
					mFooterProgress.startAnimation(getAnim());
				}
				// 请求
				ajax();
			}
		});

	}

	/**
	 * 
	 * @Title: setFooterHint
	 * @Description: 设置footer的文字提示
	 * @param @param loading
	 * @param @param more
	 * @return void
	 * @throws
	 */
	public void setFooterHint(String loading, String more) {
		this.mFooterTextLoading = loading;
		this.mFooterTextMore = more;
	}

	/**
	 * 
	 * @Title: setRequestType
	 * @Description: 设置请求类型
	 * @param @param type
	 * @return void
	 * @throws
	 */
	public void setRequestType(int type) {
		this.mRequestType = type;
	}

	/**
	 * 
	 * @Title: setPageName
	 * @Description: 设置页码的参数名
	 * @param @param pageName
	 * @return void
	 * @throws
	 */
	public void setPageName(String pageName) {
		this.mPageName = pageName;
	}

	/**
	 * 
	 * @Title: setFooterBackgroundResource
	 * @Description: 设置footer背景的resource
	 * @param @param id
	 * @return void
	 * @throws
	 */
	public void setFooterBackgroundResource(int id) {
		this.mFooterBackgroundResource = id;
	}

	/**
	 * 
	 * @Title: setFooterProgressDrawableResource
	 * @Description: 设置footer进度条的resource
	 * @param @param id
	 * @return void
	 * @throws
	 */
	public void setFooterProgressDrawableResource(int id) {
		this.mFooterProgressDrawableResource = id;
	}

	/**
	 * 
	 * @Title: showResult
	 * @Description: 开始加载数据
	 * @param @param url
	 * @return void
	 * @throws
	 */
	public void showResult(String url) {
		this.mUrl = url;
		this.mParams = new HashMap<String, String>();
		getException();
		showFooterView();
		ajax();
	}

	/**
	 * 
	 * @Title: showResult
	 * @Description: 开始加载数据
	 * @param @param url
	 * @param @param params
	 * @return void
	 * @throws
	 */
	public void showResult(String url, HashMap<String, String> params) {
		this.mUrl = url;
		this.mParams = params;
		getException();
		showFooterView();
		ajax();
	}

	/**
	 * 
	 * @Title: getException
	 * @Description: 判断一些必填参数的异常
	 * @param
	 * @return void
	 * @throws
	 */
	private void getException() {
		if (mUrl == null || mUrl == "") {
			// 如果url参数不正确
			throw new RuntimeException("Url is null or empty. 请设置Url参数。");
		}
	}

	/**
	 * 
	 * @Title: showFooterView
	 * @Description: 正在加载时对的footer
	 * @param
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private void showFooterView() {
		if (mFooterProgressDrawableResource != 0) {
			// 如果进度条id正确，则加载旋转动画
			mFooterProgress.setVisibility(View.VISIBLE);
			Drawable drawable = getResources().getDrawable(mFooterProgressDrawableResource);
			mFooterProgress.setBackgroundDrawable(drawable);
			mFooterProgress.startAnimation(getAnim());
		}
		if (mFooterBackgroundResource != 0) {
			// 如果footer的背景id正确，则加载footer背景
			mFooterView.setBackgroundResource(mFooterBackgroundResource);
		}

		// 设置加载中的文字提示
		mFooterTextView.setText(mFooterTextLoading);
	}

	/**
	 * 
	 * @Title: ajax
	 * @Description: 根据type执行对应的请求方式
	 * @param
	 * @return void
	 * @throws
	 */
	private void ajax() {
		switch (mRequestType) {
		case TYPE_GET:
			doGet();
			break;
		case TYPE_POST:
			doPost();
			break;
		}
	}

	/**
	 * 
	 * @Title: doGet
	 * @Description: get
	 * @param
	 * @return void
	 * @throws
	 */
	private void doGet() {
		// 设置参数
		String url = mUrl + "?" + mPageName + "=" + ++mPageCount;
		if (mParams.size() != 0) {
			Set<Map.Entry<String, String>> mapEntrySet = mParams.entrySet();
			Iterator<Map.Entry<String, String>> mapEntryIterator = mapEntrySet.iterator();
			while (mapEntryIterator.hasNext()) {
				Map.Entry<String, String> entry = mapEntryIterator.next();
				url = url + "&" + entry.getKey() + "=" + entry.getValue();
			}
		}

		// 请求
		getAQueryInstance(mContext).ajax(url, String.class, 15 * 60 * 1000, new AjaxCallback<String>() {
			@Override
			public void callback(String url, String str, AjaxStatus status) {
				if (str != null) {
					// 成功
					onComplete(true, str);
				} else {
					// 失败
					onComplete(false, getRequestError(status.getCode()));
				}
				super.callback(url, str, status);
			}
		});
	}

	/**
	 * 
	 * @Title: doPost
	 * @Description: post
	 * @param
	 * @return void
	 * @throws
	 */
	private void doPost() {
		// 设置参数
		mParams.put(mPageName, ++mPageCount + "");
		// 请求
		getAQueryInstance(mContext).ajax(mUrl, mParams, String.class, new AjaxCallback<String>() {

			@Override
			public void callback(String url, String str, AjaxStatus status) {
				if (str != null) {
					// 成功
					onComplete(true, str);
				} else {
					// 失败
					onComplete(false, getRequestError(status.getCode()));
				}
				super.callback(url, str, status);
			}
		});
	}

	/**
	 * 
	 * @Title: getRequestError
	 * @Description: 得到请求后错误类型
	 * @param @param errorCode
	 * @param @return
	 * @return String
	 * @throws
	 */
	private String getRequestError(int errorCode) {
		switch (errorCode) {
		case AjaxStatus.AUTH_ERROR:
			return "AUTH_ERROR";
		case AjaxStatus.NETWORK_ERROR:
			return "NETWORK_ERROR";
		case AjaxStatus.TRANSFORM_ERROR:
			return "TRANSFORM_ERROR";
		default:
			return "ERROR";
		}
	}

	/**
	 * 
	 * @Title: onComplete
	 * @Description: 完成后执行的方法
	 * @param @param isSuccess
	 * @param @param res
	 * @return void
	 * @throws
	 */
	public void onComplete(boolean isSuccess, String res) {
		mFooterTextView.setText(mFooterTextMore);
		mFooterView.setClickable(true);
		mFooterProgress.setAnimation(null);
		mFooterProgress.setVisibility(View.GONE);
		if (mOnCompleteListener != null) {
			if (isSuccess) {
				mOnCompleteListener.onSuccess(res);
			} else {
				mPageCount = mPageCount - 1;
				mOnCompleteListener.onFail(res);
			}
		}
	}

	/**
	 * 
	 * @Title: setOnCompleteListener
	 * @Description: 设置监听
	 * @param @param l
	 * @return void
	 * @throws
	 */
	public void setOnCompleteListener(OnCompleteListener l) {
		this.mOnCompleteListener = l;
	}

	/**
	 * 
	 * @Title: ReListView.java
	 * @Package com.hao.requestlistview
	 * @Description: 监听接口
	 * @author YosonHao
	 * @E-mail haoyuexing@gmail.com
	 * @date 2014-4-28 下午11:39:38
	 */
	public interface OnCompleteListener {

		public void onSuccess(String str);

		public void onFail(String str);

	}

	/**
	 * 
	 * @Title: getAnim
	 * @Description: 360度旋转动画
	 * @param @param context
	 * @param @param progressDrawable
	 * @param @param progress
	 * @param @return
	 * @return RotateAnimation
	 * @throws
	 */
	public RotateAnimation getAnim() {
		RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		LinearInterpolator lin = new LinearInterpolator();
		anim.setInterpolator(lin);
		anim.setDuration(1000);
		anim.setRepeatCount(Animation.INFINITE);
		return anim;
	}

}
