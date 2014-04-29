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

	private AQuery mAq;

	private RelativeLayout mFooterView;// footer 布局
	private TextView mFooterTextView; // footer 文字textview
	private ImageView mFooterProgress;

	private int mFooterBackground;
	private int mFooterProgressDrawable;

	private String mFooterTextLoading = "Loading...";
	private String mFooterTextMore = "More...";

	public static final int TYPE_GET = 0; // get请求类型
	public static final int TYPE_POST = 1; // post请求类型

	private int mRequestType;
	private String mPageName = "page";

	private OnCompleteListener mOnCompleteListener;

	private int mPageCount;

	private String mUrl;
	private HashMap<String, String> mParams;

	private static AQuery aq;

	private static AQuery getInstance(Context context) {
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
				ajax(mUrl, mParams, mPageName, ++mPageCount);
			}
		});

	}

	public void showResult(String url) {
		this.mUrl = url;
		this.mParams = new HashMap<String, String>();
		getException();
		showFooterView();
		ajax(mUrl, mParams, mPageName, ++mPageCount);
	}

	public void showResult(String url, HashMap<String, String> params) {
		this.mUrl = url;
		this.mParams = params;
		getException();
		showFooterView();
		ajax(mUrl, mParams, mPageName, ++mPageCount);
	}

	public void showResult(String url, HashMap<String, String> params, String pageName) {
		this.mUrl = url;
		this.mParams = params;
		this.mPageName = pageName;
		getException();
		showFooterView();
		ajax(mUrl, mParams, mPageName, ++mPageCount);
	}

	private void getException() {
		if (mUrl == null || mUrl == "") {
			// 如果url参数不正确
			throw new RuntimeException("Url is null or empty. 请设置Url参数。");
		}
	}

	private void showFooterView() {
		if (mFooterProgressDrawable != 0) {
			// 如果进度条id正确，则加载旋转动画
			mFooterProgress.setVisibility(View.VISIBLE);
			mFooterProgress.startAnimation(getAnim(mContext, mFooterProgressDrawable, mFooterProgress));
		}
		if (mFooterBackground != 0) {
			// 如果footer的背景id正确，则加载footer背景
			mFooterView.setBackgroundResource(mFooterBackground);
		}

		// 设置加载中的文字提示
		mFooterTextView.setText(mFooterTextLoading);
	}

	private void ajax(String url, HashMap<String, String> params, String mPageName, int pageCount) {
		switch (mRequestType) {
		case TYPE_GET:
			doGet(url, params, mPageName, pageCount);
			break;
		case TYPE_POST:
			doPost(url, params, mPageName, pageCount);
			break;
		}
	}

	private void doGet(String url, HashMap<String, String> params, String mPageName, int pageCount) {
		String url1 = url + "?" + mPageName + "=" + pageCount;
		if (params.size() != 0) {
			Set<Map.Entry<String, String>> mapEntrySet = params.entrySet();
			Iterator<Map.Entry<String, String>> mapEntryIterator = mapEntrySet.iterator();
			while (mapEntryIterator.hasNext()) {
				Map.Entry<String, String> entry = mapEntryIterator.next();
				url = url + "&" + entry.getKey() + "=" + entry.getValue();
			}
		}

		getInstance(mContext).ajax(url1, String.class, 15 * 60 * 1000, new AjaxCallback<String>() {
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

	private void doPost(String url, HashMap<String, String> params, String mPageName, int pageCount) {
		params.put(mPageName, pageCount + "");
		mAq.ajax(url, params, String.class, new AjaxCallback<String>() {

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
	 * 完成后执行的方法
	 * 
	 * @param res
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
	@SuppressWarnings("deprecation")
	public RotateAnimation getAnim(Context context, int progressDrawable, ImageView progress) {
		Drawable drawable = context.getResources().getDrawable(progressDrawable);
		progress.setBackgroundDrawable(drawable);
		RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		LinearInterpolator lin = new LinearInterpolator();
		anim.setInterpolator(lin);
		anim.setDuration(1000);
		anim.setRepeatCount(Animation.INFINITE);
		return anim;
	}

}
