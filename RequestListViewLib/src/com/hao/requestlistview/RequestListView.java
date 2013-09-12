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
	private RelativeLayout mFooterView;
	private TextView mMore;

	private String mUrl;
	private String mUrlPageParaName = "page";

	private int mPageCount;

	private AQuery mAq;

	private HashMap<String, String> mParaMap;

	private OnCompleteListener mOnCompleteListener;

	private String mMoreString = "More...";
	private String mLoadingString = "onLoading...";

	private ImageView mProgress;

	private int mProgressDrawable;
	private int mFooterBackground;

	private String mResult;

	private static final int REQUEST_SUCCESS = 1;
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

	private void init() {
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFooterView = (RelativeLayout) mInflater.inflate(R.layout.footer, this,
				false);
		this.addFooterView(mFooterView);
		mMore = (TextView) mFooterView.findViewById(R.id.more);
		mProgress = (ImageView) mFooterView.findViewById(R.id.progress);
		mFooterView.setOnClickListener(this);
		mParaMap = new HashMap<String, String>();
	}

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
		mMore.setText(mLoadingString);
		mFooterView.setClickable(false);
		if (mProgressDrawable != 0) {
			mProgress.setVisibility(View.VISIBLE);
			mProgress.startAnimation(getAnim());
		}
		ajax(++mPageCount);
	}

	public void showResult() {
		if (mUrl == null || mUrl == "") {
			throw new RuntimeException("Url is null or empty. 请设置Url参数。");
		}
		if (mProgressDrawable != 0) {
			mProgress.setVisibility(View.VISIBLE);
			mProgress.startAnimation(getAnim());
		}
		if (mFooterBackground!=0) {
			mFooterView.setBackgroundResource(mFooterBackground);
		}
		mMore.setText(mLoadingString);
		mAq = new AQuery(mContext);
		ajax(++mPageCount);
	}

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
					mResult = str;
					onComplete(REQUEST_SUCCESS);
				} else {
					onComplete(REQUEST_FAIL);
				}
				super.callback(url, str, status);
			}
		});
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public void setUrlPageParaName(String urlPageParaName) {
		this.mUrlPageParaName = urlPageParaName;
	}

	public void setUrlPara(String k, String v) {
		this.mParaMap.put(k, v);
	}

	public void setFooterHint(String beforLoading, String loading) {
		this.mMoreString = beforLoading;
		this.mLoadingString = loading;
	}

	public void setFooterBackground(int background) {
		mFooterBackground=background;
	}

	public void setProgressDrawable(int drawable) {
		mProgressDrawable = drawable;
	}

	public String getResult() {
		return mResult;
	}

	public void requestCancel() {
		mAq.ajaxCancel();
	}

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

	public void setOnCompleteListener(OnCompleteListener l) {
		mOnCompleteListener = l;
	}

	public interface OnCompleteListener {

		public void onSuccess(String str);

		public void onFail();

	}

}
